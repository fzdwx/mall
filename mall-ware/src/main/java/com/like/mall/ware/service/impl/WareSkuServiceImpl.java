package com.like.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.To.mq.OrderTo;
import com.like.mall.common.To.mq.StockLocked;
import com.like.mall.common.exception.NoStockException;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.utils.R;
import com.like.mall.common.vo.WareSkuLockVo;
import com.like.mall.ware.dao.WareSkuDao;
import com.like.mall.ware.entity.WareOrderTaskDetailEntity;
import com.like.mall.ware.entity.WareOrderTaskEntity;
import com.like.mall.ware.entity.WareSkuEntity;
import com.like.mall.ware.feign.OrderFeignService;
import com.like.mall.ware.feign.ProductFeignService;
import com.like.mall.ware.service.WareOrderTaskService;
import com.like.mall.ware.service.WareSkuService;
import com.like.mall.ware.vo.SkuStockVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    ProductFeignService productFeignService;
    @Resource
    WareOrderTaskService wareOrderTaskService;
    @Resource
    WareOrderTaskDetailServiceImpl wareOrderTaskDetailService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Resource
    private OrderFeignService orderFeignService;
    @Resource
    private WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> query = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            query.eq("sku_id", skuId);

        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            query.eq("ware_id", wareId);

        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.新增库存
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (list.size() == 0) {
            WareSkuEntity wareSku = new WareSkuEntity();
            wareSku.setSkuId(skuId);
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSku.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
            }
            //         todo    wareSku.setSkuName();
            save(wareSku);
        } else {
            // 2.添加
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuStockVo> skuHasStock(List<Long> skuIds) {
        return skuIds.stream()
                .map(i -> {
                    SkuStockVo skuStockVo = new SkuStockVo();
                    Long count = this.baseMapper.getSkuStock(i);
                    skuStockVo.setSkuId(i);
                    skuStockVo.setHasStock(count != null && count > 0);
                    return skuStockVo;
                }).collect(Collectors.toList());
    }

    /**
     * 订单锁定库存
     */
    @Override
    @Transactional(rollbackFor = NoStockException.class)
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // 保存库存工作单，追溯
        WareOrderTaskEntity task = new WareOrderTaskEntity();
        task.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(task);

        // 1.按照下单的收货地址，找到一个就近的仓库，锁定库存
        // 找到每个商品在哪个仓库的库存足够，在锁定
        List<WareSkuLockVo.OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> stockList = locks.stream().map(s -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(s.getSkuId());
            // 查询这个商品在哪里有库存
            stock.setWareId(listWareIdHasSkuStock(s.getSkuId(), s.getCount()));
            stock.setCount(s.getCount());
            return stock;
        }).collect(Collectors.toList());
        // 2.锁定库存
        for (SkuWareHasStock hasStock : stockList) {
            List<Long> ware = hasStock.getWareId();
            if (ware == null || ware.size() <= 0) { // 库存不足
                throw new NoStockException(hasStock.getSkuId());
            }
            boolean skuIsStock = false;
            for (Long wareId : ware) {
                boolean b = baseMapper.lockSkuStock(hasStock.skuId, wareId, hasStock.getCount());
                if (b) {  // 锁定成功
                    skuIsStock = true;
                    // 保存库存详情单
                    WareOrderTaskDetailEntity taskDetail =
                            new WareOrderTaskDetailEntity(null, hasStock.skuId, null, hasStock.getCount(), task.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(taskDetail);
                    // 发送消息，告诉MQ消息-库存锁定成功
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked",
                            new StockLocked(task.getId(), taskDetail.getId(), taskDetail.getSkuId(), taskDetail.getSkuNum(), taskDetail.getWareId(), taskDetail.getLockStatus()));
                    break;
                } else { // 失败
                    skuIsStock = false;
                }
            }
            // 循环完后，如果有一次锁定失败就结束
            if (!skuIsStock) {
                throw new NoStockException(hasStock.getSkuId());
            }
        }
        return true;
    }

    /**
     * 库存自动解锁
     * 1.下单成功，锁定也成功，但是order服务后面的应用调用出错，执行解锁
     * 2.锁库存失败
     * - 当前订单是否存在
     * - 没有 -> 必须解锁
     * - 有
     * - 查看订单状态
     * - 取消 -> 解锁库存
     * - 没取消，不解锁
     */
    @Override
    public void unLock(StockLocked info) {
        // 1.查询数据库关于这个订单的锁定库存信息,如果有->解锁
        if (wareOrderTaskDetailService.getById(info.getDetailId()) != null) {
            // 2.查看订单号
            String orderSn = wareOrderTaskService.getById(info.getId()).getOrderSn();
            // 3.远程查看订单状态 【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
            Integer orderStatus = orderFeignService.getOrderStatus(orderSn);
            if (orderStatus == null || orderStatus == 4 || orderStatus == 1) { //订单不存在 取消，解锁库存
                unLockStock(info.getDetailId(), info.getWareId(), info.getSkuId(), info.getSkuNum());
            }
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，昆虫消息优先到期
     * 导致卡顿的订单永远不能解锁库存
     */
    @Transactional
    @Override
    public void unLockStock(OrderTo info) {
        String orderSn = info.getOrderSn();
        WareOrderTaskEntity orderTask = wareOrderTaskService.getOrderByOrderSn(orderSn);// 查一下最新状态
        Long id = orderTask.getId();
        List<WareOrderTaskDetailEntity> tasks = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity task : tasks) {
            unLockStock(task.getId(),task.getWareId(), task.getSkuId(), task.getSkuNum());
        }
    }

    protected void unLockStock(Long taskDetailId, Long wareId, Long skuId, Integer skuNum) {
        // 1.库存解锁
        wareSkuDao.unLock(wareId, skuId, skuNum);
        // 2.更新状态
        WareOrderTaskDetailEntity newTask = new WareOrderTaskDetailEntity();
        newTask.setTaskId(taskDetailId);
        newTask.setLockStatus(2); // 已解锁
        wareOrderTaskDetailService.updateById(newTask);
    }

    private List<Long> listWareIdHasSkuStock(Long skuId, Integer count) {
        System.out.println(skuId);
        System.out.println(count);
        return baseMapper.listWareIdHasSkuStock(skuId, count);
    }


    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer count;
    }
}
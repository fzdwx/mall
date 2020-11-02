package com.like.mall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.AttrAttrgroupRelationDao;
import com.like.mall.product.dao.AttrDao;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.service.AttrService;
import com.like.mall.product.vo.AttrVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao aarDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
                                          );

        return new PageUtils(page);
    }

    @Override
    @Transactional // 事务
    public void saveAttr(AttrVo attr) {
        System.out.println("attr = " + attr);
        // 1.保存基本数据
        AttrEntity ae = new AttrEntity();
        BeanUtil.copyProperties(attr, ae);
        save(ae);

        // 2.保存关联关系
        AttrAttrgroupRelationEntity aarEntity = AttrAttrgroupRelationEntity
                .builder()
                .attrGroupId(attr.getAttrGroupId())
                .attrId(getByNameAndCatelogId(ae).getAttrId())
                .build();
        aarDao.insert(aarEntity);
    }

    /**
     * 根据name 和 catelog id 查询 attr
     */
    public AttrEntity getByNameAndCatelogId(AttrEntity ae) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        query.eq("attr_name", ae.getAttrName())
             .eq("catelog_id", ae.getCatelogId())
             .select("attr_id");
        return baseMapper.selectOne(query);
    }

}
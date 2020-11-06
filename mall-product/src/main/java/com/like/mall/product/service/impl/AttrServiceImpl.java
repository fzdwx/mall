package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.AttrAttrGroupRelationDao;
import com.like.mall.product.dao.AttrDao;
import com.like.mall.product.dao.AttrGroupDao;
import com.like.mall.product.dao.CategoryDao;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.entity.AttrGroupEntity;
import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.AttrAttrgroupRelationService;
import com.like.mall.product.service.AttrService;
import com.like.mall.product.service.CategoryService;
import com.like.mall.product.vo.AttrRespVo;
import com.like.mall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.like.mall.common.constant.ProductConstant.AttrEnum.ATTR_TYPE_BASE;
import static com.like.mall.common.constant.ProductConstant.AttrEnum.ATTR_TYPE_SALE;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrGroupRelationDao aarDao;
    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Resource
    AttrGroupDao agDao;
    @Resource
    CategoryDao cDao;
    @Resource
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>());

        return new PageUtils(page);
    }

    @Override
    @Transactional // 事务
    public void saveAttr(AttrVo attr) {
        // 1.保存基本数据
        AttrEntity ae = new AttrEntity();
        BeanUtils.copyProperties(attr, ae);
        save(ae);
        // 2.保存关联关系
        if (attr.getAttrType() == ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity aarEntity = AttrAttrgroupRelationEntity
                    .builder()
                    .attrGroupId(attr.getAttrGroupId())
                    .attrId(ae.getAttrId())
                    .build();
            aarDao.insert(aarEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(
                        attrType) ? ATTR_TYPE_BASE.getCode() : ATTR_TYPE_SALE.getCode()); // 0-销售属性，1-基本属性，2-既是销售属性又是基本属性
        // 1.组装查询条件
        if (catelogId != 0) {
            query.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            // attr_id attr_name
            query.and(q -> {
                q.eq("attr_id", key)
                        .or()
                        .like("attr_name", key);
            });
        }

        // 2.执行查询
        IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params), query);

        // 3.封装返回
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attrRespVoList =
                records.stream()
                        .map(attrEntity -> {
                            AttrRespVo vo = new AttrRespVo();
                            BeanUtils.copyProperties(attrEntity, vo);
                            // 3.1.获取当前attr对应的分组信息
                            AttrAttrgroupRelationEntity aare = null;
                            if ("base".equalsIgnoreCase(attrType)) {
                                aare = aarDao.selectOne(
                                        // 查询属性分组组名和分类
                                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq(
                                                "attr_id",
                                                vo.getAttrId()));
                            }
                            if (aare != null) {
                                AttrGroupEntity attrGroup = agDao.selectById(
                                        aare.getAttrGroupId());
                                vo.setGroupName(attrGroup.getAttrGroupName());
                            }
                            // 3.2.获取当前attr对应的分类信息
                            CategoryEntity category = cDao.selectById(vo.getCatelogId());
                            if (category != null) {
                                vo.setCatelogName(category.getName());
                            }
                            return vo;
                        })
                        .collect(Collectors.toList());
        pageUtils.setList(attrRespVoList);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo vo = new AttrRespVo();
        // 1.获取attr信息
        AttrEntity attr = getById(attrId);
        BeanUtils.copyProperties(attr, vo);

        // 2.获取分组信息
        if (attr.getAttrType() == ATTR_TYPE_BASE.getCode()) { // 如果是基本属性才需要查询属性分组信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = aarDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null) {
                vo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = agDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    vo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 3.获取分类完整路径
        Long[] catelogPath = categoryService.findCatelogPath(attr.getCatelogId());
        vo.setCatelogPath(catelogPath);
        return vo;
    }

    @Override
    public void updateDetail(AttrVo attr) {
        AttrEntity attrE = new AttrEntity();
        BeanUtils.copyProperties(attr, attrE);
        // 1.先更新attr
        updateById(attrE);
        if (attr.getAttrType() == ATTR_TYPE_BASE.getCode()) { // 如果是基本属性才需要修改属性分组信息
            // 2.修改分组关联
            AttrAttrgroupRelationEntity relation =
                    AttrAttrgroupRelationEntity.builder()
                            .attrId(attr.getAttrId())
                            .attrGroupId(attr.getAttrGroupId())
                            .build();
            // 修改，防止該屬性沒有選擇分組
            attrAttrgroupRelationService.saveOrUpdate(relation, new QueryWrapper<AttrAttrgroupRelationEntity>().eq(
                    "attr_id", attr.getAttrId()));
        }
    }

    @Override
    public List<AttrEntity> getAttrGroupById(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> ens = aarDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        // 收集所有id
        List<Long> ids =
                ens.stream()
                        .map(AttrAttrgroupRelationEntity::getAttrId)
                        .collect(Collectors.toList());

        if (ids.isEmpty()) {  // 添加判断
            return null;
        }
        return listByIds(ids);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        // 1.当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = agDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 2.当前分类中所有属性分组
        List<AttrGroupEntity> otherAttrGroup = agDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq(catelogId != null, "catelog_id", catelogId));
        List<Long> otherAttrGroupIds = otherAttrGroup.stream()
                .map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList()); // 收集属性分组id

        // 3.这些分组所关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in(!otherAttrGroupIds.isEmpty(), "attr_group_id", otherAttrGroupIds));
        List<Long> attrIds = relationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList()); // 收集关联过属性分组的属性
        // 4.查询出：当前分类，没有关联过分组的属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq(catelogId != null, "catelog_id", catelogId)
                .notIn(!attrIds.isEmpty(), "attr_id", attrIds)  // 查询没有关联过属性分组的属性
                .eq("attr_type", ATTR_TYPE_BASE.getCode()); // 查询基本属性
        // 模糊查询
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(query -> {
                query.eq("attr_id", key)
                        .or()
                        .like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
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
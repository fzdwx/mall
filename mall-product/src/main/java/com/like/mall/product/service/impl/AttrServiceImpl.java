package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.AttrAttrgroupRelationDao;
import com.like.mall.product.dao.AttrDao;
import com.like.mall.product.dao.AttrGroupDao;
import com.like.mall.product.dao.CategoryDao;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.entity.AttrGroupEntity;
import com.like.mall.product.entity.CategoryEntity;
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


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao aarDao;
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
                new QueryWrapper<AttrEntity>()
                                          );

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
        AttrAttrgroupRelationEntity aarEntity = AttrAttrgroupRelationEntity
                .builder()
                .attrGroupId(attr.getAttrGroupId())
                .attrId(ae.getAttrId())
                .build();
        aarDao.insert(aarEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        // 1.组装查询条件
        if(catelogId != 0) {
            query.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if(StringUtils.isNotBlank(key)) {
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
                           AttrAttrgroupRelationEntity aare = aarDao.selectOne(
                                   // 查询属性分组组名和分类
                                   new QueryWrapper<AttrAttrgroupRelationEntity>().eq(
                                           "attr_id",
                                           vo.getAttrId()));
                           if(aare != null) {
                               AttrGroupEntity attrGroup = agDao.selectById(
                                       aare.getAttrGroupId());
                               vo.setGroupName(attrGroup.getAttrGroupName());
                           }
                           // 3.2.获取当前attr对应的分类信息
                           CategoryEntity category = cDao.selectById(vo.getCatelogId());
                           if(category != null) {
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
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = aarDao.selectOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if(attrAttrgroupRelationEntity != null) {
            vo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
        }
        assert attrAttrgroupRelationEntity != null;
        AttrGroupEntity attrGroupEntity = agDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
        if(attrGroupEntity != null) {
            vo.setGroupName(attrGroupEntity.getAttrGroupName());
        }

        // 3.获取分类完整路径
        Long[] catelogPath = categoryService.findCatelogPath(attr.getCatelogId());
        vo.setCatelogPath(catelogPath);
        return vo;
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
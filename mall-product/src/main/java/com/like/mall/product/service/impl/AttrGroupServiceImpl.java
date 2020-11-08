package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.AttrAttrGroupRelationDao;
import com.like.mall.product.dao.AttrGroupDao;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.entity.AttrGroupEntity;
import com.like.mall.product.service.AttrGroupService;
import com.like.mall.product.service.AttrService;
import com.like.mall.product.vo.AttrGroupAndAttrVo;
import com.like.mall.product.vo.AttrGroupRelationVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    AttrAttrGroupRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>());

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        IPage<AttrGroupEntity> page = null;
        QueryWrapper<AttrGroupEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key"); // 获取参数中的key
        if (StringUtils.isNotBlank(key)) {
            query.and(o -> {
                o.eq("attr_group_id", key)
                        .or()
                        .like("attr_group_name", key);
            });
        }
        // 默认显示所有
        //        if (catelogId == 0) {
        //            page = page (new Query<AttrGroupEntity> ().getPage (params), new QueryWrapper<AttrGroupEntity>
        //            ());
        //        } else {
        //            // select * from pms_attr_group where catelog_id = ? and ( attr_group_id = key or
        //            attr_group_name  like "%key%"
        //            query.eq ("catelog_id", catelogId);
        //        }
        if (catelogId != 0) {
            query.eq("catelog_id", catelogId);
        }
        page = page(new Query<AttrGroupEntity>().getPage(params), query);
        return new PageUtils(page);
    }

    @Override
    public Long delAttrAndAttrGroupRelation(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> entities =
                vos.stream()
                        .map((vo) -> {
                            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                                    AttrAttrgroupRelationEntity.builder()
                                            .build();
                            BeanUtils.copyProperties(vo, attrAttrgroupRelationEntity);
                            return attrAttrgroupRelationEntity;
                        })
                        .collect(Collectors.toList());
        return relationDao.delAttrAndAttrGroupRelation(entities);
    }

    @Resource
    AttrService attrService;

    /**
     * 根据分类id查询对应的属性分组以及属性
     *
     * @param catelogId catelog id
     * @return {@link List<AttrGroupAndAttrVo>}
     */
    @Override
    public List<AttrGroupAndAttrVo> getAttrGroupAndAttrByCatId(Long catelogId) {
        // 1、查询所有分组
        List<AttrGroupEntity> attrGroup = list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));

        return attrGroup.stream()
                .map(i -> {
                    AttrGroupAndAttrVo vo = new AttrGroupAndAttrVo();
                    BeanUtils.copyProperties(i, vo);
                    // 2、根据分组id查询所有属性
                    List<AttrEntity> attrs = attrService.getAttrGroupById(vo.getAttrGroupId());
                    vo.setAttrs(attrs);
                    return vo;
                })
                .collect(Collectors.toList());
    }

}
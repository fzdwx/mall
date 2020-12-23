package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import com.like.mall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    int saveAttrAndAttrGroupRrtion(List<AttrGroupRelationVo> vos);



	AttrAttrgroupRelationEntity findOneByAllOrderByAttrGroupIdDesc(AttrAttrgroupRelationEntity attrAttrgroupRelationEntity);



	List<AttrAttrgroupRelationEntity> selectByAllOrderByAttrGroupIdDesc(AttrAttrgroupRelationEntity attrAttrgroupRelationEntity);


}


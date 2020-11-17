package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.vo.AttrRespVo;
import com.like.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 级联保存 属性，属性-属性分组
     */
    void saveAttr(AttrVo attr);

    /**
     * 查询分类的属性
     * - catelogId为0：查询所有
     * - 为其他查询对应的
     * @param params
     * @param catelogId
     * @param attrType
     * @return
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    /**
     * 数据回显：
     * - attr本体
     * - category分类的信息
     * 得到attr信息
     *
     * @param attrId attr id
     *
     * @return {@link AttrRespVo}
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * 更新的属性的时候级联更新
     * - 属性和属性组的关系
     *
     * @param attr attr
     */
    void updateDetail(AttrVo attr);


    /**
     * 根据attrGroupId(基本属性)获取attr列表
     *
     * @param attrGroupId attr组id
     *
     * @return {@link List<AttrEntity>}
     */
    List<AttrEntity> getAttrGroupById(Long attrGroupId);

    /**
     * 没有关系attr
     * 获取没有关联分组的属性
     *
     * @param params      参数个数
     * @param attrGroupId attr组id
     * @return {@link PageUtils}
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);

    /**
     * 在attrId中查找可检索的属性
     *
     * @param attrIdList attr id列表
     * @return {@link List<Long>}
     */
    List<Long> selectSearchAttrs(List<Long> attrIdList);
}


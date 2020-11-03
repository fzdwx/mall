package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.vo.AttrRespVo;
import com.like.mall.product.vo.AttrVo;

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
     * @return
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId);

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
}


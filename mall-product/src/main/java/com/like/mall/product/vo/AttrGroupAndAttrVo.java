package com.like.mall.product.vo;

import com.like.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author like
 * @since 2020-11-08 11:55
 */
@Data
public class AttrGroupAndAttrVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;


    /**
     * 存放所有属性
     */
    private List<AttrEntity> attrs;
}

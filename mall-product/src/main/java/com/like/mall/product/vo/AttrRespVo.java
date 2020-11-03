package com.like.mall.product.vo;

import lombok.Data;

/**
 * @author like
 * @since 2020-11-02 20:56
 */
@Data
public class AttrRespVo extends AttrVo {

    /**
     * 分类名字
     */
    private String catelogName;
    /**
     * 属性分组名字
     */
    private String groupName;

    /** 保存完整分类路径 */
    private Long[] catelogPath;
}

package com.like.mall.search.constant;

public interface EsConstant {

    /**
     * 商品保存在es中的索引
     */
    public static final String PRODUCT_INDEX ="product";
    /**
     * 一次从es中读取多少个数据
     */
    public static final Integer PRODUCT_PAGE_SIZE = 16;
    public static final String failure ="失败";
    public static final String success ="成功";

}

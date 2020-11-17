package com.like.mall.common.constant;

/**
 * @author like
 * @since 2020-11-04 13:33
 */
public class ProductConstant {

    public enum AttrEnum {

        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");


        private int code;
        private String meg;

        AttrEnum(int code, String meg) {
            this.code = code;
            this.meg = meg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMeg() {
            return meg;
        }

        public void setMeg(String meg) {
            this.meg = meg;
        }
    }


    /** 商品新建*/
    public static final Integer NEW = 0;
    /** 商品上架*/
    public static final Integer UP = 1;
    /** 商品下架*/
    public static final Integer DOWN = 2;
}

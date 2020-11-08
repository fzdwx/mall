package com.like.mall.common.constant;

/**
 * @author like
 * @since 2020-11-04 13:33
 */
public class WareConstant {

    public  static class Purchase {
        // 采购单状态
        public static final int create = 0;
        public static final int assigned = 1;
        public static final int receive = 2;
        public static final int finish = 3;
        public static final int hasError = 4;
    }

    public  static class PurchaseDetail{
        // 采购需求单状态
        public static final int create = 0;
        public static final int assigned = 1;
        public static final int buying = 2;
        public static final int finish = 3;
        public static final int hasError = 4;
    }

}

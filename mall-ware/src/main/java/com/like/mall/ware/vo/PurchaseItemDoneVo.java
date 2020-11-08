package com.like.mall.ware.vo;

import lombok.Data;

/**
 * @author like
 * @since 2020-11-08 19:54
 */
@Data
public class PurchaseItemDoneVo {

    private Long itemId;
    private Integer status;
    private String reason;

}

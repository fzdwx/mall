package com.like.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author like
 * @since 2020-11-08 19:53
 */
@Data
public class PurchaseDoneVo {

    private Long id; // 采购单id
    private List<PurchaseItemDoneVo> items; // 采购项
}

package com.like.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author like
 * @since 2020-11-08 18:00
 */
@Data
public class MergeVo {

    private Long purchaseId;
    private List<Long> items;
}

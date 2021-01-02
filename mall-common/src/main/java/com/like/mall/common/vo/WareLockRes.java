package com.like.mall.common.vo;

import lombok.Data;

/**
 * @author like
 * @date 2021-01-02 19:02
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class WareLockRes {

    private Long skuId;
    private Integer numOf;
    private Boolean lock;
}

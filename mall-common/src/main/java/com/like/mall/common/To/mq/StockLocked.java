package com.like.mall.common.To.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author like
 * @date 2021-01-05 16:53
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLocked {
    private Long id;// 库存工作单id
    private Long detailId; //工作详情单id
    private Long skuId;
    private Integer skuNum;
    private Long wareId;
    private Integer lockStatus;

}

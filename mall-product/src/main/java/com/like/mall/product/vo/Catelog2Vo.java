package com.like.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author like
 * @since 2020-11-22 16:42
 * 二级分类vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {
    private String catalogId; // 一级父分类id
    private List<Catelog3Vo> catalog3List; // 三级子分类id
    private String id;
    private String name;

    /**
     * 二级分类vo
     *
     * @author like
     * @date 2020/11/22 16:47:07
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }
}

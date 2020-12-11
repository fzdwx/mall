package com.like.mall.search.service;

import com.like.mall.search.vo.SearchParam;
import com.like.mall.search.vo.SearchResData;

import java.io.IOException;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-10 12:07
 */
public interface MallSearchService {

    /**
     * 搜索
     *
     * @param param 检索的所有参数参数
     * @return {@link Object} 检索的结果
     */
    SearchResData search(SearchParam param) throws IOException;
}

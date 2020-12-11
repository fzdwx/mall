package com.like.mall.search.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author like
 * @date 2020-12-11 17:42
 * @email 980650920@qq.com
 */
@FeignClient("mall-product")
public interface ProductServiceFeign {

    @RequestMapping("product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);


}

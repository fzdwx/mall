package com.like.mall.product.feign.fallback;

import com.like.mall.common.utils.R;
import com.like.mall.product.feign.SecKillFeignService;
import org.springframework.stereotype.Component;

/**
 * @author like
 * @date 2021-01-30 11:26
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
public class SecKillFeignServiceFallBack implements SecKillFeignService {

    @Override
    public R getSkuSecKillInfo(String skuId) {
        return R.error("太多的请求");
    }
}

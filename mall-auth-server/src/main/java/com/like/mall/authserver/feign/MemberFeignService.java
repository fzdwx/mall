package com.like.mall.authserver.feign;

import com.like.mall.authserver.Vo.UserRegisterVo;
import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author like
 * @date 2020-12-15 19:07
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @PostMapping("member/member/register")
    public R register(@RequestBody UserRegisterVo vo);
}

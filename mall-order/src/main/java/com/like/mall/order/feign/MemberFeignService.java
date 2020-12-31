package com.like.mall.order.feign;

import com.like.mall.order.vo.MemberAddrVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author like
 * @date 2020-12-31 17:25
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("member/memberreceiveaddress/{memberId}/addresses")
     List<MemberAddrVo> getUserAddress(@PathVariable Long memberId);
}

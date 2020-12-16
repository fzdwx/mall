package com.like.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.member.entity.MemberEntity;
import com.like.mall.member.vo.UserLoginVo;
import com.like.mall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 12:51:23
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean checkEmail(String email);

    boolean checkMobile(String mobile);

    boolean checkUsername(String username);

    void register(UserRegisterVo vo);

    MemberEntity login(UserLoginVo vo);
}


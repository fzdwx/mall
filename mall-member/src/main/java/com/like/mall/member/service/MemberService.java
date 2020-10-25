package com.like.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.member.entity.MemberEntity;

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
}


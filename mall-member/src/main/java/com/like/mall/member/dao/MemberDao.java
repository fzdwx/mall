package com.like.mall.member.dao;

import com.like.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 12:51:23
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

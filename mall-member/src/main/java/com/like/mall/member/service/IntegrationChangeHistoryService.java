package com.like.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 12:51:23
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


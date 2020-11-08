package com.like.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.ware.entity.PurchaseEntity;
import com.like.mall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:02:26
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void merge(MergeVo vo);
}


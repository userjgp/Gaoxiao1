package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;

import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *

 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
//
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);
//
//    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}


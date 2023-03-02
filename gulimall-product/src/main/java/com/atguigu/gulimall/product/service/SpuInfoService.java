package com.atguigu.gulimall.product.service;



import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * spu信息
 *

 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    void saveSpuInfo(SpuSaveVo vo);
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);


//
//    void savesupInfo(SpuSaveVo vo);
//
//    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);
//
//    PageUtils queryPageByCondtion(Map<String, Object> params);
//
//    /**
//     * 商品上架
//     * @param spuId
//     */
//    void up(Long spuId);
//
    /**
     * 根据skuId查询spu的信息
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}


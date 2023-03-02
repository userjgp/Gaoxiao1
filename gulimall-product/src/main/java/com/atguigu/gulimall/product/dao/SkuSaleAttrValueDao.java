package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;

import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:00:33 * @author jiguangpeng
 *  * @email 1762928707@qq.com
 *  * @date 2022-12-05 23:00:33
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId")Long skuId);


//    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);

}

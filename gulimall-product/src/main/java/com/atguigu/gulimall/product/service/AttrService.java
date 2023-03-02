package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.Attrvo;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 */
public interface AttrService extends IService<AttrEntity> {

//    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(Attrvo attr);
//
//
//    AttrRespVo getAttrInfo(Long attrId);
//
//    void updateAttrById(AttrVo attr);
//
//    /**
//     * 根据分组id找到关联的所有属性
//     *
//     * @param attrgroupId
//     * @return
//     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);
//
//

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性集合里面，挑出检索属性
     *
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrs(List<Long> attrIds);

    PageUtils queryBaseAttrPage(Map<String, Object> parmas, Long cateLogId,String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(Attrvo attrvo);

    void deleteRelation(AttrGroupRelationVo[] vos);


}


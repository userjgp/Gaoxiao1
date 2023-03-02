package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 */
public interface  CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
//
    List<CategoryEntity> listWithTree();
//
    void removeMenuByIds(List<Long> asList);

//
//    /**
//     * 找到catelogId的完整路径
//     * [父/子/孙]
//     * @param catelogId
//     * @return
//     */
    Long[] findCatelogPath(Long catelogId);


//
    public void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2Vo>> getCatalogJson() throws InterruptedException;

    Map<String, List<Catelog2Vo>> getCatalogJsonRedissonDb() throws InterruptedException;

    Map<String, List<Catelog2Vo>> getCatalogJsonRedisDb() throws InterruptedException;

    Map<String, List<Catelog2Vo>> getCatalogJsonDb();
//
//    List<CategoryEntity> getLevel1Categorys();
//
//    Map<String, List<Catelog2Vo>> getCatalogJson();
}


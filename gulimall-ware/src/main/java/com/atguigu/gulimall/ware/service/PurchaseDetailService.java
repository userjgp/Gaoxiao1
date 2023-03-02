package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;

import java.util.List;
import java.util.Map;


public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void listDetailByPurchaseId(Long id);
}


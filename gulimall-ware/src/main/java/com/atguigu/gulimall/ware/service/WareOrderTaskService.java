package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;
import com.atguigu.common.utils.PageUtils;
/**
 * 库存工作单
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:02:13
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


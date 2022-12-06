package com.atguigu.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;

import java.util.Map;
import com.atguigu.common.utils.PageUtils;
/**
 * 订单退货申请
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 22:58:02
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


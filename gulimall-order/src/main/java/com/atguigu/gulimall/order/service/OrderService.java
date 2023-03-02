package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.PageUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author 夏沫止水
 * @email HeJieLin@gulimall.com
 * @date 2020-05-22 19:49:53
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo asyncVo);

    void createSeckillOrder(SeckillOrderTo orderTo);




    /**
     * 创建订单
     * @param vo
     * @return
     */


    /**
     * 按照订单号获取订单信息
     * @param orderSn
     * @return
     */

    /**
     * 关闭订单
     * @param orderEntity
     */


    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */


    /**
     * 查询当前用户所有订单数据
     * @param params
     * @return
     */


    /**
     *支付宝异步通知处理订单数据
     * @param asyncVo
     * @return
     */


    /**
     * 微信异步通知处理
     * @param notifyData
     */



    /**
     * 创建秒杀单
     * @param orderTo
     */

}


package com.atguigu.gulimall.order.web;


import com.atguigu.common.exception.NoStockException;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;


     @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
         OrderConfirmVo confirmVo=orderService.confirmOrder();
         model.addAttribute("confirmOrderData",confirmVo);
         return "confirm";
     }

     @PostMapping("/submitOrder")
     public String submitOrder(OrderSubmitVo orderSubmitVo,Model model, RedirectAttributes attributes){
         try{

             SubmitOrderResponseVo submitOrderResponseVo=orderService.submitOrder(orderSubmitVo);
             if(submitOrderResponseVo.getCode()==0){
                 //下单成功来到支付选择页
                 model.addAttribute("submitOrderResp",submitOrderResponseVo);
                 return "pay";
             }else {
                 String msg = "下单失败";
                 switch (submitOrderResponseVo.getCode()) {
                     case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                     case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                     case 3: msg += "库存锁定失败，商品库存不足"; break;
                 }
                 attributes.addFlashAttribute("msg",msg);
                 return "redirect:http://order.gulimall.com/toTrade";
             }
         }catch (Exception e) {
             if (e instanceof NoStockException) {
                 String message = ((NoStockException)e).getMessage();
                 attributes.addFlashAttribute("msg",message);
             }
             return "redirect:http://order.gulimall.com/toTrade";
         }
         //下单失败回到订单确认页重新确定订单信息
     }

}

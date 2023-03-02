package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.exception.CartExceptionHandler;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: nb纪广鹏
 **/

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";


    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String o = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(o)) {
            CartItemVo cartItemVo = new CartItemVo();

            CompletableFuture<Void> skuInfo1 = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
//        商品添加到购物车

                cartItemVo.setSkuId(skuId);
                cartItemVo.setCount(num);
                cartItemVo.setCheck(true);
                cartItemVo.setImage(data.getSkuDefaultImg());
                cartItemVo.setTitle(data.getSkuTitle());
//        cartItemVo.setTotalPrice(data.getPrice());
                cartItemVo.setPrice(data.getPrice());
            }, executor);
//        远程查询当前要添加的商品的信息
            CompletableFuture<Void> getSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(skuInfo1, getSaleAttrValues).get();

            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), s);
            return cartItemVo;
        } else {
            CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), s);

            return cartItemVo;

        }

    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();

        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
//      登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
//            如果临时购物车的数据没有合并
            List<CartItemVo> cartItem = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            if (cartItem != null) {
//            临时购物车有数据先合并
                for (CartItemVo cartItemVo : cartItem) {
                    addToCart(cartItemVo.getSkuId(), cartItemVo.getCount());
                }
//                清除临时购物车
                clearCart(CART_PREFIX + userInfoTo.getUserKey());
            }
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);


        } else {
            //  没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> cartItems = getCartItems(cartKey);

            cartVo.setItems(cartItems);


        }
        return cartVo;

    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(checked==1?true:false);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);


    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {

        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),redisValue);
    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {

        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //获取当前用户登录的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //如果用户未登录直接返回null
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            //获取购物车项
            String cartkey = CART_PREFIX + userInfoTo.getUserId();
            //获取所有的
            List<CartItemVo> cartItems = getCartItems(cartkey);
            if (cartItems == null) {
                throw new CartExceptionHandler();
            }
            //筛选出选中的
            cartItemVoList = cartItems.stream()
                    .filter(items -> items.getCheck())
                    .map(item -> {
                        //更新为最新的价格（查询数据库）
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    })
                    .collect(Collectors.toList());
        }

        return cartItemVoList;
    }



    private List<CartItemVo> getCartItems(String cartkey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartkey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }


    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }


    /**
     * 获取用户登录或者未登录购物车里所有的数据
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */


    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */


    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */



    /**
     * 删除购物项
     * @param skuId
     */

}

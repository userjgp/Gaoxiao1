package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService {
//远程获取sku信息
@RequestMapping("/product/skuinfo/info/{id}")
//@RequiresPermissions("product:spuinfo:info")
public R info(@PathVariable("id") Long id);
}

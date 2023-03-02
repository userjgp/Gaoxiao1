package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.service.SpuInfoService;
import com.atguigu.common.utils.R;

/**
 * spu信息
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:00:33
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SkuInfoService skuInfoService;



    /**
     * 根据skuId查询spu的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/skuId/{skuId}")
    public R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId) {

        SpuInfoEntity spuInfoEntity = spuInfoService.getSpuInfoBySkuId(skuId);

        return R.ok().setData(spuInfoEntity);
    }
    /**
     * 列表
//     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }


    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
            spuInfoService.up(spuId);
            return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }
//
//    /**
//     * 保存
//     */
@RequestMapping("/save")
//    @RequiresPermissions("product:skuinfo:save")
public R save(@RequestBody SpuSaveVo vo){
    spuInfoService.saveSpuInfo(vo);

    return R.ok();
}
//
//    /**
//     * 修改
//     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }
//
//    /**
//     * 删除
//     */
    @RequestMapping("/delete")
    @RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

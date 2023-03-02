package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.Attrvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.service.AttrService;


/**
 * 商品属性
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:00:33
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    //@RequiresPermissions("product:attr:list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = attrService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }

    @PostMapping("/update/{spuId}")
    public R update(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();

    }
@GetMapping("/base/listforspu/{spuId}")
public  R baseAttrlistforspu(@PathVariable("spuId") Long spuId){
    List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListforspu(spuId);
    return R.ok().put("data",productAttrValueEntities);
}

    @GetMapping("{attrType}/list/{catelogId}")
    public R baseattrsalelist(@RequestParam Map<String,Object> parmas,@PathVariable("catelogId") Long cateLogId,@PathVariable("attrType") String type){
        PageUtils pageUtils=attrService.queryBaseAttrPage(parmas,cateLogId,type);
        return  R.ok().put("page",pageUtils);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo respVo= attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody Attrvo attrvo){
		attrService.saveAttr(attrvo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody Attrvo attrvo){
		attrService.updateAttr(attrvo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

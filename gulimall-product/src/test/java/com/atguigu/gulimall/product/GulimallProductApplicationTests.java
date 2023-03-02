package com.atguigu.gulimall.product;

import com.aliyun.oss.*;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;


import com.aliyun.oss.OSS;

import com.aliyun.oss.OSSException;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
    @Autowired
    public BrandService brandService;
@Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void textredis() {
        ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
        forValue.set("hello","word"+ UUID.randomUUID().toString());

        String hello = forValue.get("hello");
        System.out.println(hello);
    }

    @Test
    public void test() {
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(22L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void textredisson() {
        System.out.println(redissonClient);
    }

    @Test
    public void test1() {
        List<SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(22L);
        System.out.println(saleAttrBySpuId);
    }


//    @Test
//    public void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("seccessfully");
//
//
//    }
//        @Autowired
//    OSSClient ossClient;
//    @Test
//    public void testupdate() throws Exception {
//            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
////            String endpoint = "oss-cn-beijing.aliyuncs.com";
////            // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
////            String accessKeyId = "LTAI5t8J8uFGHPwRdqLakSC7";
////            String accessKeySecret = "KrojIwnjABqlt0u0RVwy8Uqqic6pd5";
//            // 填写Bucket名称，例如examplebucket。
//            String bucketName = "gulimall-hellojgp";
//            // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//            String objectName = "aliyunuser.png";
//            // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
//            // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//            String filePath= "C:\\Users\\17629\\Desktop\\aliyunuser.png";
//
//            // 创建OSSClient实例。
////            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//            try {
//                InputStream inputStream = new FileInputStream(filePath);
//                // 创建PutObject请求。
//                ossClient.putObject(bucketName, objectName, inputStream);
//            } catch (OSSException oe) {
//                System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                        + "but was rejected with an error response for some reason.");
//                System.out.println("Error Message:" + oe.getErrorMessage());
//                System.out.println("Error Code:" + oe.getErrorCode());
//                System.out.println("Request ID:" + oe.getRequestId());
//                System.out.println("Host ID:" + oe.getHostId());
//            } finally {
//                if (ossClient != null) {
//                    ossClient.shutdown();
//                }
//            }
//                System.out.println("上传完成");
//        }


}

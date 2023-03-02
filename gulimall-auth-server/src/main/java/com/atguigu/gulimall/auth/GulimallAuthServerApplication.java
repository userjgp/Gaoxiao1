package com.atguigu.gulimall.auth;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 核心原理
 * 1）、@EnableRedisHttpSession导入RedisHttpSessionConfiguration配置
 *      1、给容器中添加了一个组件
 *          RedisOperationsSessionRepository：Redis操作session，session的增删改查封装类
 *2.SessionRepositoryFilter==>Filter   session存储过滤器：每个请求都必须进过filter
 *  *             1.创建的时候。就自动从容器中获取到SessionRepository
 *  *             2.原始的request 和 response被包装SessionRepositoryRequsetwrapper，SessionRepositoryResponsewrapper
 *  *             3.以后获取session
 * // *             SessionRepositoryRequsetwrapper
 *  *             4.wrapperRequset。getsession()；==》SessionRepository中获取的
 *  *
 *  *       装饰着模式
 *
 *
 *  自动延期：redis中也有过期时间
 */

@EnableRedisHttpSession     //整合Redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}

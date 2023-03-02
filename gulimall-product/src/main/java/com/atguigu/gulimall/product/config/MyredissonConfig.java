package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyredissonConfig {
    @Bean
    public RedissonClient redissonClient(){
//        构建cofig对象
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.30.128:6379");

//        根据config对象创建出实力
        return Redisson.create(config);

    }
}

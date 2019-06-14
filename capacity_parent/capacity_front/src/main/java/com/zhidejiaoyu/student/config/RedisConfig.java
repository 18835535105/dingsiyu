package com.zhidejiaoyu.student.config;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wuchenxi
 * @date 2018/11/14
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置 redis 序列化方式
     *
     * @return
     */
    @Bean
    public RedisTemplate redisTemplateInit() {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        // 设置redis中清学版学习内容过期时间为12小时
        redisTemplate.expire(RedisKeysConst.PREFIX, 12, TimeUnit.HOURS);
        return redisTemplate;
    }
}

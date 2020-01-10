package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 奖品相关缓存
 *
 * @author: wuchenxi
 * @date: 2020/1/10 09:58:58
 */
@Slf4j
@Component
public class AwardRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每天第一个被抽到的奖品初始化被抽到的次数
     *
     * @param name  奖品名称
     * @param count 第几位抽到该奖品
     */
    public void addDrawRecordIndex(String name, Integer count) {
        String hKey = RedisKeysConst.DRAW_COUNT_WITH_NAME;
        redisTemplate.opsForHash().put(hKey, name, count);
    }

    /**
     * 第多少位抽到奖品的人
     *
     * @param name
     * @return
     */
    public int selDrawRecordIndex(String name) {
        try {
            Object object = redisTemplate.opsForHash().get(RedisKeysConst.DRAW_COUNT_WITH_NAME, name);
            if (object != null) {
                return Integer.parseInt(object + "");
            }
        } catch (Exception e) {
            log.error("error=[{}]", e.getMessage());
        }
        return 0;
    }

    /**
     * 删除各个奖品抽到的次数缓存
     */
    public void delDrawRecord() {
        Set<Object> keys = redisTemplate.opsForHash().keys(RedisKeysConst.DRAW_COUNT_WITH_NAME);
        redisTemplate.opsForHash().delete(RedisKeysConst.DRAW_COUNT_WITH_NAME, keys);
    }
}

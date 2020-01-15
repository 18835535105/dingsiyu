package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.PayLogMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: wuchenxi
 * @date: 2020/1/9 11:54:54
 */
@Component
public class PayLogRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PayLogMapper payLogMapper;

    /**
     * 判断学生是否已经充值过
     *
     * @param studentId
     * @return true：充值过；false：未充值
     */
    public boolean isPaid(Long studentId) {
        String key = RedisKeysConst.IS_PAID + ":" + studentId;
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            return true;
        }
        int payCount = payLogMapper.countByStudent(studentId);
        if (payCount > 0) {
            redisTemplate.opsForValue().set(key, studentId);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
            return true;
        }
        return false;
    }
}

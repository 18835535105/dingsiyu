package com.zhidejiaoyu.student.common.redis;


import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CurrentDayOfStudyRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存正常学习下的错误信息
     *
     * @param redisStr  redis数据
     * @param studentId 学生id
     * @param feldId    学习数据id
     */
    public void saveStudyCurrent(String redisStr, Long studentId, Long feldId) {
        Object o = redisTemplate.opsForHash().get(redisStr + studentId, feldId);
        if (o != null) {
            int i = Integer.parseInt(o.toString());
            redisTemplate.opsForHash().put(redisStr + studentId, feldId, i);
        } else {
            redisTemplate.opsForHash().put(redisStr + studentId, feldId, 1);
        }
    }


}

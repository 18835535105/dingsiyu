package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 膜拜相关缓存
 *
 * @author: wuchenxi
 * @date: 2020/4/7 13:36:36
 */
@Slf4j
@Component
public class WorshipRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 记录当天被膜拜的学生
     *
     * @param studentId   当前学生id
     * @param byStudentId 被膜拜的学生id
     */
    public void saveTodayWorshipedStudentId(Long studentId, Long byStudentId) {
        String key = RedisKeysConst.BY_WORSHIPED_TODAY + DateUtil.formatDate(new Date(), DateUtil.YYYYMMDD);
        Object o = redisTemplate.opsForHash().get(key, studentId);
        Map<String, Long> map;
        if (o == null) {
            map = new HashMap<>(16);
            map.put(String.valueOf(byStudentId), byStudentId);
            redisTemplate.opsForHash().put(key, studentId, map);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        } else {
            map = (Map<String, Long>) o;
            if (!map.containsKey(String.valueOf(byStudentId))) {
                map.put(String.valueOf(byStudentId), byStudentId);
                redisTemplate.opsForHash().put(key, studentId, map);
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 查询今天学生膜拜过的学生id
     *
     * @param studentId
     * @return
     */
    public Map<String, Long> getTodayByWorshipedStudentIds(Long studentId) {
        String key = RedisKeysConst.BY_WORSHIPED_TODAY + DateUtil.formatDate(new Date(), DateUtil.YYYYMMDD);
        Object o = redisTemplate.opsForHash().get(key, studentId);
        if (o == null) {
            return Collections.emptyMap();
        }
        return (Map<String, Long>) o;
    }
}

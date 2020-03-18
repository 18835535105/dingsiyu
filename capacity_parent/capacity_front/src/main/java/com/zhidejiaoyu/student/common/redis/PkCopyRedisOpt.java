package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 副本挑战相关缓存
 *
 * @author: wuchenxi
 * @date: 2020/3/18 10:00:00
 */
@Slf4j
@Component
public class PkCopyRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 记录参加校区指定副本挑战的学生id
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @param studentId
     */
    public void saveSchoolCopyStudentInfo(Integer schoolAdminId, Long pkCopyBaseId, Long studentId) {
        String key = RedisKeysConst.SCHOOL_COPY + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        Set<Long> studentIds;
        if (o == null) {
            studentIds = new HashSet<>();
        } else {
            studentIds = (Set<Long>) o;
        }
        studentIds.add(studentId);
        redisTemplate.opsForHash().put(key, pkCopyBaseId, studentIds);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    /**
     * 获取参加校区指定副本挑战的学生id
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @return
     */
    public Set<Long> getSchoolCopyStudentInfo(Integer schoolAdminId, Long pkCopyBaseId) {
        String key = RedisKeysConst.SCHOOL_COPY + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        if (o != null) {
            return (Set<Long>) o;
        }
        return new HashSet<>();
    }
}

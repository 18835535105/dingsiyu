package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 校区每周活动排行操作类
 *
 * @author: wuchenxi
 * @date: 2020/5/27 10:20:20
 */
@Slf4j
@Component
public class WeekActivityRankOpt extends BaseRankOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化每周活动排行数据
     *
     * @param schoolAdminId
     * @param studentIds
     */
    public void init(Integer schoolAdminId, List<Long> studentIds) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdminId;
        if (CollectionUtils.isNotEmpty(studentIds)) {
            studentIds.parallelStream()
                    .forEach(studentId -> {
                        if (log.isDebugEnabled()) {
                            log.debug("初始化key：{}, 学生： {}", key, studentId);
                        }
                        redisTemplate.opsForZSet().add(key, studentId, 0);
                    });
        }
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

}

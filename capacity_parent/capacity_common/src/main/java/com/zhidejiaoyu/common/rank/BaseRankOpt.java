package com.zhidejiaoyu.common.rank;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排行公用方法
 *
 * @author: wuchenxi
 * @date: 2020/2/28 11:48:48
 */
@Component
public class BaseRankOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定成员的索引（即学生的排名）
     *
     * @param key
     * @param member
     * @return 获取的排名从 1 开始
     */
    public long getRank(String key, Long member) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, member);
        return rank == null ? -1 : rank + 1;
    }

    /**
     * 按照 score 从高到低查询指定范围内的学生 id
     *
     * @param key
     * @param start 起始索引
     * @param end   结束索引
     * @return
     */
    public List<Long> getReverseRangeMembersBetweenStartAndEnd(String key, Long start, Long end) {
        return this.getReverseRangeMembersBetweenStartAndEnd(key, start, end, null);
    }

    /**
     * 按照 score 从高到低查询指定范围内的学生 id
     *
     * @param key
     * @param start   起始索引
     * @param end     结束索引
     * @param showNum 排行最多可以展示的数据量，如果是100，则最多只能展示到第100名的数据，为空时表示展示所有
     * @return 学生id
     */
    public List<Long> getReverseRangeMembersBetweenStartAndEnd(String key, Long start, Long end, Integer showNum) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples;
        if (showNum == null) {
            typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end - 1);
        } else {
            typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end - 1 >= showNum ? showNum : end - 1);
        }

        if (typedTuples == null || typedTuples.size() == 0) {
            return new ArrayList<>();
        }
        return typedTuples.stream().map(typedTuple -> Long.valueOf(String.valueOf(typedTuple.getValue()))).collect(Collectors.toList());
    }

    /**
     * 获取指定类型排行参与人数
     *
     * @param key
     * @return
     */
    public long getMemberSize(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return size == null ? 0L : size;
    }
}

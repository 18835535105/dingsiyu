package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author wuchenxi
 * @date 2019-07-23
 */
public class RankOptTest extends BaseTest {

    @Autowired
    private RankOpt rankOpt;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void addOrUpdate() {
    }

    @Test
    public void optGoldRank() {
    }

    @Test
    public void optCcieRank() {
    }

    @Test
    public void optMedalRank() {
    }

    @Test
    public void optWorshipRank() {
    }

    @Test
    public void getMemberSize() {
    }

    @Test
    public void getReverseRangeMembersBetweenStartAndEnd() {
    }

    @Test
    public void getScore() {
    }

    @Test
    public void getRank() {
    }

    @Test
    public void deleteMember() {
        String key = RankKeysConst.CLASS_CCIE_RANK + 197 + ":" + 118;
        Long studentId = 5686L;
        System.out.println(redisTemplate.opsForZSet().score(key, studentId));
        rankOpt.deleteMember(key, studentId);
    }
}

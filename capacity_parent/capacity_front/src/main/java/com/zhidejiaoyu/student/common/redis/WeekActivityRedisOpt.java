package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.pojo.WeekActivity;
import com.zhidejiaoyu.student.business.activity.vo.AwardListVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 每周活动缓存
 *
 * @author: wuchenxi
 * @date: 2020/5/27 17:19:19
 */
@Component
public class WeekActivityRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 完成进度与奖励的金币数对应关系
     */
    private static final Map<Integer, Integer> AWARD_GOLD_MAP = new HashMap<>(16);

    /**
     * 不同活动的id与提示语之间的对应关系
     */
    private static final Map<Integer, String> CONDITION_MAP = new HashMap<>(16);

    static {
        AWARD_GOLD_MAP.put(0, 10);
        AWARD_GOLD_MAP.put(1, 20);
        AWARD_GOLD_MAP.put(2, 30);

        CONDITION_MAP.put(1, "达成熟练单词$&$个");
        CONDITION_MAP.put(2, "学习时长达到$&$小时");
        CONDITION_MAP.put(3, "金币贡献$&$个");
        CONDITION_MAP.put(4, "成绩累计$&$分");
        CONDITION_MAP.put(5, "PK获胜累计$&$次");
        CONDITION_MAP.put(6, "金币考试达到$&$分");
        CONDITION_MAP.put(7, "连续打卡$&$天");
        CONDITION_MAP.put(8, "boss伤害累计$&$");
    }

    /**
     * 获取奖励列表
     *
     * @param studentId
     * @param plan         当前活动学生完成进度
     * @param weekActivity
     * @return
     */
    public List<AwardListVO.ActivityList> getActivityList(Long studentId, Integer plan, WeekActivity weekActivity) {

        plan = plan == null ? 0 : plan;

        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_LIST;
        Object o = redisTemplate.opsForHash().get(key, studentId);

        Map<Integer, Integer> totalPlanMap = new HashMap<>(16);
        totalPlanMap.put(0, weekActivity.getPlanOne());
        totalPlanMap.put(1, weekActivity.getPlanTwo());
        totalPlanMap.put(2, weekActivity.getPlanThree());

        List<AwardListVO.ActivityList> activityLists;
        int conditionCount = 3;
        if (o == null) {
            activityLists = new ArrayList<>(conditionCount);
            for (int i = 0; i < conditionCount; i++) {
                AwardListVO.ActivityList activityList = AwardListVO.ActivityList.builder()
                        .awardGold(AWARD_GOLD_MAP.get(i))
                        .canGet(plan >= totalPlanMap.get(i) ? 2 : 1)
                        .condition(CONDITION_MAP.get(weekActivity.getId()).replace("$&$", String.valueOf(totalPlanMap.get(i))))
                        .build();
                activityLists.add(activityList);
            }
            redisTemplate.opsForHash().put(key, studentId, activityLists);
            redisTemplate.expire(key, 7, TimeUnit.DAYS);
            return activityLists;
        }

        activityLists = (List<AwardListVO.ActivityList>) o;
        for (int i = 0; i < conditionCount; i++) {
            AwardListVO.ActivityList activityList = activityLists.get(i);
            Integer canGet = activityList.getCanGet();
            if (canGet == 2 || canGet == 3) {
                continue;
            }
            activityList.setCanGet(plan >= totalPlanMap.get(i) ? 2 : 1);
            activityList.setCondition(CONDITION_MAP.get(weekActivity.getId()).replace("$&$", String.valueOf(totalPlanMap.get(i))));
        }
        redisTemplate.opsForHash().put(key, studentId, activityLists);
        return activityLists;
    }
}

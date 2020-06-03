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

    /**
     * 保存测试下的错误信息
     */
    public void saveTestStudyCurrent(Long studentId, String errorTestInfo) {
        if (errorTestInfo != null && errorTestInfo.length() > 0) {
            Object o = redisTemplate.opsForHash().get(RedisKeysConst.ERROR_TEST + studentId, 1);
            String errorTestInfo1;
            if (o == null) {
                errorTestInfo1 = getErrorTestInfo(errorTestInfo, null);

            } else {
                String testInfo = o.toString();
                errorTestInfo1 = getErrorTestInfo(errorTestInfo, testInfo);
            }
            redisTemplate.opsForHash().put(RedisKeysConst.ERROR_TEST + studentId, 1, errorTestInfo1);
        }
    }

    private String getErrorTestInfo(String errorTestInfo, String testInfo) {
        StringBuilder builder = new StringBuilder();
        String[] split = errorTestInfo.split("##");
        for (String str : split) {
            String[] suStr = str.split("&&");
            if (suStr.length > 0) {
                if (suStr.length > 1) {
                    builder.append(suStr[0]).append("&&").append(suStr[1]).append("##");
                } else {
                    builder.append(suStr[0]).append("##");
                }
            }
        }
        if (testInfo == null) {
            testInfo = builder.toString();
        } else {
            testInfo += builder.toString();
        }
        return testInfo;

    }


}

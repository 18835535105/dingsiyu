package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 源分战力排行
 *
 * @author: wuchenxi
 * @date: 2020/2/28 11:04:04
 */
@Slf4j
@Component
public class SourcePowerRankOpt extends BaseRankOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加或者修改源分战力排行
     *
     * @param sourcePower 源分战力值
     * @param studyPower  PK值
     */
    public void optSourcePowerRank(Student student, Integer sourcePower, Integer studyPower) {

        double score = Double.parseDouble(sourcePower + "." + studyPower);

        try {
            redisTemplate.opsForZSet().add(SourcePowerKeysConst.COUNTRY_RANK, student.getId(), score);
            redisTemplate.opsForZSet().add(SourcePowerKeysConst.SCHOOL_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId(), score);
        } catch (Exception e) {
            log.error("修改学生[{} - {} - {}]源分战力排行出错！", student.getId(), student.getAccount(), student.getStudentName());
        }
    }

}

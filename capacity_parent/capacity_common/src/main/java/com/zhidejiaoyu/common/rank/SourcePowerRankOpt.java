package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.StudentEquipmentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

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

    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    /**
     * 添加或者修改源分战力排行
     * 如果学生没有装备飞船，不显示在排行中
     *
     * @param sourcePower 源分战力值
     * @param studyPower  PK值
     */
    public void optSourcePowerRank(Student student, Integer sourcePower, Integer studyPower) {

        Long studentId = student.getId();
        int count = studentEquipmentMapper.countEquipmentShipByStudentId(studentId);
        if (count == 0) {
            return;
        }

        double score = Double.parseDouble(sourcePower + "." + studyPower);

        try {
            redisTemplate.opsForZSet().add(SourcePowerKeysConst.COUNTRY_RANK, studentId, score);
            redisTemplate.opsForZSet().add(SourcePowerKeysConst.SCHOOL_RANK + TeacherInfoUtil.getSchoolAdminId(student), studentId, score);
            redisTemplate.opsForZSet().add(SourcePowerKeysConst.SERVER_RANK, studentId, score);
            log.info("添加学生[{}]源分战力排行成功！", studentId);
        } catch (Exception e) {
            log.error("修改学生[{} - {} - {}]源分战力排行出错！", studentId, student.getAccount(), student.getStudentName());
        }
    }

    /**
     * 删除学生源分战力排行
     *
     * @param studentId
     */
    public void deleteSourcePower(Long studentId) {
        this.deleteSourcePower(Collections.singletonList(studentId));
    }

    /**
     * 删除学生源分战力排行
     *
     * @param studentIds
     */
    public void deleteSourcePower(List<Long> studentIds) {
        redisTemplate.opsForZSet().remove(SourcePowerKeysConst.COUNTRY_RANK, studentIds);
        redisTemplate.opsForZSet().remove(SourcePowerKeysConst.SCHOOL_RANK, studentIds);
        redisTemplate.opsForZSet().remove(SourcePowerKeysConst.SERVER_RANK, studentIds);
        log.info("学生id={}已被删除，其源分战力排行已被删除！", studentIds.toString());
    }

    /**
     * 获取指定学生的源分战力值
     *
     * @param key
     * @param studentId
     * @return 整数部分是源分战力值，小数部分是PK值
     */
    public double getStudentScore(String key, Long studentId) {
        Double score = redisTemplate.opsForZSet().score(key, studentId);
        return score == null ? 0.0D : score;
    }
}

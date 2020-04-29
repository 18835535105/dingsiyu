package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.mapper.StudentEquipmentMapper;
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
            log.info("添加学生[{}]源分战力排行成功！", studentId);
        } catch (Exception e) {
            log.error("修改学生[{} - {} - {}]源分战力排行出错！", studentId, student.getAccount(), student.getStudentName());
        }
    }

}

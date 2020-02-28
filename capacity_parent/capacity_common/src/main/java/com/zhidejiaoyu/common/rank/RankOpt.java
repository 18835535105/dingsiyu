package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.CcieMapper;
import com.zhidejiaoyu.common.mapper.WorshipMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 排行操作类
 * 当学生需要参与排行的数据指标发生变化时，在当前类中操作变化值
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
@Slf4j
@Component
public class RankOpt extends BaseRankOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    /**
     * 新增或者更新指定数据
     *
     * @param key
     * @param member
     * @param score
     */
    public void addOrUpdate(String key, Long member, Double score) {
        try {
            redisTemplate.opsForZSet().add(key, member, score);
        } catch (Exception e) {
            log.warn("更新 redis 中排行数据失败！", e);
        }
    }

    /**
     * 修改金币排行信息
     *
     * @param student
     */
    public void optGoldRank(Student student) {
        try {
            // 更新 redis 中金币排行数据
            double gold = BigDecimalUtil.add(student.getOfflineGold(), student.getSystemGold());
            this.addOrUpdate(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), gold);
            this.addOrUpdate(RankKeysConst.SCHOOL_GOLD_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId(), gold);
            this.addOrUpdate(RankKeysConst.COUNTRY_GOLD_RANK, student.getId(), gold);
        } catch (Exception e) {
            log.error("修改 redis 排行中的金币排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 删除金币排行信息
     *
     * @param student
     */
    public void deleteGoldRank(Student student) {
        try {
            //删除学生金币信息
            redisTemplate.opsForZSet().remove(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.SCHOOL_GOLD_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.COUNTRY_GOLD_RANK, student.getId());
        } catch (Exception e) {
            log.error("修改 redis 排行中的金币排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 删除膜拜排行信息
     *
     * @param student
     */
    public void deleteWorshipRank(Student student) {
        try {
            //删除学生膜拜信息
            redisTemplate.opsForZSet().remove(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.SCHOOL_WORSHIP_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId());
        } catch (Exception e) {
            log.error("修改 redis 排行中的金币排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 删除勋章排行信息
     *
     * @param student
     */
    public void deleteMedalRank(Student student) {
        try {
            //删除学生勋章信息
            redisTemplate.opsForZSet().remove(RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.SCHOOL_MEDAL_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.COUNTRY_MEDAL_RANK, student.getId());
        } catch (Exception e) {
            log.error("修改 redis 排行中的金币排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 删除勋章证书信息
     *
     * @param student
     */
    public void deleteCcieRank(Student student) {
        try {
            //删除学生证书信息
            redisTemplate.opsForZSet().remove(RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.SCHOOL_CCIE_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId());
            redisTemplate.opsForZSet().remove(RankKeysConst.COUNTRY_CCIE_RANK, student.getId());
        } catch (Exception e) {
            log.error("修改 redis 排行中的金币排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }


    /**
     * 修改证书排行信息
     *
     * @param student
     */
    public void optCcieRank(Student student) {
        try {
            int countCcieByStudentId = ccieMapper.getCountCcieByStudentId(student.getId());
            this.addOrUpdate(RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), countCcieByStudentId * 1.0);
            this.addOrUpdate(RankKeysConst.SCHOOL_CCIE_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId(), countCcieByStudentId * 1.0);
            this.addOrUpdate(RankKeysConst.COUNTRY_CCIE_RANK, student.getId(), countCcieByStudentId * 1.0);
        } catch (Exception e) {
            log.error("修改 redis 排行中的证书排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 修改勋章排行信息
     *
     * @param student
     */
    public void optMedalRank(Student student) {
        try {
            int medalCount = awardMapper.countGetModel(student.getId());
            this.addOrUpdate(RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), medalCount * 1.0);
            this.addOrUpdate(RankKeysConst.SCHOOL_MEDAL_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId(), medalCount * 1.0);
            this.addOrUpdate(RankKeysConst.COUNTRY_MEDAL_RANK, student.getId(), medalCount * 1.0);
        } catch (Exception e) {
            log.error("修改 redis 排行中的勋章排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 修改膜拜排行信息
     *
     * @param student
     */
    public void optWorshipRank(Student student) {
        try {
            int byWorshipCount = worshipMapper.countByWorship(student.getId());
            this.addOrUpdate(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), byWorshipCount * 1.0);
            this.addOrUpdate(RankKeysConst.SCHOOL_WORSHIP_RANK + TeacherInfoUtil.getSchoolAdminId(student), student.getId(), byWorshipCount * 1.0);
            this.addOrUpdate(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId(), byWorshipCount * 1.0);
        } catch (Exception e) {
            log.error("修改 redis 排行中的勋章排行信息失败！[{} - {} - {}]", student.getId(), student.getAccount(), student.getStudentName(), e);
        }
    }

    /**
     * 获取 score
     *
     * @param key
     * @param member
     * @return
     */
    public long getScore(String key, Long member) {
        Double score = redisTemplate.opsForZSet().score(key, member);
        if (score == null) {
            return -1;
        }
        return Math.round(score);
    }


    /**
     * 删除多余的排行信息
     *
     * @param key
     * @param studentId
     */
    public void deleteMember(String key, Long studentId) {
        redisTemplate.opsForZSet().remove(key, studentId);
    }
}

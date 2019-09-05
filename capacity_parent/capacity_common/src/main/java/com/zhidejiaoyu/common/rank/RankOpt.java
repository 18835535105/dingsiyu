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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排行操作类
 * 当学生需要参与排行的数据指标发生变化时，在当前类中操作变化值
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
@Slf4j
@Component
public class RankOpt {

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
     * 获取指定类型排行参与人数
     *
     * @param key
     * @return
     */
    public Long getMemberSize(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return size == null ? 0L : size;
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
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end - 1 >= 100 ? 100 : end - 1);
        if (typedTuples == null || typedTuples.size() == 0) {
            return new ArrayList<>();
        }
        return typedTuples.stream().map(typedTuple -> Long.valueOf(String.valueOf(typedTuple.getValue()))).collect(Collectors.toList());
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
     * 获取指定成员的索引（即学生的排名）
     *
     * @param key
     * @param member
     * @return  获取的排名从 1 开始
     */
    public long getRank(String key, Long member) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, member);
        return rank == null ? -1 : rank + 1;
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

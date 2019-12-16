package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.mapper.AwardMapper;
import com.zhidejiaoyu.common.mapper.CcieMapper;
import com.zhidejiaoyu.common.mapper.MedalMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleAwardMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleWorshipMapper;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.Medal;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzAwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2018/6/8 16:24
 */
@Slf4j
@Service
public class QuartzAwardServiceImpl implements QuartzAwardService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private RankOpt rankOpt;

    @Resource
    private MedalMapper medalMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void initRankCaches() {
        List<Student> students = studentMapper.selectHasRank();
        initCache(students);
    }

    private void initCache(List<Student> students) {
        // 各个学生被膜拜的次数
        Map<Long, Map<Long, Long>> byWorshipCount = worshipMapper.countWorshipWithStudents(students);
        // 各个学生获取的勋章个数
        Map<Long, Map<Long, Long>> medalCount = awardMapper.countGetModelByStudents(students);
        // 各个学生获取的证书个数
        Map<Long, Map<Long, Long>> ccieCount = ccieMapper.countCcieByStudents(students);
        students.forEach(student -> {
            Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

            double goldCount = BigDecimalUtil.add(student.getOfflineGold(), student.getSystemGold());
            rankOpt.addOrUpdate(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), goldCount);
            rankOpt.addOrUpdate(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, student.getId(), goldCount);
            rankOpt.addOrUpdate(RankKeysConst.COUNTRY_GOLD_RANK, student.getId(), goldCount);
            log.info("学生[{} - {} - {}] 金币数：[{}]", student.getId(), student.getAccount(), student.getStudentName(), goldCount);

            Long count = 0L;
            String countStr = "count";
            if (ccieCount.get(student.getId()) != null && ccieCount.get(student.getId()).get(countStr) != null) {
                count = ccieCount.get(student.getId()).get(countStr);
            }
            rankOpt.addOrUpdate(RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.SCHOOL_CCIE_RANK + schoolAdminId, student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.COUNTRY_CCIE_RANK, student.getId(), count * 1.0);
            log.info("学生[{} - {} - {}] 证书个数：[{}]", student.getId(), student.getAccount(), student.getStudentName(), count);

            count = 0L;
            if (medalCount.get(student.getId()) != null && medalCount.get(student.getId()).get(countStr) != null) {
                count = medalCount.get(student.getId()).get(countStr);
            }
            rankOpt.addOrUpdate(RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.SCHOOL_MEDAL_RANK + schoolAdminId, student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.COUNTRY_MEDAL_RANK, student.getId(), count * 1.0);
            log.info("学生[{} - {} - {}] 勋章个数：[{}]", student.getId(), student.getAccount(), student.getStudentName(), count);

            count = 0L;
            if (byWorshipCount.get(student.getId()) != null && byWorshipCount.get(student.getId()).get(countStr) != null) {
                count = byWorshipCount.get(student.getId()).get(countStr);
            }
            rankOpt.addOrUpdate(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.SCHOOL_WORSHIP_RANK + schoolAdminId, student.getId(), count * 1.0);
            rankOpt.addOrUpdate(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId(), count * 1.0);
            log.info("学生[{} - {} - {}] 被膜拜次数：[{}]", student.getId(), student.getAccount(), student.getStudentName(), count);

        });
    }

    @Override
    public void initRankCache(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        List<Student> students = new ArrayList<>();
        students.add(student);
        initCache(students);
    }

    @Override
    public void initMonsterMedal() {
        List<Student> students = studentMapper.selectList(new EntityWrapper<Student>().isNotNull("account_time").ne("status", 3));
        Date date = new Date();
        List<Long> parentIds = this.getMedalIds();
        List<Medal> medals = medalMapper.selectByParentIds(parentIds);
        students.forEach(student -> {
            List<Award> awards = medals.stream().map(medal -> {
                Award award = new Award();
                award.setCanGet(2);
                award.setGetFlag(2);
                award.setType(3);
                award.setMedalType(medal.getId());
                award.setStudentId(student.getId());
                award.setCreateTime(date);
                award.setCurrentPlan(0);
                award.setTotalPlan(medal.getTotalPlan());
                return award;
            }).collect(Collectors.toList());
            awardMapper.insertList(awards);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 40 0 1 * ?")
    public void deleteRank() {

        if (checkPort(port)) {
            return;
        }

        log.info("定时清理缓存中冗余的排行信息开始...");
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.CLASS_WORSHIP_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.CLASS_CCIE_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.CLASS_GOLD_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.CLASS_MEDAL_RANK + "*"));

        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.SCHOOL_WORSHIP_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.SCHOOL_CCIE_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.SCHOOL_GOLD_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.SCHOOL_MEDAL_RANK + "*"));

        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.COUNTRY_WORSHIP_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.COUNTRY_CCIE_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.COUNTRY_GOLD_RANK + "*"));
        this.removeDeletedStudent(redisTemplate.keys(RankKeysConst.COUNTRY_MEDAL_RANK + "*"));
        log.info("定时清理缓存中冗余的排行信息结束...");

    }

    private void removeDeletedStudent(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        keys.parallelStream().forEach(key -> {
            Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
            if (CollectionUtils.isEmpty(members)) {
                return;
            }

            members.parallelStream().forEach(member -> {
                Student student = studentMapper.selectById((Serializable) member);
                if (Objects.isNull(student) || Objects.equals(student.getStatus(), 3)) {
                    log.info("id=[{}]的学生排行缓存被清除！key=[{}]", member, key);
                    redisTemplate.opsForZSet().remove(key, member);
                }
            });
        });
    }

    /**
     * 获取需要初始化的勋章id
     *
     * @return
     */
    private List<Long> getMedalIds() {
        List<Long> parentIds = new ArrayList<>();
        parentIds.add(110L);
        parentIds.add(111L);
        parentIds.add(112L);
        parentIds.add(113L);
        parentIds.add(114L);
        parentIds.add(115L);
        parentIds.add(116L);
        parentIds.add(117L);
        parentIds.add(118L);
        parentIds.add(119L);
        parentIds.add(120L);
        parentIds.add(121L);
        parentIds.add(122L);
        parentIds.add(123L);
        return parentIds;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ? ")
    public void deleteDailyAward() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时删除日奖励信息开始");
        simpleAwardMapper.deleteDailyAward();
        log.info("定时删除日奖励信息结束");
    }

}

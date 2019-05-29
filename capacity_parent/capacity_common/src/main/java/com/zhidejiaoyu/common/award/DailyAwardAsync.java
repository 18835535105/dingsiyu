package com.zhidejiaoyu.common.award;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * 异步保存日奖励信息
 *
 * @author wuchenxi
 * @date 2019-02-28
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class DailyAwardAsync extends BaseAwardAsync {

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private OpenUnitLogMapper openUnitLogMapper;

    @Autowired
    private AwardContentTypeMapper awardContentTypeMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 保存学生今日完成一个单元
     */
    public void todayLearnOneUnit(Student student) {
        Long studentId = student.getId();
        final long awardContentType = 2;
        // 查看学生今日开启单元个数
        Award award = awardMapper.selectByAwardContentTypeAndType(studentId, DAILY_TYPE, (int) awardContentType);

        try {
            if (this.checkAward(award, DAILY_TYPE)) {
                int openCount = openUnitLogMapper.countTodayOpenCount(studentId);
                optAward(studentId, awardContentType, openCount, award, DAILY_TYPE);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存日奖励信息出错"), e);
        }
    }

    /**
     * 今日完成/闯关成功10个单元闯关测试
     *
     * @param student
     */
    public void todayCompleteTenUnitTest(Student student) {
        Long studentId = student.getId();

        // 判断学生今日是否闯关成功10个单元闯关测试
        final int awardContentType1 = 4;

        try {
            // 闯关成功10个单元
            Award awardSuccess = awardMapper.selectByAwardContentTypeAndType(studentId, DAILY_TYPE, awardContentType1);
            if (this.checkAward(awardSuccess, DAILY_TYPE)) {
                int successCount = testRecordMapper.countUnitTest(studentId, 1);
                this.optAward(studentId, awardContentType1, successCount, awardSuccess, DAILY_TYPE);
            }

            // 闯关10个单元
            final int awardContentType2 = 3;
            Award award = awardMapper.selectByAwardContentTypeAndType(studentId, DAILY_TYPE, awardContentType2);
            if (this.checkAward(award, DAILY_TYPE)) {
                int completeCount = testRecordMapper.countUnitTest(studentId, 2);
                this.optAward(studentId, awardContentType2, completeCount, award, DAILY_TYPE);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存日奖励信息出错"), e);
        }
    }

    /**
     * 今日复习30个生词且记忆强度达到50%
     */
    public void todayReview(Student student) {
        Long studentId = student.getId();
        final int awardContentType = 7;
        Award award = awardMapper.selectByAwardContentTypeAndType(studentId, DAILY_TYPE, awardContentType);
        try {
            if (this.checkAward(award, DAILY_TYPE)) {
                int count = learnMapper.countTodayRestudyAndMemoryStrengthGePercentFifty(student);
                this.optAward(studentId, awardContentType, count, award, DAILY_TYPE);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存日奖励信息出错"), e);
        }
    }

    /**
     * 每日首次登录奖励
     *
     * @param student
     */
    public void firstLogin(Student student) {
        final int awardContentType = 1;
        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), DAILY_TYPE, awardContentType);
        try {
            if (this.checkAward(award, DAILY_TYPE)) {
                super.optAward(student.getId(), awardContentType, 1, award, DAILY_TYPE);
            }
            this.initDailyAward(student);
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存日奖励信息出错"), e);
        }
    }

    /**
     * 今日全校排行榜上升10名以上
     *
     * @param student
     */
    public void todayUpRank(Student student) {
        Long studentId = student.getId();

        final int awardContentType = 8;
        Award award = awardMapper.selectByAwardContentTypeAndType(student.getId(), DAILY_TYPE, awardContentType);
        try {
            if (this.checkAward(award, DAILY_TYPE)) {
                // 查询学生昨天的学校排名
                 RankList rankList = rankListMapper.selectByStudentId(studentId);
                int rank;
                if (rankList == null || rankList.getSchoolDayRank() == null) {
                    rank = studentMapper.countHasLoginLogStudentsBySchoolAdminId(this.getSchoolAdminId(student));
                } else {
                    rank = rankList.getSchoolDayRank();
                }

                // 学生当前全校排名
                int up;
                int currentRank = this.getCurrentSchoolRank(student);
                if (currentRank == 0) {
                    up = 0;
                } else {
                    up = Math.abs(rank - currentRank);
                }
                this.optAward(studentId, awardContentType, up, award, DAILY_TYPE);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "保存日奖励信息出错"), e);
        }
    }

    private Integer getSchoolAdminId(Student student) {
        if (student.getTeacherId() == null) {
            return null;
        }
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(Integer.valueOf(student.getTeacherId().toString()));
        if (schoolAdminId == null) {
            return Integer.valueOf(student.getTeacherId().toString());
        }
        return schoolAdminId;
    }

    public void initAward(Student student) {

        this.initGoldAward(student);

        this.initMedalAward(student);
    }

    /**
     * 初始化勋章信息
     *
     * @param student
     */
    private void initMedalAward(Student student) {
        Long studentId = student.getId();
        Date date = new Date();
        List<Long> parentIds = new ArrayList<>();
        parentIds.add(1L);
        parentIds.add(6L);
        parentIds.add(11L);
        parentIds.add(17L);
        parentIds.add(23L);
        parentIds.add(37L);
        parentIds.add(47L);
        parentIds.add(67L);
        parentIds.add(72L);
        parentIds.add(87L);
        parentIds.add(97L);
        List<Medal> medals = medalMapper.selectByParentIds(parentIds);
        List<Award> awards = new ArrayList<>(medals.size());
        medals.forEach(medal -> {
            Award award = new Award();
            award.setCanGet(2);
            award.setGetFlag(2);
            award.setType(MEDAL_TYPE);
            award.setMedalType(medal.getId());
            award.setStudentId(studentId);
            award.setCreateTime(date);
            award.setCurrentPlan(0);
            award.setTotalPlan(medal.getTotalPlan());
            awards.add(award);
        });

        try {
            awardMapper.insertList(awards);
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "初始化学生勋章信息失败"), e);
        }
    }

    /**
     * 初始化金币奖励
     *
     * @param student
     */
    private void initGoldAward(Student student) {
        Long studentId = student.getId();
        Date date = new Date();
        List<Long> ids = new ArrayList<>();
        // 学习总有效时长相关 id
        final long validTimeStartId = 15L;
        final long validTimeEndId = 25L;
        for (long i = validTimeStartId; i < validTimeEndId; i++) {
            ids.add(i);
        }

        // 单元闯关成功相关 id
        final long successUnitTestStartId = 105;
        final long successUnitTestEndId = 115;
        for (long i = successUnitTestStartId; i < successUnitTestEndId; i++) {
            ids.add(i);
        }
        initSaveAwards(student, studentId, date, ids, GOLD_TYPE);
    }

    private void initSaveAwards(Student student, Long studentId, Date date, List<Long> ids, int type) {
        List<AwardContentType> contentTypes = awardContentTypeMapper.selectByIds(ids);
        List<Award> awards = new ArrayList<>(contentTypes.size());
        contentTypes.forEach(contentType -> {
            Award award = new Award();
            award.setCanGet(2);
            award.setGetFlag(2);
            award.setStudentId(studentId);
            award.setCreateTime(date);
            award.setType(type);
            award.setCurrentPlan(0);
            award.setTotalPlan(contentType.getTotalPlan());
            award.setAwardContentType(contentType.getId());
            awards.add(award);
        });

        if (awards.size() > 0) {
            try {
                awardMapper.insertList(awards);
            } catch (Exception e) {
                if (type == DAILY_TYPE) {
                    log.error(super.logErrorMsg(student, "初始化日奖励失败"), e);
                } else {
                    log.error(super.logErrorMsg(student, "初始化金币奖励失败"), e);
                }

            }
        }
    }

    /**
     * 初始化日奖励信息
     *
     * @param student
     */
    private void initDailyAward(Student student) {
        Long studentId = student.getId();
        Date date = new Date();
        List<Long> ids = new ArrayList<>();
        ids.add(2L);
        ids.add(3L);
        ids.add(4L);
        ids.add(7L);
        ids.add(8L);
        ids.add(9L);
        initSaveAwards(student, studentId, date, ids, DAILY_TYPE);
    }

    /**
     * 获取学生当前全校排名
     *
     * @param student
     * @return
     */
    private int getCurrentSchoolRank(Student student) {
        Integer schoolAdminById = teacherMapper.getSchoolAdminById(student.getTeacherId() == null ? 0 : Integer.valueOf(student.getTeacherId().toString()));
        Map<Long, Map<String, Object>> schoolLevelMap = studentMapper.selectLevelByStuId(student, 2 , schoolAdminById);
        if (schoolLevelMap.get(student.getId()) != null && schoolLevelMap.get(student.getId()).get("rank") != null) {
            return parseInt(schoolLevelMap.get(student.getId()).get("rank").toString().split("\\.")[0]);
        }
        return 0;
    }
}

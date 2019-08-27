package com.zhidejiaoyu.common.award;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.MedalConstant;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.StudentUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 异步保存勋章奖励信息
 *
 * @author wuchenxi
 * @date 2019-03-19
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class MedalAwardAsync extends BaseAwardAsync {

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private RankOpt rankOpt;

    @Autowired
    private StudentExpansionMapper studentExpansionMapper;

    /**
     * 初出茅庐勋章
     * 任意课程单元下，首次完成‘慧记忆、慧听写、慧默写、例句听力、例句翻译’五个模块，依次点亮L1、L2、L3、L4、L5。
     *
     * @param student
     */
    public void inexperienced(Student student) {
        Long studentId = student.getId();

        int length = MedalConstant.INEXPERIENCED_TOTAL_PLAN.length;
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, (long) MedalConstant.INEXPERIENCED_TOTAL_PLAN[length - 1]);
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                // 学生所有课程下学习的模块
                List<String> studyModels = learnMapper.selectLearnedModelByStudent(student);
                int modelSize = studyModels.size();
                for (int i = 0; i < length; i++) {
                    award = awardMapper.selectByStudentIdAndMedalType(studentId, (long) MedalConstant.INEXPERIENCED_TOTAL_PLAN[i]);
                    if (this.checkAward(award, MEDAL_TYPE)) {
                        super.optAward(studentId, MedalConstant.INEXPERIENCED_TOTAL_PLAN[i], modelSize, award, MEDAL_TYPE);
                    }
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 更新辉煌荣耀勋章
     *
     * @param student
     * @param validTime
     * @param onlineTime
     */
    public void honour(Student student, Integer validTime, Integer onlineTime) {
        double efficiency = BigDecimalUtil.div(validTime, onlineTime, 2);

        // 辉煌荣耀子勋章id
        List<Medal> children = medalMapper.selectChildrenIdByParentId(6);

        // 获取勋章奖励信息
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        if (awards.size() > 0) {
            Date date = new Date();
            int size = awards.size();
            Award award;
            int currentPlan = 0;
            try {
                for (int i = 0; i < size; i++) {
                    award = awards.get(i);
                    // 总进度为空或者总进度不等于需要完成的总进度，重新设置奖励总进度
                    if (award.getTotalPlan() == null || award.getTotalPlan() != MedalConstant.HONOUR_TOTAL_PLAN[i]) {
                        award.setTotalPlan(MedalConstant.HONOUR_TOTAL_PLAN[i]);
                    }
                    // 如果总进度!=当前完成进度，更新当前完成进度
                    if (!Objects.equals(award.getTotalPlan(), award.getCurrentPlan())) {
                        // 学习效率 >= 需要完成的学习效率，并且当前记录不是今天更新的，进度++
                        if (efficiency >= MedalConstant.HONOUR_TARGET_PLAN[i]
                                && !Objects.equals(DateUtil.formatYYYYMMDD(award.getCreateTime()), DateUtil.formatYYYYMMDD(date))) {
                            if (award.getCurrentPlan() == null) {
                                currentPlan = 1;
                            } else {
                                currentPlan = award.getCurrentPlan() + 1;
                            }
                        }
                        super.optAward(student.getId(), award.getMedalType(), currentPlan, award, MEDAL_TYPE);
                    }
                }
            } catch (Exception e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }

    /**
     * 每次当天第一次登录如果学生辉煌荣耀等级没有更新，将该奖励金都置为0
     *
     * @param student
     */
    public void updateHonour(Student student) {
        // 辉煌荣耀子勋章id
        List<Medal> children = medalMapper.selectChildrenIdByParentId(6);

        // 获取勋章奖励信息
        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(student, children);

        if (awards.size() > 0) {
            try {
                Award award;
                int size = awards.size();
                for (int i = 0; i < size; i++) {
                    award = awards.get(i);
                    if (award.getTotalPlan() == null) {
                        award.setTotalPlan(MedalConstant.HONOUR_TOTAL_PLAN[i]);
                    }
                    // 如果总进度!=当前完成进度并且昨天没有更新（昨天不更新说明没有达到条件）,将当前进度置为0
                    if (!Objects.equals(award.getTotalPlan(), award.getCurrentPlan())
                            && !Objects.equals(DateUtil.formatYYYYMMDD(award.getCreateTime()), DateUtil.formatYYYYMMDD(new DateTime().minusDays(1).toDate()))) {
                        award.setCurrentPlan(0);
                    }
                    awardMapper.updateById(award);
                }
            } catch (Exception e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }


    /**
     * 天道酬勤:LV1 本校今日排行至少前进一名
     * LV2 本校今日排名至少前进五名
     * LV3 本校今日排名至少前进十名
     * LV4 全国今日排名至少前进三十名
     * LV5 全国今日排名至少前进五十名
     *
     * @param student
     */
    public void upLevel(Student student, Integer schoolAdminId) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(17);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        if (super.checkAward(award, MEDAL_TYPE)) {
            // 学生当前排行信息
            RankList rankList = rankListMapper.selectByStudentId(studentId);
            int schoolDayRank;
            int countryDayRank;
            if (rankList == null) {
                schoolDayRank = studentMapper.countHasLoginLogStudentsBySchoolAdminId(schoolAdminId);
                countryDayRank = studentMapper.selectCount(new EntityWrapper<Student>().isNotNull("account_time").in("role", new Object[]{1, 2}).ne("status", 3));
            } else {
                schoolDayRank = rankList.getSchoolDayRank() == null ? 0 : rankList.getSchoolDayRank();
                countryDayRank = rankList.getCountryDayRank() == null ? 0 : rankList.getCountryDayRank();
            }
            try {
                // 学生当前全校排名
                Map<Long, Map<String, Object>> currentSchoolRank = studentMapper.selectLevelByStuId(student, 2, schoolAdminId);
                // 学生当前全国排名
                Map<Long, Map<String, Object>> currentCountryRank = studentMapper.selectLevelByStuId(student, 3, null);

                double schoolRank = 0;
                if (currentSchoolRank != null && currentSchoolRank.get(student.getId()) != null && currentSchoolRank.get(student.getId()).get("rank") != null) {
                    schoolRank = Double.parseDouble(currentSchoolRank.get(student.getId()).get("rank").toString());
                }

                double countryRank = 0;
                if (currentCountryRank != null && currentCountryRank.get(student.getId()) != null && currentCountryRank.get(student.getId()).get("rank") != null) {
                    countryRank = Double.parseDouble(currentCountryRank.get(student.getId()).get("rank").toString());
                }

                Long medalId;
                // 变化名次
                int change;
                for (int i = 0; i < 5; i++) {
                    medalId = children.get(i).getId();
                    award = awardMapper.selectByStudentIdAndMedalType(studentId, medalId);
                    if (i < 3) {
                        if (super.checkAward(award, MEDAL_TYPE)) {
                            change = (int) (schoolDayRank - schoolRank);
                            super.optAward(studentId, medalId, change >= 0 ? change : award.getCurrentPlan(), award, MEDAL_TYPE);
                        }
                    } else {
                        if (super.checkAward(award, MEDAL_TYPE)) {
                            change = (int) (countryDayRank - countryRank);
                            super.optAward(studentId, medalId, change >= 0 ? change : award.getCurrentPlan(), award, MEDAL_TYPE);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }

    /**
     * 学霸崛起
     * <ul>
     * 所有测试首次获得满分，点亮学霸崛起Lv1
     * 所有课程的首次测试累计五次获得满分，点亮学霸崛起Lv2
     * 所有课程的首次测试累计十次获得满分，点亮学霸崛起Lv3
     * 所有课程的首次测试累计二十次获得满分，点亮学霸崛起Lv4
     * 所有课程的首次测试累计五十次获得满分，点亮学霸崛起Lv5
     * </ul>
     *
     * @param student
     */
    public void superStudent(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(23);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        if (!super.checkAward(award, MEDAL_TYPE)) {
            return;
        }

        // 学生测试获得满分的次数（同一个单元不重复计算）
        int size = 0;
        List<TestRecord> testRecords = testRecordMapper.selectFullPoint(studentId);
        if (testRecords.size() > 0) {
            Map<String, Long> collect = testRecords.stream().collect(Collectors.groupingBy(testRecord -> testRecord.getCourseId() + "-" + testRecord.getUnitId(), Collectors.counting()));
            size = collect.size();
        }

        try {
            for (Medal child : children) {
                award = awardMapper.selectByStudentIdAndMedalType(studentId, child.getId());
                if (this.checkAward(award, MEDAL_TYPE)) {
                    super.optAward(studentId, child.getId(), size, award, MEDAL_TYPE);
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 最有潜力
     * <ul>
     * <li>所有测试成绩未及格总次数达到5次，点亮LV1。</li>
     * <li>达到10次，点亮LV2。</li>
     * <li>达到20次，点亮LV3。</li>
     * <li>达到50次，点亮LV4。</li>
     * <li>达到100次，点亮LV5。</li>
     * </ul>
     *
     * @param student
     */
    public void potentialMan(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(97);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                // 测试不及格总次数
                int totalTestField = testRecordMapper.countTestFailByStudent(student);
                packagePlan(studentId, children, totalTestField);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 资深队员：
     * <ul>
     * <li>在线登录时长达一个月，点亮LV1。</li>
     * <li>达五个月，点亮LV2。</li>
     * <li>达一年，点亮LV3。</li>
     * <li>达两年，点亮LV4。</li>
     * <li>达三年，点亮LV5。</li>
     * </ul>
     *
     * @param student
     */
    public void oldMan(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(87);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                // 在线总时长
                Long totalOnlineTime = durationMapper.countTotalOnlineTime(student);
                // 总天数
                int totalDays = totalOnlineTime == null ? 0 : (int) (totalOnlineTime / 86400);
                packagePlan(studentId, children, totalDays);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 小试牛刀：
     * 全部课程‘慧记忆’熟词累计10个，点亮LV1。
     * 累计50个，点亮LV2。
     * 累计100个，点亮LV3。
     * 累计300个，点亮LV4。
     * 累计1000个，点亮LV5。
     * <p>
     * 学习狂人：
     * 全部课程‘慧听写’熟词累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 数一数二：
     * 全部课程‘慧默写’熟词累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 金榜题名：
     * 全部课程‘例句听力’熟句累计50个，点亮LV1。
     * 累计200个，点亮LV2。
     * 累计500个，点亮LV3。
     * 累计1000个，点亮LV4。
     * 累计2000个，点亮LV5。
     * <p>
     * 出类拔萃：
     * 翻译句子累计50句，点亮LV1。
     * 累计200句，点亮LV2。
     * 累计500句，点亮LV3。
     * 累计1000句，点亮LV4。
     * 累计2000句，点亮LV5。
     * <p>
     * 功成名就：
     * 默写句子累计50句，点亮LV1。
     * 累计200句，点亮LV2。
     * 累计500句，点亮LV3。
     * 累计1000句，点亮LV4。
     * 累计2000句，点亮LV5。
     *
     * @param student
     * @param model   1:慧记忆;2:慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     */
    public void tryHand(Student student, int model) {
        if (model == 0) {
            return;
        }
        Long studentId = student.getId();
        List<Medal> children = null;
        switch (model) {
            case 1:
                children = medalMapper.selectChildrenIdByParentId(37);
                break;
            case 2:
                children = medalMapper.selectChildrenIdByParentId(42);
                break;
            case 3:
                children = medalMapper.selectChildrenIdByParentId(47);
                break;
            case 4:
                children = medalMapper.selectChildrenIdByParentId(52);
                break;
            case 5:
                children = medalMapper.selectChildrenIdByParentId(57);
                break;
            case 6:
                children = medalMapper.selectChildrenIdByParentId(62);
                break;
            default:
        }
        if (children != null) {
            // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
            Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
            try {
                if (super.checkAward(award, MEDAL_TYPE)) {
                    // 熟词、熟句总数
                    int count = learnMapper.countKnownCountByStudentId(student, model);
                    packagePlan(studentId, children, count);
                }
            } catch (Exception e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }

    /**
     * 勋章达人：
     * 点亮其他所有勋章方可获得。
     *
     * @param student
     */
    public void expertMan(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(67);
        // 如果已领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                // 勋章总个数
                int totalMedal = awardMapper.countTotalMedal(studentId);
                int getModel = awardMapper.countGetModel(studentId);
                if (totalMedal == getModel + 1) {
                    super.optAward(studentId, 67, 1, award, MEDAL_TYPE);
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 女神勋章（男神勋章）：
     * 达到‘名列前茅’级后点亮
     *
     * @param student
     */
    public void godMan(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(72);
        // 如果已领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                double totalGold = BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
                Level level = levelMapper.selectByPrimaryKey(13L);
                if (totalGold >= level.getGold()) {
                    super.optAward(studentId, 72, 1, award, MEDAL_TYPE);
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }

    /**
     * 众望所归：
     * 被膜拜一次，点亮LV1。
     * 被膜拜十次，点亮LV2。
     * 被膜拜三十次，点亮LV3。
     * 被膜拜五十次，点亮LV4。
     * 被膜拜一百次，点亮LV5。
     *
     * @param student
     */
    public void enjoyPopularConfidence(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(77);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                int byWorshipCount = worshipMapper.countByWorship(studentId);
                packagePlan(studentId, children, byWorshipCount);
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }


    /**
     * 拔得头筹
     * <p>
     * 本校金币排行前三名保持1分钟，点亮 拔得头筹 LV1
     * 本校金币排行前三名保持1小时，点亮 拔得头筹 LV2。
     * 本校金币排行前三名保持1天，点亮 拔得头筹 LV3。
     * 本校金币排行第一名保持1天，点亮 拔得头筹 LV4。
     * 本校金币排行第一名保持1周，点亮 拔得头筹 LV5。
     *
     * @param student
     */
    public void theFirst(Student student) {

        Integer schoolAdminId = super.getSchoolAdminId(student);

        // 计算金币排行前三名的情况
        this.saveBetterThree(student, schoolAdminId);

        // 计算金币排行全校第一名的情况
        this.saveTheFirst(student, schoolAdminId);
    }

    /**
     * 计算金币排行全校第一名的情况
     *
     * @param student
     * @param schoolAdminId
     */
    private void saveTheFirst(Student student, Integer schoolAdminId) {
        List<Long> theFirstStudentIds = rankOpt.getReverseRangeMembersBetweenStartAndEnd(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, 0L, 1L);

        // 查询有全校排名第一标识的学生，之前是全校第一名但当前不是第一名的学生信息
        List<Student> students = StudentUtil.getStudentBySchoolAdmin(schoolAdminId)
                .parallelStream()
                .filter(student1 -> student.getSchoolGoldFirstTime() != null && !Objects.equals(student1.getId(), theFirstStudentIds.get(0)))
                .collect(Collectors.toList());

        if (theFirstStudentIds.contains(student.getId())) {
            // 当前学生是全校第一名
            this.calculateTheFirst(student);

            if (students.size() > 0) {
                students.forEach(student1 -> {
                    // 计算这些学生拔得头筹勋章
                    this.calculateTheFirst(student);
                    student1.setSchoolGoldFirstTime(null);
                    studentMapper.updateById(student1);
                });
            }
        }
    }

    /**
     * 计算学生全校第一名保持时间，并更新拔得头筹勋章
     *
     * @param student
     */
    private void calculateTheFirst(Student student) {
        if (student.getSchoolGoldFirstTime() == null) {
            return;
        }
        // 当前学生在全校第一名
        long keepTime = (System.currentTimeMillis() - student.getSchoolGoldFirstTime().getTime()) / 60000;
        if (keepTime >= 1) {
            // 判断是否已经点亮： 本校金币排行第一名保持1周，点亮 拔得头筹 LV5。
            Award award = awardMapper.selectByStudentIdAndMedalType(student.getId(), 15L);
            try {
                if (super.checkAward(award, MEDAL_TYPE)) {
                    List<Long> children = new ArrayList<>(2);
                    children.add(14L);
                    children.add(15L);
                    for (long childId : children) {
                        award = awardMapper.selectByStudentIdAndMedalType(student.getId(), childId);
                        super.optAward(student.getId(), childId, (int) keepTime, award, MEDAL_TYPE);
                    }
                }
            } catch (Exception e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }

    /**
     * 计算全校金币排行前三名的情况
     *
     * @param student
     * @param schoolAdminId
     */
    private void saveBetterThree(Student student, Integer schoolAdminId) {

        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion == null) {
            log.error("学生[{} - {} - {}] 的扩展信息 studentExpansion = null", student.getId(), student.getAccount(), student.getStudentName());
            return;
        }

        // 查询本校前 3 名的学生
        List<Long> betterThreeStudentIds = rankOpt.getReverseRangeMembersBetweenStartAndEnd(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, 0L, 3L);

        if (betterThreeStudentIds.contains(student.getId())) {
            // 当前学生在本校的前三名中
            // 如果学生之前就在前三名中，计算其保持的时间
            // 如果学生之前不在前三名中，将其进入前三名的时间更新为当前时间
            if (studentExpansion.getBetterThreeTime() == null) {
                studentExpansion.setBetterThreeTime(new Date());
                studentExpansionMapper.updateById(studentExpansion);
            } else {
                this.calculateTheFirstMedal(student, studentExpansion);
            }

            // 计算跌出全校前三名的学生勋章
            this.calculateOtherStudentTheFirstMedal(schoolAdminId, betterThreeStudentIds);
        }
    }

    /**
     * 统计跌出全校前三名的学生拔得头筹勋章
     *
     * @param schoolAdminId         校管 id
     * @param betterThreeStudentIds 之前全校前三名的学生 id
     */
    private void calculateOtherStudentTheFirstMedal(Integer schoolAdminId, List<Long> betterThreeStudentIds) {
        List<StudentExpansion> studentExpansions = studentExpansionMapper.selectBySchoolAdminId(schoolAdminId);
        if (studentExpansions.size() > 0) {
            Map<Long, List<StudentExpansion>> collect = studentExpansions
                    .stream()
                    .filter(studentExpansion -> studentExpansion.getBetterThreeTime() != null)
                    .collect(Collectors.groupingBy(StudentExpansion::getStudentId));
            if (collect.size() == 0) {
                return;
            }

            betterThreeStudentIds.forEach(id -> {
                if (!collect.containsKey(id)) {
                    // 说明学生之前在全校前三名，现在已经不在前三名了，计算其勋章
                    Student student = studentMapper.selectById(id);
                    StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
                    if (studentExpansion == null) {
                        log.error("学生[{} - {} - {}] 的扩展信息 studentExpansion = null", student.getId(), student.getAccount(), student.getStudentName());
                        return;
                    }

                    this.calculateTheFirstMedal(student, studentExpansion);

                    studentExpansion.setBetterThreeTime(null);
                    studentExpansionMapper.updateById(studentExpansion);
                }
            });
        }
    }

    /**
     * 计算拔得头筹前三名的勋章获取情况
     *
     * @param student          学生信息
     * @param studentExpansion 学生扩展信息
     */
    private void calculateTheFirstMedal(Student student, StudentExpansion studentExpansion) {
        if (studentExpansion.getBetterThreeTime() == null) {
            return;
        }
        long keepTime = (System.currentTimeMillis() - studentExpansion.getBetterThreeTime().getTime()) / 60000;
        if (keepTime >= 1) {
            // 判断是否已经点亮： 本校金币排行前三名保持1天，点亮 拔得头筹 LV3。
            Award award = awardMapper.selectByStudentIdAndMedalType(student.getId(), 13L);
            try {
                if (super.checkAward(award, MEDAL_TYPE)) {
                    List<Long> children = new ArrayList<>(3);
                    children.add(11L);
                    children.add(12L);
                    children.add(13L);
                    for (long childId : children) {
                        award = awardMapper.selectByStudentIdAndMedalType(student.getId(), childId);
                        super.optAward(student.getId(), childId, (int) keepTime, award, MEDAL_TYPE);
                    }
                }
            } catch (Exception e) {
                log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
            }
        }
    }

    /**
     * @param studentId
     * @param children
     * @param currentPlan
     */
    private void packagePlan(Long studentId, List<Medal> children, int currentPlan) {
        Award award;
        for (Medal child : children) {
            award = awardMapper.selectByStudentIdAndMedalType(studentId, child.getId());
            if (super.checkAward(award, MEDAL_TYPE)) {
                super.optAward(studentId, child.getId(), currentPlan, award, MEDAL_TYPE);
            }
        }
    }

    /**
     * 重置天道酬勤勋章中未达到领取条件的勋章
     *
     * @param student
     */
    public void resetLevel(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(17);
        Award award;
        try {
            for (Medal medal : children) {
                award = awardMapper.selectByStudentIdAndMedalType(studentId, medal.getId());
                if (award != null && !Objects.equals(award.getCurrentPlan(), award.getTotalPlan())) {
                    award.setCurrentPlan(0);
                    awardMapper.updateById(award);
                }
            }
        } catch (Exception e) {
            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
        }
    }
}

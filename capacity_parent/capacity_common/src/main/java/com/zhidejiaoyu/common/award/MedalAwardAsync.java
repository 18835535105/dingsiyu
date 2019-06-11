package com.zhidejiaoyu.common.award;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.MedalConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                        super.optAward(studentId, (long) MedalConstant.INEXPERIENCED_TOTAL_PLAN[i], modelSize, award, MEDAL_TYPE);
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
                countryDayRank = studentMapper.selectCount(new EntityWrapper<Student>().isNotNull("account_time"));
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
     * <li>所有测试首次满分，点亮LV1</li>
     * <li>连续五次，点亮LV2。</li>
     * <li>连续十次，点亮LV3。</li>
     * <li>连续十五次，点亮LV4。</li>
     * <li>连续二十次，点亮LV5。</li>
     * </ul>
     *
     * @param student
     */
    public void superStudent(Student student) {
        Long studentId = student.getId();
        List<Medal> children = medalMapper.selectChildrenIdByParentId(23);
        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
        Award award = awardMapper.selectByStudentIdAndMedalType(studentId, children.get(children.size() - 1).getId());
        try {
            if (super.checkAward(award, MEDAL_TYPE)) {
                Long medalId;
                // 上次测试得分
                Integer prePoint = testRecordMapper.selectPrePoint(studentId);
                // 如果上次测试是100分，进度+1
                if (prePoint != null) {
                    for (Medal child : children) {
                        medalId = child.getId();
                        award = awardMapper.selectByStudentIdAndMedalType(studentId, medalId);
                        if (award == null) {
                            if (prePoint == 100) {
                                super.optAward(studentId, medalId, 1, null, MEDAL_TYPE);
                            } else {
                                super.optAward(studentId, medalId, 0, null, MEDAL_TYPE);
                            }
                        } else if (!Objects.equals(award.getCurrentPlan(), award.getTotalPlan())) {
                            // 如果当前奖励还没有完成
                            // 分数为100当前进度+1
                            if (prePoint == 100) {
                                super.optAward(studentId, medalId, award.getCurrentPlan() + 1, award, MEDAL_TYPE);
                            } else {
                                // 分数不等于100当前进度清零
                                super.optAward(studentId, medalId, 1, award, MEDAL_TYPE);
                            }
                        }
                    }
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
     * 值得元老：
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
                int totalMonth = totalOnlineTime == null ? 0 : (int) (totalOnlineTime / 2592000);
                packagePlan(studentId, children, totalMonth);
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
     *
     * 本校金币总数第一名保持一分钟，点亮 拔得头筹 LV1
     * 本校金币总数第一名保持一小时，点亮 拔得头筹 LV2。
     * 本校金币总数第一名保持两小时，点亮 拔得头筹 LV3。
     * 本校金币总数第一名保持一天，点亮 拔得头筹 LV4。
     * 本校金币总数第一名保持一周，点亮 拔得头筹 LV5。
     * @param student
     */
    public void theFirst(Student student) {
        Integer schoolAdminId = super.getSchoolAdminId(student);
        // 查询本校最高的金币数的学生
        Student maxGoldStudent = studentMapper.selectMaxGoldForSchool(schoolAdminId);
        if (maxGoldStudent == null) {
            // 还没有学生获取拔得头筹，给当前学生更新获取拔得头筹的时间
            student.setSchoolGoldFirstTime(new Date());
            studentMapper.updateById(student);
        } else {
            // 已经有学生获取了拔得头筹
            double maxGold = maxGoldStudent.getSystemGold() + maxGoldStudent.getOfflineGold();
            double myGold = student.getSystemGold() + student.getOfflineGold();
            if (myGold == maxGold) {
                student.setSchoolGoldFirstTime(new Date());
                studentMapper.updateById(student);
            } else if (myGold > maxGold) {

                List<Medal> children = medalMapper.selectChildrenIdByParentId(11);

                // 当前学生金币在本校第一名
                student.setSchoolGoldFirstTime(new Date());
                studentMapper.updateById(student);

                // 查询本校所有金币总和为 maxGold 的学生信息
                List<Student> students = studentMapper.selectMaxGoldForGold(schoolAdminId, maxGold);
                // 计算保持时间，并保存相应的勋章奖励
                students.parallelStream().forEach(maxStudent -> {
                    if (maxStudent.getSchoolGoldFirstTime() != null) {
                        // 如果最后一个奖励条件已达成，说明其之前奖励都已能领取，不再进行其他计算
                        Award award = awardMapper.selectByStudentIdAndMedalType(student.getId(), children.get(children.size() - 1).getId());
                        try {
                            if (super.checkAward(award, MEDAL_TYPE)) {
                                // 保持的分钟数
                                int keepTime = (int) ((System.currentTimeMillis() - maxStudent.getSchoolGoldFirstTime().getTime()) / 60000);
                                for (Medal child : children) {
                                    award = awardMapper.selectByStudentIdAndMedalType(student.getId(), child.getId());
                                    super.optAward(student.getId(), child.getId(), keepTime, award, MEDAL_TYPE);
                                }
                            }
                        } catch (Exception e) {
                            log.error(super.logErrorMsg(student, "操作勋章信息失败"), e);
                        }
                    }
                });
                // 去掉金币排名不再是学校第一名学生的时间标识
                if (students.size() > 0) {
                    studentMapper.updateSchoolGoldFirstTimeToNull(students);
                }
            }
        }
    }

    /**
     *
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

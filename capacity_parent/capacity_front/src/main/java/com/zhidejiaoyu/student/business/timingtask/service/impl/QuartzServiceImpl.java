package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.UnitFeignClient;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzService;
import com.zhidejiaoyu.student.common.redis.AwardRedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2018/6/8 16:24
 */
@Slf4j
@Service
public class QuartzServiceImpl implements QuartzService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private SimpleStudentMapper simpleStudentMapper;

    @Resource
    private SimpleRankListMapper simpleRankListMapper;

    @Resource
    private SimpleNewsMapper simpleNewsMapper;

    @Resource
    private SimpleStudentRankMapper simpleStudentRankMapper;

    @Resource
    private SimpleAwardMapper simpleAwardMapper;

    @Resource
    private SimpleWorshipMapper simpleWorshipMapper;

    @Resource
    private AwardRedisOpt awardRedisOpt;

    @Resource
    private SimpleTeacherMapper simpleTeacherMapper;

    @Resource
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Resource
    private GauntletMapper gauntletMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SimpleLevelMapper simpleLevelMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private AwardMapper awardMapper;

    @Resource
    private CcieMapper ccieMapper;

    @Resource
    private LocationMapper locationMapper;

    @Resource
    private RankOpt rankOpt;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private TestRecordInfoMapper testRecordInfoMapper;
    @Resource
    private LearnMapper learnMapper;
    @Resource
    private SimpleSimpleCapacityMapper simpleCapacityMapper;
    @Resource
    private CapacityListenMapper capacityListenMapper;
    @Resource
    private CapacityMemoryMapper capacityMemoryMapper;
    @Resource
    private CapacityPictureMapper capacityPictureMapper;
    @Resource
    private CapacityWriteMapper capacityWriteMapper;
    @Resource
    private SentenceWriteMapper sentenceWriteMapper;
    @Resource
    private SentenceListenMapper sentenceListenMapper;
    @Resource
    private SentenceTranslateMapper sentenceTranslateMapper;
    @Resource
    private RunLogMapper runLogMapper;
    @Resource
    private GoldLogMapper goldLogMapper;
    @Resource
    private SimpleStudentExchangePrizeMapper studentExchangePrizeMapper;
    @Resource
    private StudentRestudyMapper studentRestudyMapper;
    @Resource
    private DurationMapper durationMapper;
    @Resource
    private MessageBoardMapper messageBoardMapper;
    @Resource
    private SimpleDrawRecordMapper drawRecordMapper;
    @Resource
    private GameScoreMapper gameScoreMapper;
    @Resource
    private LetterListenMapper letterListenMapper;
    @Resource
    private LetterPairMapper letterPairMapper;
    @Resource
    private LetterWriteMapper letterWriteMapper;
    @Resource
    private OpenUnitLogMapper openUnitLogMapper;
    @Resource
    private SimpleStudentUnitMapper simpleStudentUnitMapper;
    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;
    @Resource
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Resource
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;
    @Resource
    private RecycleBinMapper recycleBinMapper;
    @Resource
    private LearnHistoryMapper learnHistoryMapper;
    @Resource
    private ErrorLearnLogMapper errorLearnLogMapper;
    @Resource
    private ErrorLearnLogHistoryMapper errorLearnLogHistoryMapper;
    @Resource
    private UnitFeignClient unitFeignClient;
    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;
    @Autowired
    private CourseFeignClient courseFeignClient;
    @Resource
    private SchoolTimeMapper schoolTimeMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;
    @Resource
    private WeekHistoryPlanMapper weekHistoryPlanMapper;
    @Resource
    private TotalHistoryPlanMapper totalHistoryPlanMapper;
    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    /**
     * 每日 00:10:00 更新提醒消息中学生账号到期提醒
     */
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 10 0 * * ?")
    @Override
    public void updateNews() {
        if (checkPort(port)) {
            return;
        }

        log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 开始执行...");

        // 对距离有效期还剩3天的学生进行消息提醒
        // 查询小于等于3天到达有效期的学生
        List<Student> students = simpleStudentMapper.selectAccountTimeLessThreeDays();
        List<Long> ids = new ArrayList<>();
        for (Student student : students) {
            ids.add(student.getId());
        }

        if (ids.size() == 0) {
            log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 没有需要提醒的学生.");
            return;
        }

        // 根据学生id查询消息
        List<News> newsList = simpleNewsMapper.selectByStuIds(ids);

        // key:studentId    value:news
        Map<Long, News> map = new HashMap<>(16);
        for (News news : newsList) {
            map.put(news.getStudentid(), news);
        }

        List<News> updateList = new ArrayList<>();
        List<News> insertList = new ArrayList<>();
        News news;
        for (Student student : students) {
            if (map.containsKey(student.getId())) {
                // 提醒消息已存在，更新
                news = map.get(student.getId());
                news.setTitle(this.getDay(student.getAccountTime()));
                news.setTime(new Date());
                updateList.add(news);
            } else {
                // 消息不存在，新增
                news = new News();
                news.setTime(new Date());
                news.setTitle(this.getDay(student.getAccountTime()));
                news.setContent("亲爱的用户，你的账户即将在" + DateUtil.formatYYYYMMDD(student.getAccountTime()) + "到期,请及时续费，否则将对您产生无法登陆平台的影响，请知晓。");
                news.setStudentid(student.getId());
                news.setType("提醒消息");
                news.setRobotspeak("我们还会再见面了吗？在不续费我们就挥手再见了。");
                news.setRead(2);
                insertList.add(news);
            }
        }

        // 更新消息
        if (updateList.size() > 0) {
            try {
                simpleNewsMapper.updateByList(updateList);
            } catch (Exception e) {
                log.error("批量修改学生有效期倒计时提醒消息出错！", e);
            }
        }

        // 新增消息
        if (insertList.size() > 0) {
            try {

                simpleNewsMapper.insertList(insertList);
            } catch (Exception e) {
                log.error("批量增加学生账号有效期到期提醒消息出错", e);
            }
        }
        log.info("定时任务 -> 更新提醒消息中学生账号到期提醒 执行完成.");
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("0.1").equals(new BigDecimal("0.1")));
        System.out.println(new BigDecimal("0.1").equals(new BigDecimal("0.2")));
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 20 0 * * ?")
    @Override
    public void updateEnergy() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 能量清零...");
        simpleStudentMapper.updEnergyByAll();
        log.info("定时任务 -> 能量清零  执行完成...");
        log.info("定时任务 -> 教师创建学生清零...");
        simpleTeacherMapper.updateCreateStudentNumber();
        log.info("定时任务 -> 教师创建学生清零 执行完成...");
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 30 0 * * ?")
    @Override
    public void updateFrozen() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 给每个冻结用户增加一天...");
        List<Student> studentList = studentMapper.getAllFrozenStudent();
        for (Student student : studentList) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date time = c.getTime();
            student.setAccountTime(time);
            studentMapper.updateById(student);
        }
        log.info("定时任务 -> 给每个冻结用户增加一天结束...");
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 5 0 1 * ? ")
    @Override
    public void updateClassMonthRank() {
        if (checkPort(port)) {
            return;
        }
        // 班级与学生对应关系
        List<Student> students = simpleStudentMapper.selectList(new QueryWrapper<Student>().isNotNull("account_time").gt("system_gold", 0));

        // 存放各个班级下所有学生信息
        Map<Long, List<Student>> studentClassMap = new HashMap<>(16);
        students.parallelStream().forEach(student -> {
            if (student != null) {
                List<Student> studentList = null;
                if (studentClassMap.containsKey(student.getClassId())) {
                    studentList = studentClassMap.get(student.getClassId());
                }
                if (studentList == null) {
                    studentList = new ArrayList<>();
                }
                studentList.add(student);
                studentClassMap.put(student.getClassId(), studentList);
            }
        });

        int size = students.size();

        // 更新学生班级金币月排行
        updateClassGoldMonthRank(studentClassMap, size);

        // 更新学生班级勋章总数排行榜
        updateClassMedalMonthRank(studentClassMap, size);

        // 更新学生被膜拜总数排行榜
        updateClassWorshipMonthRank(studentClassMap, size);
    }


    @Override
    public void updateStudentExpansion() {
        log.info("定时任务 -> 学习力重置...");
        List<StudentExpansion> studentExpansions = simpleStudentExpansionMapper.selectAll();
        for (StudentExpansion studentExpansion : studentExpansions) {
            //查询基础获取学习力
            Integer study = simpleLevelMapper.getStudyById(studentExpansion.getLevel());
            //查询发起挑战的胜利场次获取的学习力
            List<Gauntlet> gauntlets = gauntletMapper.selectStudy(1, studentExpansion.getStudentId());
            for (Gauntlet gauntlet : gauntlets) {
                if (gauntlet.getChallengeStudy() != null) {
                    study = study + gauntlet.getChallengeStudy();
                }
            }
            //获取发起挑战失败
            List<Gauntlet> gauntlets1 = gauntletMapper.selectStudy(2, studentExpansion.getStudentId());
            for (Gauntlet gauntlet : gauntlets1) {
                if (gauntlet.getChallengeStudy() != null) {
                    study = Math.max(study - gauntlet.getChallengeStudy(), 0);
                }
            }
            //查询被发起挑战的胜利场次获取的学习力
            List<Gauntlet> gauntlets2 = gauntletMapper.selectStudy(3, studentExpansion.getStudentId());
            for (Gauntlet gauntlet : gauntlets2) {
                if (gauntlet.getBeChallengeStudy() != null) {
                    study = study + gauntlet.getBeChallengeStudy();
                }
            }
            //获取发起挑战失败
            List<Gauntlet> gauntlets3 = gauntletMapper.selectStudy(4, studentExpansion.getStudentId());
            for (Gauntlet gauntlet : gauntlets3) {
                if (gauntlet.getBeChallengeStudy() != null) {
                    if (study - gauntlet.getBeChallengeStudy() > 0) {
                        study = study - gauntlet.getBeChallengeStudy();
                    }
                } else {
                    study = 0;
                }
            }
            studentExpansion.setStudyPower(study);
            simpleStudentExpansionMapper.updateById(studentExpansion);
        }
        log.info("定时任务 -> 学习力重置完成...");
    }

    @Override
    @Scheduled(cron = "0 5 0 * * 1")
    public void deleteStudentLocation() {
        if (checkPort(port)) {
            return;
        }
        log.info("开始清除学生定位信息...");
        locationMapper.delete(null);
        log.info("清除学生定位信息完成...");
    }

    /**
     * 每天清楚体验账号到期60天的账号
     */
    @Override
    @Scheduled(cron = "0 15 2 * * ?")
    public void deleteExperienceAccount() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 删除回收站中到期60天的学生信息开始.");
        //获取删除日期之前的学生
        Date date = DateTime.now().minusDays(60).toDate();
        //回收站超过60天的学生数据
        List<Long> ids = recycleBinMapper.selectDeleteStudentIdByDate(date);
        if (ids.size() > 0) {
            //查询要删除的学生数据
            List<Student> students = studentMapper.selectDeleteAccount(ids);
            //获取所有的学生id
            List<Long> studentIds = new ArrayList<>();
            List<String> accountList = new ArrayList<>();
            students.forEach(stu -> {
                        studentIds.add(stu.getId());
                        accountList.add("账号：" + stu.getAccount() + " 姓名：" + stu.getStudentName() + " 学校：" + stu.getSchoolName());
                    }
            );
            if (studentIds.size() > 0) {
                simpleStudentUnitMapper.deleteByStudentIds(students);
                // 删除跟学生相关的挑战
                gauntletMapper.deleteByChallengerStudentIdsOrBeChallengerStudentIds(studentIds);
                // 删除学生勋章、奖励
                awardMapper.deleteByStudentIds(studentIds);
                weekHistoryPlanMapper.deleteByStudentIds(studentIds);
                totalHistoryPlanMapper.deleteByStudentIds(studentIds);
                learnNewMapper.deleteByStudentIds(studentIds);
                learnHistoryMapper.deleteByStudentIds(studentIds);
                studentEquipmentMapper.deleteByStudentIds(studentIds);
                recycleBinMapper.deleteByStudentIds(studentIds);
                this.resetRecord(studentIds);
                studentMapper.deleteByIds(studentIds);
                RunLog runLog = new RunLog();
                runLog.setType(3);
                runLog.setOperateUserId(1L);
                runLog.setCreateTime(new Date());
                runLog.setLogContent("时间:" + date + "删除回收站超过60天的学生:" + accountList.toString());
                runLogMapper.insert(runLog);
            }
        }
        log.info("定时任务 -> 删除回收站中到期60天的学生信息完成.");
    }


    @Override
    @Scheduled(cron = "0 15 1 * * ?")
    public void saveRecycleBin() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 将到期的体验账号放入回收站中开始.");
        //查询到期的体验账号
        List<Student> students = studentMapper.selectExperienceAccount();
        Date date = new Date();
        StringBuilder builder = new StringBuilder();
        if (students.size() > 0) {
            students.forEach(student -> {
                RecycleBin bin = new RecycleBin();
                bin.setCreateTime(date);
                bin.setDelStatus(1);
                bin.setOperateUserId(student.getTeacherId());
                bin.setOperateUserName("管理员");
                bin.setStudentId(student.getId());
                recycleBinMapper.insert(bin);
                // 清除学生排行缓存
                rankOpt.deleteGoldRank(student);
                rankOpt.deleteCcieRank(student);
                rankOpt.deleteMedalRank(student);
                rankOpt.deleteWorshipRank(student);
                builder.append("账号：").append(student.getAccount()).append(" 姓名：")
                        .append(student.getStudentName())
                        .append(" 学校：").append(student.getSchoolName()).append(",");
            });
            RunLog runLog = new RunLog();
            runLog.setType(3);
            runLog.setOperateUserId(1L);
            runLog.setCreateTime(new Date());
            runLog.setLogContent("时间:" + date + "将到期的体验账号放入回收站:" + builder.toString());
            runLogMapper.insert(runLog);
            studentMapper.updateStatus(students);

        }
        log.info("定时任务 -> 将到期的体验账号放入回收站中完成.");
    }

    @Override
    public void updateWelfareAccountToOutOfDate() {
        if (checkPort(port)) {
            return;
        }

        log.info("定时将招生账号置为过期状态开始...");
        List<Student> students = studentMapper.selectList(new QueryWrapper<Student>().eq("role", 4));

        if (CollectionUtils.isNotEmpty(students)) {
            String accountTime = DateUtil.DateTime();
            students.forEach(student -> {
                student.setAccount(accountTime);
                log.info("学生[{} - {} - {}]有效期置为过期状态！", student.getId(), student.getAccount(), student.getStudentName());
                studentMapper.updateById(student);
            });
        }
        log.info("定时将招生账号置为过期状态完成...");
    }

    /**
     * 获取单元学习进度，给学生增加错误优先级
     */
    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    public void calculateRateOfChange() {
        if (checkPort(port)) {
            return;
        }
        log.info("更新学生优先级开始。。。");
        //获取所有充课学生和所有未到期学生
        List<Long> studentIds = studentMapper.selectAllStudentId();
        //获取学生学习数量 可能修改
        List<Map<String, Object>> studyList = learnHistoryMapper.selectStudyFiveStudent(studentIds);
        //获取当前学生中学习了五个单元以上的学生id
        List<Map<String, Object>> collect = studyList.stream().filter(study ->
                study.get("count") != null && Integer.parseInt(study.get("count").toString()) > 5).collect(Collectors.toList());
        List<Long> studyStudentIds = new ArrayList<>();
        //获取可修改学生数据
        for (Map<String, Object> map : collect) {
            studyStudentIds.add(Long.parseLong(map.get("studentId").toString()));
        }
        studyStudentIds.forEach(studentId -> {
            //获取学生错误次数最大的单元
            //1获取已完成的单元id
            //错误率最大的单元
            Map<String, Object> isMap = new HashMap<>();
            List<Map<String, Object>> maps = learnHistoryMapper.selectStudyUnitByStudentId(studentId);
            if (maps.size() > 5) {
                //2,查询单元下数据的总数量
                List<Long> unitIds = new ArrayList<>();
                maps.forEach(map -> unitIds.add(Long.parseLong(map.get("unitId").toString())));
                Map<Long, Map<String, Object>> allUnitStudyCount =
                        unitFeignClient.selectCountByUnitIds(unitIds);
                //获取最大多错误率
                maps.forEach(map -> {
                    long studyUnit = Long.parseLong(map.get("unitId").toString());
                    long easyOrHard = Long.parseLong(map.get("easyOrHard").toString());
                    int errCount = errorLearnLogMapper.selectCountByStudentIdAndUnitIdAndEasyOrHard
                            (studentId, studyUnit, easyOrHard);
                    Map<String, Object> studyMap = allUnitStudyCount.get(studyUnit);
                    if (studyMap != null) {
                        if (studyMap.get("count") != null) {
                            int studyCount = Integer.parseInt(studyMap.get("count").toString());
                            double studyDouble = 1.0 * errCount / studyCount;
                            if (isMap.size() == 0) {
                                isMap.put("unitId", studyUnit);
                                isMap.put("easyOrHard", easyOrHard);
                                isMap.put("studyDouble", studyDouble);
                            } else {
                                double parseStudyDouble = Double.parseDouble(isMap.get("studyDouble").toString());
                                if (studyDouble > parseStudyDouble) {
                                    isMap.put("unitId", studyUnit);
                                    isMap.put("easyOrHard", easyOrHard);
                                    isMap.put("studyDouble", studyDouble);
                                }
                            }
                        }
                    }
                });
                //更新错误率删除errorLog数据
                if (isMap.size() > 0) {
                    double studyDouble = Double.parseDouble(isMap.get("studyDouble").toString());
                    long unitId = Long.parseLong(isMap.get("unitId").toString());
                    int easyOrHard = Integer.parseInt(isMap.get("easyOrHard").toString());
                    if (studyDouble > 0.0) {
                        //获取错误率最大的优先级
                        StudentStudyPlanNew studentStudyPlanNew =
                                studentStudyPlanNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
                        if (studentStudyPlanNew != null) {
                            //获取学生年级数值
                            Student student = studentMapper.selectById(studentId);
                            //获取课程年级数值
                            String strGrade = courseFeignClient.selectGradeByCourseId(studentStudyPlanNew.getCourseId());
                            int number = PriorityUtil.calculateRateOfChange(student.getGrade(), strGrade);
                            studentStudyPlanNew.setErrorLevel(studentStudyPlanNew.getErrorLevel() + number);
                            studentStudyPlanNew.setFinalLevel(studentStudyPlanNew.getFinalLevel() + number);
                            studentStudyPlanNewMapper.updateById(studentStudyPlanNew);
                            //获取删除的error信息
                            List<ErrorLearnLog> errorLearnLogs =
                                    errorLearnLogMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
                            if (errorLearnLogs.size() > 0) {
                                List<Long> deleteErrorIds = new ArrayList<>();
                                errorLearnLogs.forEach(logs -> deleteErrorIds.add(logs.getId()));
                                errorLearnLogMapper.deleteBatchIds(deleteErrorIds);
                                //将errorLearnLog表中删除的信息放入errorLearnLogHistory表中
                                errorLearnLogHistoryMapper.insertListByErrorLearnLogs(errorLearnLogs);
                            }
                        }
                    }
                }
                //获取学生learn_new表与student_flow_new不匹配的数据
                //获取learn表id
                List<Long> learnIds = learnNewMapper.selectIdByStudentId(studentId);
                List<Long> delIds = studentFlowNewMapper.selectDelIdByLearnIdsAndStudentId(learnIds, studentId);
                if (delIds.size() > 0) {
                    studentFlowNewMapper.deleteBatchIds(delIds);
                }

                //获取学生优先级最大的课程
                StudentStudyPlanNew plan = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);
                LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, plan.getUnitId(), plan.getEasyOrHard());
                //查看是否有当前learnNew数据
                if (learnNew == null) {
                    learnNew = new LearnNew();
                    learnNew.setCourseId(plan.getCourseId());
                    learnNew.setEasyOrHard(plan.getEasyOrHard());
                    learnNew.setGroup(1);
                    learnNew.setStudentId(studentId);
                    learnNew.setUnitId(plan.getUnitId());
                    learnNew.setUpdateTime(new Date());
                    learnNew.setModelType(1);
                    learnNewMapper.insert(learnNew);
                    saveStudentFlow(plan, learnNew);
                    //判断learnNEW中的数据是否大于10个
                } else {
                    learnNew.setUpdateTime(new Date());
                    learnNewMapper.updateById(learnNew);
                    //查看是否有flowNew数据
                    StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByLearnIdAndType(learnNew.getId(), 1);
                    if (studentFlowNew == null) {
                        saveStudentFlow(plan, learnNew);
                    }
                }
                delLearnNew(studentId);
            }
        });
        log.info("更新学生优先级完成。。。");
    }

    private void delLearnNew(Long studentId) {
        int number = learnNewMapper.selectCountByStudentId(studentId);
        if (number > 10) {
            number -= 10;
            //获取需要删除的数据
            List<LearnNew> learnNews = learnNewMapper.selectDelLearnIdByStudentIdAndNumber(studentId, number);
            List<Long> delLearnIds = new ArrayList<>();
            List<Long> delUnitIds = new ArrayList<>();
            learnNews.forEach(learn -> {
                delLearnIds.add(learn.getId());
                delUnitIds.add(learn.getUnitId());
            });
            if (delLearnIds.size() > 0) {
                studentFlowNewMapper.deleteByLearnIds(delLearnIds);
                learnNewMapper.deleteBatchIds(delLearnIds);
            }
            log.info("删除学生[{}],课程Id为[{}]", studentId, delUnitIds);
        }
    }

    private void saveStudentFlow(StudentStudyPlanNew plan, LearnNew learnNew) {
        StudentFlowNew studentFlowNew = new StudentFlowNew();
        studentFlowNew.setCurrentFlowId(plan.getFlowId());
        studentFlowNew.setLearnId(learnNew.getId());
        studentFlowNew.setUpdateTime(new Date());
        studentFlowNew.setStudentId(learnNew.getStudentId());
        studentFlowNew.setType(1);
        studentFlowNewMapper.insert(studentFlowNew);
    }

    /**
     * 每周给学生添加课程
     */
    @Override
    @Scheduled(cron = "0 30 3 * * ?")
    public void addStudyByWeek() {
        if (checkPort(port)) {
            return;
        }
        log.info("根据school_time配置更新学生课程开始。。。");
        //获取当前日期月的第几周
        int week = DateUtil.getWeekOfMonth(new Date());
        int month = DateUtil.getMonth();
        //获取当前月份当前周学校学习信息
        List<Map<String, Object>> maps = schoolTimeMapper.selectByMonthAndWeek(month, week);
        Map<Long, List<Map<String, Object>>> collect =
                maps.stream().collect(Collectors.groupingBy(map -> Long.parseLong(map.get("userId").toString())));
        //获取所有学生
        List<Map<String, Object>> allStudentIdTeacherId = studentMapper.getAllStudentIdTeacherId();
        allStudentIdTeacherId.forEach(student -> {
            Long adminId = Long.parseLong(student.get("adminId").toString());
            String grade = student.get("grade").toString();
            Long studentId = Long.parseLong(student.get("studentId").toString());
            //获取学生是否有个人的计划
            Integer count = schoolTimeMapper.selectCountByStudentId(studentId);
            //需要添加的计划
            Map<String, Object> studentMap = new HashMap<>();
            if (count > 0) {
                //获取学生添加的计划
                studentMap = schoolTimeMapper.selectByMonthAndWeekAndStudentId(month, week, studentId);

            } else {
                //如果没有获取学生可能添加的计划
                List<Map<String, Object>> studyModelList = collect.get(adminId);
                //获取学习单元
                if (studyModelList == null || studyModelList.size() == 0) {
                    studyModelList = collect.get(1L);
                }
                if (studyModelList != null) {
                    for (Map<String, Object> returnMap : studyModelList) {
                        String garde = returnMap.get("grade").toString();
                        String timeGrade = returnMap.get("timeGrade").toString();
                        if (garde.equals("高中")) {
                            if (timeGrade.equals(grade)) {
                                studentMap = returnMap;
                            }
                        } else {
                            if (garde.equals(grade)) {
                                studentMap = returnMap;
                            }
                        }

                    }
                }
            }
            if (studentMap != null && studentMap.size() > 0) {
                //判断当前学生是否拥有当前单元
                Long unitId = Long.parseLong(studentMap.get("unitId").toString());
                Long courseId = Long.parseLong(studentMap.get("courseId").toString());
                int i = studentStudyPlanNewMapper.selectByStudentIdAndUnitId(studentId, unitId);
                if (i <= 0) {
                    long userId;
                    int type = Integer.parseInt(studentMap.get("type").toString());
                    if (type == 1) {
                        userId = Long.parseLong(studentMap.get("userId").toString());
                    } else {
                        userId = studentId;
                    }
                    //判断当前单元是上册还是下册全册
                    String label = studentMap.get("label").toString();
                    //学习的单元数量
                    int number = 0;
                    if (label.contains("上册") || label.contains("全册")) {
                        //查询当前单元之前的本册书单元
                        number = unitFeignClient.selectByUnitIdAndCourseId(unitId, courseId);
                    } else if (label.contains("下册")) {
                        //获取上册单元数量
                        number = schoolTimeMapper.selectByGradeAndLabel(grade, "上册", type, userId);
                        number += unitFeignClient.selectByUnitIdAndCourseId(unitId, courseId);
                    }
                    //获取数量
                    int addNumber = PriorityUtil.calculateTimeOfChange(number);
                    StudentStudyPlanNew plan = getStudentStudyPlanNew(grade, studentId, unitId, courseId, addNumber, 70L, 1, 0);
                    studentStudyPlanNewMapper.insert(plan);
                    plan = getStudentStudyPlanNew(grade, studentId, unitId, courseId, addNumber, 79L, 2, 100);
                    studentStudyPlanNewMapper.insert(plan);
                    plan = getStudentStudyPlanNew(grade, studentId, unitId, courseId, addNumber, 145L, 3, 150);
                    studentStudyPlanNewMapper.insert(plan);

                }
            }
        });

        log.info("根据school_time配置更新学生课程完成。。。");
    }

    private StudentStudyPlanNew getStudentStudyPlanNew(String grade, Long studentId, Long unitId, Long courseId, int addNumber, long flowId, int type, int reduce) {
        StudentStudyPlanNew plan = new StudentStudyPlanNew();
        plan.setCourseId(courseId);
        plan.setStudentId(studentId);
        plan.setGroup(1);
        plan.setUnitId(unitId);
        plan.setUpdateTime(new Date());
        plan.setComplete(1);
        plan.setCurrentStudyCount(0);
        plan.setTotalStudyCount(0);
        plan.setEasyOrHard(type);
        plan.setBaseLevel(PriorityUtil.getEasyOrHard(grade, type) - reduce);
        plan.setErrorLevel(1);
        plan.setTimeLevel(addNumber);
        plan.setFinalLevel(plan.getBaseLevel() + plan.getTimeLevel() + plan.getErrorLevel());
        //70,79,143
        plan.setFlowId(flowId);
        return plan;
    }


    /**
     * 删除学生相关记录
     *
     * @param studentIdList
     */
    private void resetRecord(List<Long> studentIdList) {

        // 删除测试记录详情
        testRecordInfoMapper.deleteByStudentIds(studentIdList);

        // 删除测试记录
        testRecordMapper.deleteByStudentIds(studentIdList);

        // 删除学习记录
        learnMapper.deleteByStudentIds(studentIdList);

        // 删除青学版学习记录
        simpleCapacityMapper.deleteByStudentIds(studentIdList);

        // 删除智能版单词学习记录
        capacityListenMapper.deleteByStudentIds(studentIdList);
        capacityMemoryMapper.deleteByStudentIds(studentIdList);
        capacityPictureMapper.deleteByStudentIds(studentIdList);
        capacityWriteMapper.deleteByStudentIds(studentIdList);

        // 删除同步版句型慧追踪内容
        sentenceWriteMapper.deleteByStudentIds(studentIdList);
        sentenceListenMapper.deleteByStudentIds(studentIdList);
        sentenceTranslateMapper.deleteByStudentIds(studentIdList);

        //去除登入日志内容
        runLogMapper.deleteByStudentIds(studentIdList);
        goldLogMapper.deleteByStudentIds(studentIdList);

        // 清除学生兑奖记录
        studentExchangePrizeMapper.deleteByStudentIds(studentIdList);

        // 清除学生复习记录
        studentRestudyMapper.deleteByStudentIds(studentIdList);

        // 清除学生时长信息
        durationMapper.deleteByStudentIds(studentIdList);

        // 删除学生证书信息
        ccieMapper.deleteByStudentIds(studentIdList);

        // 清除学生留言反馈信息
        messageBoardMapper.deleteByStudentIds(studentIdList);

        // 清除学生抽奖记录
        drawRecordMapper.deleteByStudentIds(studentIdList);
        simpleSimpleStudentUnitMapper.deleteByStudentIds(studentIdList);

        // 删除学生游戏记录
        gameScoreMapper.deleteByStudentIds(studentIdList);

        // 删除字母学习相关记录
        letterListenMapper.delete(new QueryWrapper<LetterListen>().in("student_id", studentIdList));
        letterPairMapper.delete(new QueryWrapper<LetterPair>().in("student_id", studentIdList));
        letterWriteMapper.delete(new QueryWrapper<LetterWrite>().in("student_id", studentIdList));
        //清楚学习计划
        studentStudyPlanMapper.deleteByStudentIds(studentIdList);
        capacityStudentUnitMapper.deleteByStudentIds(studentIdList);
        // 删除开启单元的记录
        openUnitLogMapper.delete(new QueryWrapper<OpenUnitLog>().in("student_id", studentIdList));
    }


    /**
     * 更新学生被膜拜总数排行榜
     *
     * @param studentClassMap
     * @param size
     */
    private void updateClassWorshipMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级被膜拜次数排行信息开始。。。");
        Map<Long, Map<Long, Long>> studentWorshipCount = simpleWorshipMapper.countWorshipWithStudent();
        if (!sortStudentMedalRankAndWorshipRank(studentClassMap, size, studentWorshipCount, 3)) {
            log.error("定时增加学生班级被膜拜次数排行信息失败！");
            return;
        }

        log.info("定时增加学生班级被膜拜次数排行信息执行完成");
    }

    /**
     * 更新学生班级勋章总数排行榜
     *
     * @param studentClassMap
     * @param size            学生人数
     */
    private void updateClassMedalMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级勋章排行信息开始。。。");
        Map<Long, Map<Long, Long>> studentMedalCount = simpleAwardMapper.countMedalWithStudent();
        if (!sortStudentMedalRankAndWorshipRank(studentClassMap, size, studentMedalCount, 2)) {
            log.error("定时增加学生班级勋章排行信息失败！");
            return;
        }
        log.info("定时增加学生班级勋章排行信息执行完成");
    }

    private boolean sortStudentMedalRankAndWorshipRank(Map<Long, List<Student>> studentClassMap, int size, Map<Long, Map<Long, Long>> studentMedalCount, int type) {
        List<StudentRank> rankList = new ArrayList<>(size);

        final int[] rank = {1};
        final Date date = new Date();
        studentClassMap.forEach((classId, studentList) -> {

            // 存放当前班级学生 id 和勋章个数/被膜拜次数
            List<Map<String, Long>> studentMedalCountMap = new ArrayList<>();

            rank[0] = 1;
            studentList.forEach(student -> {
                if (student == null) {
                    return;
                }
                Map<String, Long> map = new HashMap<>(16);
                if (studentMedalCount.get(student.getId()) != null && studentMedalCount.get(student.getId()).get("count") != null) {
                    map.put("studentId", student.getId());
                    map.put("medalCount", studentMedalCount.get(student.getId()).get("count"));
                    studentMedalCountMap.add(map);
                }
            });

            studentMedalCountMap.sort(Comparator.comparing(studentMedal -> studentMedal.get("medalCount")));
            Collections.reverse(studentMedalCountMap);
            studentMedalCountMap.forEach(map -> {

                StudentRank studentRank = new StudentRank();
                studentRank.setStudentId(map.get("studentId"));
                studentRank.setCreateTime(date);
                studentRank.setType(type);
                studentRank.setMyRank(rank[0]++);
                rankList.add(studentRank);
            });
        });
        try {
            simpleStudentRankMapper.insertList(rankList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新学生班级金币月排行
     *
     * @param studentClassMap
     * @param size            学生人数
     */
    private void updateClassGoldMonthRank(Map<Long, List<Student>> studentClassMap, int size) {
        log.info("定时增加学生班级金币月排行信息开始。。。");
        List<StudentRank> rankList = new ArrayList<>(size);
        final int[] rank = {1};
        final Date date = new Date();
        studentClassMap.forEach((classId, studentList) -> {
            rank[0] = 1;
            if (studentList != null && studentList.size() > 0) {
                studentList = studentList.stream().filter(Objects::nonNull).collect(Collectors.toList());
                studentList.sort(Comparator.comparing(Student::getSystemGold).reversed());
                studentList.forEach(student -> {
                    StudentRank studentRank = new StudentRank();
                    studentRank.setStudentId(student.getId());
                    studentRank.setCreateTime(date);
                    studentRank.setType(1);
                    studentRank.setMyRank(rank[0]++);
                    rankList.add(studentRank);
                });
            }
        });

        try {
            simpleStudentRankMapper.insertList(rankList);
        } catch (Exception e) {
            log.error("定时增加学生班级金币月排行信息失败！", e);
            return;
        }

        log.info("定时增加学生班级金币月排行信息执行完成");
    }


    /**
     * 每天 00:30:00 更新学生全校日排行记录
     */
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 30 0 * * ?")
    @Override
    public void updateRank() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 更新学生全校日排行记录 开始执行...");

        // 全校日排行
        int rank = 0;
        // 全校周排行
        int weekRank = 0;
        // 全校月排行
        int monthRank = 0;
        // 全国周排行
        int countryRank = 0;
        // 全校日排行是否发生变化
        boolean schoolDayRankIsChange;
        // 全校/全国周排行是否发生变化
        boolean weekRankIsChange;
        // 全校月排行是否发生变化
        boolean monthRankIsChange;
        // 全国日排行是否发生变化
        boolean countryDayRankIsChange;
        Student student;
        // 校管 id
        Integer schoolAdminId;
        RankList updateRankList;
        RankList insertRankList;
        double currentGold;
        double preGold = 0.0;

        // 全校日排行学校和排名的对应关系
        Map<Integer, Integer> schoolRankMap = new HashMap<>(16);
        // 全校周排行学校和排名的对应关系
        Map<Integer, Integer> schoolWeekRankMap = new HashMap<>(16);
        // 全校月排行学校和排名的对应关系
        Map<Integer, Integer> schoolMonthRankMap = new HashMap<>(16);
        // 同班级上一个学生的金币数
        Map<Integer, Double> preStudentGold = new HashMap<>(16);

        // 判断今天是不是周一
        boolean isMonday = false;
        int firstDayOfMonth = 1;
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            isMonday = true;
        }

        // 判断今天是不是每月的1号
        boolean beginMonth = false;
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == firstDayOfMonth) {
            beginMonth = true;
        }

        // 查询需要进行排行的学生信息
        List<Student> students = simpleStudentMapper.selectStudentList();

        // 将students按照金币总数降序排列
        students.sort((s1, s2) -> (int) ((s1.getSystemGold() + s1.getOfflineGold()) - (s2.getSystemGold() + s2.getOfflineGold())));
        Collections.reverse(students);
        Map<Long, Integer> countryRankMap = new HashMap<>(16);
        int size = students.size();

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                countryRank++;
            } else if ((students.get(i).getSystemGold() + students.get(i).getOfflineGold()) != ((students.get(i - 1).getSystemGold() + students.get(i - 1).getOfflineGold()))) {
                countryRank++;
            }
            countryRankMap.put(students.get(i).getId(), countryRank);
        }

        // 查询所有学生的排行榜信息
        Map<Long, RankList> rankListMap = simpleRankListMapper.selectRankListMap();

        // 每个学生对应的校管 id
        Map<Long, Map<Long, Integer>> studentSchoolAdminMap = simpleStudentMapper.selectStudentSchoolAdminMap(students);

        List<RankList> insertList = new ArrayList<>();
        List<RankList> updateList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            student = students.get(i);
            schoolAdminId = studentSchoolAdminMap.get(student.getId()) == null ? null : studentSchoolAdminMap.get(student.getId()).get("schoolAdminId");

            currentGold = student.getSystemGold() + student.getOfflineGold();
            if (i > 0) {
                if (preStudentGold.containsKey(schoolAdminId)) {
                    preGold = preStudentGold.get(schoolAdminId);
                } else {
                    preGold = 0;
                }
            }
            preStudentGold.put(schoolAdminId, currentGold);

            // 学生排行已存在
            if (rankListMap.containsKey(student.getId())) {
                updateRankList = rankListMap.get(student.getId());
                if (i == 0) {
                    rank++;
                    if (isMonday) {
                        // 周一，更新全国和全校周排行
                        weekRank++;
                    }
                    if (beginMonth) {
                        // 每月1号，更新全校月排行
                        monthRank++;
                    }
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                } else if (schoolRankMap.containsKey(schoolAdminId)) {
                    // 是同一所学校
                    rank = schoolRankMap.get(schoolAdminId);
                    weekRank = schoolWeekRankMap.get(schoolAdminId);
                    monthRank = schoolMonthRankMap.get(schoolAdminId);
                    // 与上个同学总金币相同，名次相同,不同名次累加
                    if (currentGold == preGold) {
                        rank++;
                        if (isMonday) {
                            // 周一，更新全校周排行
                            weekRank++;
                        }
                        if (beginMonth) {
                            // 每月1号，更新全校月排行
                            monthRank++;
                        }
                    }
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                } else {
                    // 如果不是同一所学校排名从1开始
                    rank = 1;
                    if (isMonday) {
                        weekRank = 1;
                    }
                    if (beginMonth) {
                        monthRank = 1;
                    }
                    schoolRankMap.put(schoolAdminId, rank);
                    schoolWeekRankMap.put(schoolAdminId, weekRank);
                    schoolMonthRankMap.put(schoolAdminId, monthRank);
                }

                schoolDayRankIsChange = updateRankList.getSchoolDayRank() != rank;
                weekRankIsChange = isMonday && (!Objects.equals(updateRankList.getSchoolWeekRank(), schoolWeekRankMap.get(schoolAdminId))
                        || !Objects.equals(countryRankMap.get(student.getId()), updateRankList.getCountryWeekRank()));
                monthRankIsChange = beginMonth && !Objects.equals(schoolMonthRankMap.get(schoolAdminId), updateRankList.getSchoolMonthRank());
                countryDayRankIsChange = !Objects.equals(updateRankList.getCountryDayRank(), countryRankMap.get(student.getId()));

                if (schoolDayRankIsChange || weekRankIsChange || monthRankIsChange || countryDayRankIsChange) {
                    // 说明学生排行发生变化
                    updateRankList.setSchoolDayRank(rank);
                    updateRankList.setCountryDayRank(countryRankMap.get(student.getId()));
                    if (isMonday) {
                        updateRankList.setSchoolWeekRank(weekRank);
                        updateRankList.setCountryWeekRank(countryRank);
                    }
                    if (beginMonth) {
                        updateRankList.setSchoolMonthRank(monthRank);
                    }
                    // 如果学校日排行低于学生最低排行，更新学生最低排行信息
                    if (rank > updateRankList.getSchoolLowestRank()) {
                        updateRankList.setSchoolLowestRank(rank);
                    }
                    updateList.add(updateRankList);
                }
            } else {
                // 学生排行不存在需要新增数据
                if (i == 0 || schoolRankMap.containsKey(schoolAdminId)) {
                    rank = schoolRankMap.get(schoolAdminId) == null ? 0 : schoolRankMap.get(schoolAdminId);
                    weekRank = schoolWeekRankMap.get(schoolAdminId) == null ? 0 : schoolWeekRankMap.get(schoolAdminId);
                    monthRank = schoolMonthRankMap.get(schoolAdminId) == null ? 0 : schoolMonthRankMap.get(schoolAdminId);
                    if (new BigDecimal(currentGold).equals(new BigDecimal(preGold))) {
                        rank++;
                        weekRank++;
                        monthRank++;
                    }
                } else {
                    // 跟上个同学不是同一所学校
                    rank = 1;
                    weekRank = 1;
                    monthRank = 1;
                }
                schoolRankMap.put(schoolAdminId, rank);
                schoolWeekRankMap.put(schoolAdminId, weekRank);
                schoolMonthRankMap.put(schoolAdminId, monthRank);

                insertRankList = new RankList();
                insertRankList.setSchoolDayRank(rank);
                insertRankList.setStudentId(student.getId());
                insertRankList.setCountryWeekRank(countryRankMap.get(student.getId()));
                insertRankList.setSchoolWeekRank(weekRank);
                insertRankList.setSchoolMonthRank(monthRank);
                insertRankList.setSchoolLowestRank(rank);
                insertRankList.setCountryDayRank(countryRankMap.get(student.getId()));
                insertList.add(insertRankList);
            }
        }

        if (insertList.size() > 0) {
            try {
                simpleRankListMapper.insertList(insertList);
            } catch (Exception e) {
                log.error("新增学校日排行出错！", e);
            }

        }

        if (updateList.size() > 0) {
            try {
                simpleRankListMapper.updateList(updateList);
            } catch (Exception e) {
                log.error("更新学校日排行出错！", e);
            }
        }
        log.info("定时任务 -> 更新学生全校日排行记录 执行完成.");

    }

    /**
     * 计算时间差并转换为中文
     *
     * @param accountTime
     * @return
     */
    private String getDay(Date accountTime) {
        int value = (int) Math.ceil((accountTime.getTime() - System.currentTimeMillis()) * 1.0 / 86400000);
        switch (value) {
            case 0:
                return "【消息通知】 账号今天到期";
            case 1:
                return "【消息通知】 账号距离有效期还有一天";
            case 2:
                return "【消息通知】 账号距离有效期还有二天";
            case 3:
                return "【消息通知】 账号距离有效期还有三天";
            default:
        }
        return null;
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ? ")
    public void deleteSessionMap() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时清除 sessionMap 开始");
        Set<Object> sessionMapFields = redisTemplate.opsForHash().keys(RedisKeysConst.SESSION_MAP);
        Set<Object> loginSessionFields = redisTemplate.opsForHash().keys(RedisKeysConst.LOGIN_SESSION);
        if (!sessionMapFields.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisKeysConst.SESSION_MAP, sessionMapFields.toArray());
        }
        if (!loginSessionFields.isEmpty()) {
            redisTemplate.opsForHash().delete(RedisKeysConst.LOGIN_SESSION, loginSessionFields.toArray());
        }
        log.info("定时清除 sessionMap 完成");

        log.info("定时清理在线人数开始");
        Set<Object> members = redisTemplate.opsForZSet().range(RedisKeysConst.ZSET_ONLINE_USER, 0, -1);
        if (members != null) {
            members.forEach(o -> redisTemplate.opsForZSet().remove(RedisKeysConst.ZSET_ONLINE_USER, o));
        }
        log.info("定时清理在线人数完成");
    }


    @Override
    @Scheduled(cron = "0 10 0 * * ? ")
    public void deleteDrawRedis() {
        if (checkPort(port)) {
            return;
        }
        awardRedisOpt.delDrawRecord();
    }

}

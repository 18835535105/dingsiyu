package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.StudentInfoService;
import com.zhidejiaoyu.student.utils.CountMyGoldUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class StudentInfoServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements StudentInfoService {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private RunLog runLog;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;


    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private CountMyGoldUtil countMyGoldUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveStudentInfo(HttpSession session, Student student, String oldPassword,
                                                  String newPassword) {

        Student studentInfo = getStudent(session);
        packageStudentInfo(student, studentInfo);

        // 根据完善程度奖励金币
        double scale = completeInfo(studentInfo, oldPassword, newPassword);

        // 完善信息后保存奖励信息，获取奖励金币页提示语
        String tip = saveAwardInfo(studentInfo, scale);

        // 首次修改密码奖励
        firstUpdatePasswordAward(studentInfo, oldPassword, newPassword);

        try {
            int count = studentMapper.updateByPrimaryKeySelective(studentInfo);
            session.setAttribute(UserConstant.CURRENT_STUDENT, studentInfo);
            return count > 0 ? ServerResponse.createBySuccessMessage(tip)
                    : ServerResponse.createByErrorMessage("信息完善失败！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("id为{}的学生{}完善个人信息失败！", studentInfo.getId(), studentInfo.getStudentName());
            runLog = new RunLog(3, "id为" + studentInfo.getId() + "的学生" + studentInfo.getStudentName()
                    + "完善个人信息失败！",
                    new Date());
            runLogMapper.insert(runLog);
        }
        return ServerResponse.createByErrorMessage("信息完善失败！");
    }

    private void packageStudentInfo(Student student, Student studentInfo) {
        String headUrl = student.getHeadUrl();
        String headName = headUrl.substring(headUrl.lastIndexOf("."));
        studentInfo.setHeadUrl(headUrl);
        studentInfo.setHeadName(headName);
        studentInfo.setUpdateTime(new Date());
        studentInfo.setGrade(student.getGrade());
        studentInfo.setBirthDate(student.getBirthDate());
        studentInfo.setArea(student.getArea());
        studentInfo.setCity(student.getCity());
        studentInfo.setSex(student.getSex());
        studentInfo.setAddress(student.getAddress());
        studentInfo.setStudentName(student.getStudentName());
        studentInfo.setMail(student.getMail());
        studentInfo.setSquad(student.getSquad());
        studentInfo.setNickname(student.getNickname());
        studentInfo.setPartUrl(student.getPartUrl());
        studentInfo.setProvince(student.getProvince());
        studentInfo.setVersion(student.getVersion());
        studentInfo.setPatriarchPhone(student.getPatriarchPhone());
        studentInfo.setPetName(student.getPetName());
        studentInfo.setPracticalSchool(student.getPracticalSchool());
        studentInfo.setQq(student.getQq());
        studentInfo.setReferrer(student.getReferrer());
        studentInfo.setWish(student.getWish());
        if (studentInfo.getRegisterDate() == null) {
            studentInfo.setRegisterDate(new Date());
        }
    }

    /**
     * 首次修改密码计入任务奖励
     *
     * @param student
     * @param oldPassword
     * @param newPassword
     */
    private void firstUpdatePasswordAward(Student student, String oldPassword, String newPassword) {
        if (StringUtils.isNotBlank(newPassword) && !oldPassword.equals(newPassword)) {
            student.setPassword(newPassword);
            Award award = new Award();
            award.setType(2);
            award.setStudentId(student.getId());
            award.setAwardContentType(12);
            award.setGetFlag(2);
            award.setCreateTime(new Date());
            award.setGetTime(new Date());
            award.setCanGet(1);
            awardMapper.insert(award);

            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 10));
            RunLog runLog = new RunLog(student.getId(), 4, "学生首次修改密码，奖励#10#金币", new Date());
            runLogMapper.insert(runLog);
        }
    }

    /**
     * 学生完善信息保存奖励信息
     *
     * @param student
     * @param scale 信息完成度
     * @return  完善信息后提示语
     */
    private String saveAwardInfo(Student student, double scale) {
        String tip;
        if (scale == 1) {
            // 完善完必填信息和选填信息（算修改密码），奖励金币30个
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 30));
            tip = "恭喜获得30枚金币，已收入囊中。";
            log.info("id为 " + student.getId() + " 的学生在 " + DateUtil.DateTime(new Date()) + " 首次完善资料达 " + scale * 100 + "%，奖励金币#30#枚！");
            runLog = new RunLog(student.getId(), 4, "id为 " + student.getId() + " 的学生首次完善资料达 " + scale * 100 + "%，奖励金币#30#枚！", new Date());
            runLogMapper.insert(runLog);
            int awardContentType = 11;
            packageSaveInfoAward(student, awardContentType);
        } else {
            // 完善完必填信息，奖励金币20个
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 20));
            tip = "恭喜获得20枚金币，已收入囊中。";
            log.info("id为 " + student.getId() + " 的学生在 " + DateUtil.DateTime(new Date()) + " 首次完善资料达 " + scale * 100 + "%，奖励金币#20#枚！");
            runLog = new RunLog(student.getId(), 4, "id为 " + student.getId() + " 的学生首次完善资料达 " + scale * 100 + "%，奖励金币#20#枚！", new Date());
            runLogMapper.insert(runLog);
            int awardContentType = 10;
            packageSaveInfoAward(student, awardContentType);
        }
        return tip;
    }

    private void packageSaveInfoAward(Student student, int awardContentType) {
        Award award = new Award();
        award.setType(2);
        award.setStudentId(student.getId());
        award.setAwardContentType(awardContentType);
        award.setGetFlag(1);
        award.setCreateTime(new Date());
        award.setGetTime(new Date());
        award.setCanGet(1);
        awardMapper.insert(award);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServerResponse<String> worship(HttpSession session, Long userId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 查询用户上次的膜拜信息
        List<Worship> worships = worshipMapper.selectSevenDaysInfoByStudent(student);
        if (worships.size() > 0) {
            // 上次膜拜时间
            long lastWorshipTime = worships.get(0).getWorshipTime().getTime();
            long now = System.currentTimeMillis();
            if (now - lastWorshipTime < 86400000) {
                // 上次膜拜时间距现在不足24小时
                return ServerResponse.createByErrorCodeMessage(ResponseCode.TIME_LESS_ONE_DAY.getCode(), ResponseCode.TIME_LESS_ONE_DAY.getMsg());
            }
            long count = worships.stream().filter(worship -> worship.getStudentIdByWorship().equals(userId)).count();
            if (count > 0) {
                // 本周已膜拜过该同学，不能再次膜拜
                return ServerResponse.createByErrorCodeMessage(ResponseCode.TIME_LESS_ONE_WEEK.getCode(), ResponseCode.TIME_LESS_ONE_WEEK.getMsg());
            }
        }

        List<Medal> children = medalMapper.selectChildrenIdByParentId(92);
        int[] totalPlan = {1, 7, 14, 21, 30};
        int[] complete = new int[children.size()];

        // 可以膜拜该学生
        // 查询上一个被膜拜的最高次数
        Integer lastFirstCount = worshipMapper.countLastFirstCount();
        // 查询当前被膜拜的学生总共被膜拜的次数
        WorshipExample worshipExample = new WorshipExample();
        worshipExample.createCriteria().andStudentIdByWorshipEqualTo(userId);
        int count = worshipMapper.countByExample(worshipExample);

        if (lastFirstCount == null) {
            lastFirstCount = 0;
        }
        Student byWorship = studentMapper.selectByPrimaryKey(userId);
        if (lastFirstCount == 0) {
            byWorship.setWorshipFirstTime(new Date());
            studentMapper.updateByPrimaryKeySelective(byWorship);

            List<Student> list = new ArrayList<>();
            list.add(byWorship);
            award(list, children, 1, complete, totalPlan);
            complete = new int[children.size()];
        }

        List<Award> awards = awardMapper.selectMedalByStudentIdAndMedalType(byWorship, children);

        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount < children.size() && count + 1 > lastFirstCount) {
            // 当前被膜拜的学生成为全国唯一第一名
            // 计算上个第一名保持的时间
            StudentExample studentExample = new StudentExample();
            studentExample.createCriteria().andWorshipFirstTimeIsNotNull().andIdNotEqualTo(userId);
            List<Student> list = studentMapper.selectByExample(studentExample);

            if (list.size() > 0) {
                toAward(children, byWorship, list, complete, totalPlan);
            }

            // 将之前第一名的学生的勋章第一名标识删去
            list.forEach(student1 -> {
                student1.setWorshipFirstTime(null);
                studentMapper.updateByPrimaryKey(student1);
            });

            // 当前被膜拜的学生膜拜次数为全国最高,为其加上标识
            byWorship.setWorshipFirstTime(new Date());
            studentMapper.updateByPrimaryKeySelective(byWorship);
        } else if (canGetCount < children.size() && count + 1 == lastFirstCount) {
            // 当前被膜拜的学生成为全国并列第一名
            // 计算上个第一名保持的时间
            StudentExample studentExample = new StudentExample();
            studentExample.createCriteria().andWorshipFirstTimeIsNotNull();
            List<Student> list = studentMapper.selectByExample(studentExample);
            toAward(children, byWorship, list, complete, totalPlan);
        }

        // 保存膜拜信息
        Worship worship = new Worship();
        worship.setStudentIdByWorship(userId);
        worship.setStudentIdWorship(student.getId());
        worship.setWorshipTime(new Date());
        worshipMapper.insert(worship);

        return ServerResponse.createBySuccessMessage("膜拜成功");
    }

    @Override
    public ServerResponse<String> calculateValidTime(HttpSession session, Integer classify, Long courseId, Long unitId) {
        // key: 学习模块
        Map<Integer, Duration> map;
        Duration duration;

        Object beginTime = session.getAttribute(TimeConstant.BEGIN_VALID_TIME);
        if (beginTime == null) {
            return ServerResponse.createByErrorMessage("非法操作！");
        }

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Date loginTime = DateUtil.parseYYYYMMDDHHMMSS((Date) session.getAttribute(TimeConstant.LOGIN_TIME));
        Long startTime = ((Date) beginTime).getTime();
        Long nowTime = System.currentTimeMillis();
        Long second = (nowTime - startTime) / 1000;

        // session 中还没有学生的学习有效时长
        if (session.getAttribute(TimeConstant.TOTAL_VALID_TIME) == null) {
            map = new HashMap<>(16);
            duration = new Duration();
            duration.setStudentId(student.getId());
            duration.setCourseId(courseId);
            duration.setStudyModel(classify);
            duration.setUnitId(unitId);
            duration.setValidTime(second);
            duration.setLoginTime(loginTime);
            duration.setOnlineTime(0L);
            duration.setStudentId(student.getId());
            map.put(classify, duration);
            session.setAttribute(TimeConstant.TOTAL_VALID_TIME, map);
        } else {
            // session 中已经有学生的学习有效时长，如果没有当前模块增加当前模块的有效时长；如果有当前模块的有效时长，将有效时长相加
            map = (Map<Integer, Duration>) session.getAttribute(TimeConstant.TOTAL_VALID_TIME);
            if (map.containsKey(classify)) {
                duration = map.get(classify);
                duration.setValidTime(duration.getValidTime() + second);
                duration.setLoginTime(loginTime);
                duration.setOnlineTime(0L);
                map.put(classify, duration);
            } else {
                duration = new Duration();
                duration.setCourseId(courseId);
                duration.setStudyModel(classify);
                duration.setUnitId(unitId);
                duration.setValidTime(second);
                duration.setLoginTime(loginTime);
                duration.setOnlineTime(0L);
                duration.setStudentId(student.getId());
                map.put(classify, duration);
            }
            session.setAttribute(TimeConstant.TOTAL_VALID_TIME, map);
        }
        String tip = null;
        if (classify <= 6) {
            tip = saveGoldAward(session, classify, second, loginTime);
            countMyGoldUtil.countMyGold(student);
        }
        saveDuration(session, map, loginTime);
        return ServerResponse.createBySuccessMessage(tip);
    }

    @Override
    public ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword) {

        if (!Objects.equals(nowPassword, oldPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMsg());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> updateStudentInfo(HttpSession session, Student student) {
        Student currentStudent = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (!Objects.equals(currentStudent.getId(), student.getId())) {
            log.error("学生 {}->{} 试图修改 学生 {}->{} 的个人信息！", currentStudent.getId(), currentStudent.getStudentName(),
                    student.getId(), student.getStudentName());
            return ServerResponse.createByErrorMessage("服务器错误！请稍后重试");
        }
        studentMapper.updateByPrimaryKeySelective(student);
        student = studentMapper.selectByPrimaryKey(currentStudent.getId());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess();
    }

    /**
     * 计算学生本次学习获得的金币数
     *
     * @param session
     * @param classify
     * @param second    本次学习的有效时长，从进入学习页到退出学习页的时长
     * @param loginTime
     * @return
     */
    private String saveGoldAward(HttpSession session, Integer classify, Long second, Date loginTime) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student = studentMapper.selectByPrimaryKey(student.getId());
        String learnType = commonMethod.getTestType(classify);
        // 金币数
        int gold = 0;
        // 提示语
        StringBuilder tip = new StringBuilder("本次学习获得金币：");
        gold += saveNewLearnAward(classify, loginTime, learnType, student);
        gold += saveValidLearnAward(loginTime, learnType, student, second, classify);
        gold += saveKnownLearnAward(classify, loginTime, learnType, student);
        if (gold > 0) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            studentMapper.updateByPrimaryKeySelective(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
        tip.append(gold).append("个");
        return tip.toString();
    }

    /**
     * 本次学习熟词达到指定数量奖励金币
     *
     * @param classify
     * @param loginTime
     * @param learnType
     * @param student
     * @return
     */
    private int saveKnownLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int learnCount;
        int condition = classify == 1 ? 20 : 10;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        int awardCount = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "熟词");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1).andStatusEqualTo(1);
        learnCount = learnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType)
                    .append("模块本次新学熟词大于等于").append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 本次登录学习有效时长达到30分钟后奖励金币
     *
     * @param loginTime
     * @param learnType
     * @param student
     * @param second
     * @param classify
     * @return
     */
    private int saveValidLearnAward(Date loginTime, String learnType, Student student, Long second, Integer classify) {
        int condition = 30;
        long minute = second / 60;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        // 查询本次登录期间当前奖励次数
        int count = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "有效时长大于等于30分钟");
        if (count == 0 && minute >= condition) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).
                    append("模块学习过程中有效时长大于等于30分钟，获得#5#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);

            log.info(sb.toString());
            return 5;
        }
        return 0;
    }

    /**
     * 新学若干单词后奖励金币，本次登录期间只有学习指定模块单词的整数倍才奖励相应金币
     *
     * @param classify
     * @param loginTime
     * @param learnType 学习模块
     * @param student
     * @return 金币个数
     */
    private int saveNewLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int awardCount;
        int learnCount;
        int condition = classify == 1 ?  40 : 20;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        awardCount = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "新学");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1);
        learnCount = learnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).append("模块本次登录新学大于等于")
                    .append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 保存学生时长信息
     *
     * @param session
     * @param map
     * @param loginTime
     */
    private void saveDuration(HttpSession session, Map<Integer, Duration> map, Date loginTime) {
        map.forEach((key, value) -> {
            DurationExample example = new DurationExample();
            example.createCriteria().andStudentIdEqualTo(value.getStudentId()).andCourseIdEqualTo(value.getCourseId())
                    .andUnitIdEqualTo(value.getUnitId()).andLoginTimeEqualTo(loginTime).andStudyModelEqualTo(key);
            List<Duration> durations = durationMapper.selectByExample(example);
            // 如果时长表有本次登录的当前模块时长信息,更新；否则新增时长记录
            if (durations.size() > 0) {
                value.setId(durations.get(0).getId());
                durationMapper.updateByPrimaryKeySelective(value);
            } else {
                value.setLoginOutTime(DateUtil.parseYYYYMMDDHHMMSS(new Date()));
                try {
                    durationMapper.insertSelective(value);
                } catch (Exception e) {
                    log.error("保存时长信息出错，当前 key->value => {} -> {}", key, value, e);
                }
                map.put(key, value);
                session.setAttribute(TimeConstant.TOTAL_VALID_TIME, map);
            }
        });
    }

    private void toAward(List<Medal> children, Student byWorship, List<Student> list, int[] complete, int[] totalPlan) {
        if (list.size() > 0) {
            Date worshipFirstTime = list.get(0).getWorshipFirstTime();
            int day = this.getDay(worshipFirstTime);

            award(list, children, day, complete, totalPlan);

            // 当前被膜拜的学生膜拜次数为全国最高,为其加上标识
            byWorship.setWorshipFirstTime(new Date());
            studentMapper.updateByPrimaryKeySelective(byWorship);
        }
    }

    private void award(List<Student> list, List<Medal> children, int day, int[] complete, int[] totalPlan) {
        // 奖励之前第一名应得的勋章
        List<Award> awards;
        for (Student aList : list) {
            awards = awardMapper.selectMedalByStudentIdAndMedalType(aList, children);
            int count = (int) awards.stream().filter(award1 -> award1.getCanGet() == 1).count();
            // 只有问鼎天下子勋章没有都可领取时才进行勋章点亮操作，如果其子勋章已经全部都能够领取无操作
            if (count != children.size()) {
                for (int i = 0; i < children.size(); i++) {
                    if (day < totalPlan[i]) {
                        complete[i] = day;
                    } else {
                        complete[i] = totalPlan[i];
                    }
                }
                this.packageOrderMedal(aList, complete, children, awards, totalPlan);
            }
        }
    }

    private void packageOrderMedal(Student student, int[] complete, List<Medal> children, List<Award> awards, int[] totalPlan) {
        Award award;
        Medal medal;
        if (awards.size() == 0) {
            for (int i = 0; i < children.size(); i++) {
                award = new Award();
                medal = children.get(i);

                award.setType(3);
                award.setStudentId(student.getId());

                setAwardState(complete, totalPlan, award, i);
                award.setMedalType(medal.getId());
                // 保存award获取其id
                awardMapper.insert(award);
            }
        } else {
            // 不是第一次查看勋章只需更新勋章是否可领取状态即可
            for (int i = 0; i < children.size(); i++) {
                // 有需要更新的奖励数据
                award = awards.get(i);
                if (award.getCanGet() != 1 && complete[i] == totalPlan[i]) {
                    award.setCanGet(1);
                    awards.add(award);
                }
            }
            awards.forEach(item -> awardMapper.updateByPrimaryKeySelective(item));
        }
    }

    /**
     * 设置奖励可领取状态和领取状态
     *
     * @param complete
     * @param totalPlan
     * @param award
     * @param i
     */
    static void setAwardState(int[] complete, int[] totalPlan, Award award, int i) {
        if (complete[i] == totalPlan[i]) {
            // 当前任务完成
            award.setGetFlag(2);
            award.setCanGet(1);
        } else {
            award.setGetFlag(2);
            award.setCanGet(2);
        }
    }

    /**
     * 计算指定日期距今天的天数
     *
     * @param worshipFirstTime
     * @return
     */
    private int getDay(Date worshipFirstTime) {
        long date = worshipFirstTime.getTime();
        long now = System.currentTimeMillis();
        return (int) ((now - date) / 86400000);
    }

    /**
     * 验证完善信息程度，并奖励金币
     *
     * @param student
     * @param oldPassword
     * @param newPassword
     */
    private double completeInfo(Student student, String oldPassword, String newPassword) {
        int total = 18;
        int complete = 0;
        if (StringUtils.isNotBlank(student.getStudentName())) {
            complete++;
        }
        if (student.getSex() != null) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getBirthDate())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getGrade())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getVersion())) {
            complete++;
        }

        // 地址
        if (!StringUtils.isEmpty(student.getArea())) {
            complete++;
        }
        if (!StringUtils.isEmpty(student.getProvince())) {
            complete++;
        }
        if (!StringUtils.isEmpty(student.getCity())) {
            complete++;
        }
        // 宠物名
        if (!StringUtils.isEmpty(student.getPetName())) {
            complete++;
        }

        if (StringUtils.isNotBlank(oldPassword)) {
            complete += 2;
        }
        if (StringUtils.isNotBlank(newPassword)) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getHeadUrl())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getNickname())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getWish())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getPatriarchPhone())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getQq())) {
            complete++;
        }
        if (StringUtils.isNotBlank(student.getMail())) {
            complete++;
        }

        return complete * 1.0 / total;
    }


}

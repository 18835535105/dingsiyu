package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.Vo.simple.studentInfoVo.ChildMedalVo;
import com.zhidejiaoyu.common.Vo.simple.studentInfoVo.LevelVo;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleWeekUtil;
import com.zhidejiaoyu.common.utils.simple.server.SimpleResponseCode;
import com.zhidejiaoyu.student.constant.PetImageConstant;
import com.zhidejiaoyu.student.service.simple.SimpleStudentInfoServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class StudentInfoServiceImplSimple extends SimpleBaseServiceImpl<SimpleStudentMapper, Student> implements SimpleStudentInfoServiceSimple {

    private RunLog runLog;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;


    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private SimpleMedalMapper simpleMedalMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleLevelMapper simpleLevelMapper;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Override
    @GoldChangeAnnotation
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
            if (StringUtils.isEmpty(studentInfo.getPetName())) {
                studentInfo.setPetName("大明白");
                studentInfo.setPartUrl(PetImageConstant.DEFAULT_IMG);
            }
            int count = simpleStudentMapper.updateByPrimaryKeySelective(studentInfo);
            studentInfo = simpleStudentMapper.selectById(studentInfo.getId());
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
        if (student.getSex() == null) {
            studentInfo.setSex(1);
        } else {
            studentInfo.setSex(student.getSex());
        }
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

            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 10));
            RunLog runLog = new RunLog(student.getId(), 4, "学生首次修改密码，奖励#10#金币", new Date());
            runLogMapper.insert(runLog);

            // 首次修改密码奖励
            goldAwardAsync.dailyAward(student, 12);
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
            log.info("id为 " + student.getId() + " 的学生在 " + SimpleDateUtil.DateTime(new Date()) + " 首次完善资料达 " + scale * 100 + "%，奖励金币#30#枚！");
            runLog = new RunLog(student.getId(), 4, "id为 " + student.getId() + " 的学生首次完善资料达 " + scale * 100 + "%，奖励金币#30#枚！", new Date());
            runLogMapper.insert(runLog);
            int awardContentType = 11;
            goldAwardAsync.dailyAward(student, awardContentType);
        } else {
            // 完善完必填信息，奖励金币20个
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 20));
            tip = "恭喜获得20枚金币，已收入囊中。";
            log.info("id为 " + student.getId() + " 的学生在 " + SimpleDateUtil.DateTime(new Date()) + " 首次完善资料达 " + scale * 100 + "%，奖励金币#20#枚！");
            runLog = new RunLog(student.getId(), 4, "id为 " + student.getId() + " 的学生首次完善资料达 " + scale * 100 + "%，奖励金币#20#枚！", new Date());
            runLogMapper.insert(runLog);
            int awardContentType = 10;
            goldAwardAsync.dailyAward(student, awardContentType);
        }
        return tip;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServerResponse<String> worship(HttpSession session, Long userId) {
        Student student = getStudent(session);

        // 查询用户上次的膜拜信息
        List<Worship> worships = worshipMapper.selectSevenDaysInfoByStudent(student);
        if (worships.size() > 0) {
            // 上次膜拜时间
            Date lastWorshipTime = worships.get(0).getWorshipTime();
            if (Objects.equals(SimpleDateUtil.formatYYYYMMDD(lastWorshipTime), SimpleDateUtil.formatYYYYMMDD(new Date()))) {
                // 今天已经膜拜过其他人
                return ServerResponse.createByErrorCodeMessage(SimpleResponseCode.TIME_LESS_ONE_DAY.getCode(), SimpleResponseCode.TIME_LESS_ONE_DAY.getMsg());
            }
            long count = worships.stream().filter(worship -> worship.getStudentIdByWorship().equals(userId)).count();
            if (count > 0) {
                // 本周已膜拜过该同学，不能再次膜拜
                return ServerResponse.createByErrorCodeMessage(SimpleResponseCode.TIME_LESS_ONE_WEEK.getCode(), SimpleResponseCode.TIME_LESS_ONE_WEEK.getMsg());
            }
        }

        List<Medal> children = simpleMedalMapper.selectChildrenIdByParentId(92);
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
        Student byWorship = simpleStudentMapper.selectByPrimaryKey(userId);
        if (lastFirstCount == 0) {
            byWorship.setWorshipFirstTime(new Date());
            simpleStudentMapper.updateByPrimaryKeySelective(byWorship);

            List<Student> list = new ArrayList<>();
            list.add(byWorship);
            award(list, children, 1, complete, totalPlan);
            complete = new int[children.size()];
        }

        List<Award> awards = simpleAwardMapper.selectMedalByStudentIdAndMedalType(byWorship, children);

        int canGetCount = (int) awards.stream().filter(award -> award.getCanGet() == 1).count();
        if (canGetCount < children.size() && count + 1 > lastFirstCount) {
            // 当前被膜拜的学生成为全国唯一第一名
            // 计算上个第一名保持的时间
            StudentExample studentExample = new StudentExample();
            studentExample.createCriteria().andWorshipFirstTimeIsNotNull().andIdNotEqualTo(userId);
            List<Student> list = simpleStudentMapper.selectByExample(studentExample);

            if (list.size() > 0) {
                toAward(children, byWorship, list, complete, totalPlan);
            }

            // 将之前第一名的学生的勋章第一名标识删去
            list.forEach(student1 -> {
                student1.setWorshipFirstTime(null);
                simpleStudentMapper.updateByPrimaryKey(student1);
            });

            // 当前被膜拜的学生膜拜次数为全国最高,为其加上标识
            byWorship.setWorshipFirstTime(new Date());
            simpleStudentMapper.updateByPrimaryKeySelective(byWorship);
        } else if (canGetCount < children.size() && count + 1 == lastFirstCount) {
            // 当前被膜拜的学生成为全国并列第一名
            // 计算上个第一名保持的时间
            StudentExample studentExample = new StudentExample();
            studentExample.createCriteria().andWorshipFirstTimeIsNotNull();
            List<Student> list = simpleStudentMapper.selectByExample(studentExample);
            toAward(children, byWorship, list, complete, totalPlan);
        }

        // 保存膜拜信息
        Worship worship = new Worship();
        worship.setStudentIdByWorship(userId);
        worship.setStudentIdWorship(student.getId());
        worship.setWorshipTime(new Date());
        worship.setState(2);
        worshipMapper.insert(worship);

        // 众望所归勋章
        medalAwardAsync.enjoyPopularConfidence(student);

        return ServerResponse.createBySuccessMessage("膜拜成功");
    }

    @Override
    public ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword) {

        if (!Objects.equals(nowPassword, oldPassword)) {
            return ServerResponse.createByErrorCodeMessage(SimpleResponseCode.PASSWORD_ERROR.getCode(), SimpleResponseCode.PASSWORD_ERROR.getMsg());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> updateStudentInfo(HttpSession session, Student student) {
        Student currentStudent = getStudent(session);
        if (!Objects.equals(currentStudent.getId(), student.getId())) {
            log.error("学生 {}->{} 试图修改 学生 {}->{} 的个人信息！", currentStudent.getId(), currentStudent.getStudentName(),
                    student.getId(), student.getStudentName());
            return ServerResponse.createByErrorMessage("服务器错误！请稍后重试");
        }
        student.setPartUrl(student.getPartUrl() == null ? student.getPartUrl() : student.getPartUrl().replace(AliyunInfoConst.host, ""));
        student.setHeadUrl(student.getHeadUrl() == null ? student.getHeadUrl() : student.getHeadUrl().replace(AliyunInfoConst.host, ""));
        simpleStudentMapper.updateByPrimaryKeySelective(student);
        student = simpleStudentMapper.selectById(currentStudent.getId());
        student.setDiamond(currentStudent.getDiamond());
        student.setEnergy(currentStudent.getEnergy());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Map<String, Object>> getWorship(HttpSession session, Integer type, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>(16);
        // 本周我被膜拜的次数
        Date date = new Date();
        Date firstDayOfWeek = SimpleWeekUtil.getFirstDayOfWeek(date);
        Date lastDayOfWeek = SimpleWeekUtil.getLastDayOfWeek(date);
        int count = worshipMapper.countByWorshipedThisWeed(student, SimpleDateUtil.formatYYYYMMDD(firstDayOfWeek), SimpleDateUtil.formatYYYYMMDD(lastDayOfWeek));
        map.put("count", count);

        // 膜拜记录
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, String>> mapList = worshipMapper.selectStudentNameAndTime(student, type);
        map.put("list", new PageInfo<>(mapList));

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, Long stuId, Long medalId) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = simpleStudentMapper.selectByPrimaryKey(stuId);
        }
        ChildMedalVo childMedalInfo = getChildMedalInfo(student, medalId);
        return ServerResponse.createBySuccess(childMedalInfo);
    }

    @Override
    public ServerResponse<Map<String, Object>> getAllMedal(HttpSession session, Long stuId, Integer pageNum, Integer pageSize) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = simpleStudentMapper.selectByPrimaryKey(stuId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> medalImgUrlList = simpleMedalMapper.selectMedalImgUrl(student);
        List<Map<String, Object>> medalImgUrlListTemp = getAllMedalImgUrl(student, medalImgUrlList);

        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(medalImgUrlList);
        PageInfo<Map<String, Object>> mapPageInfo1 = new PageInfo<>(medalImgUrlListTemp);
        mapPageInfo1.setTotal(mapPageInfo.getTotal());
        mapPageInfo1.setPages(mapPageInfo.getPages());

        Map<String, Object> map = new HashMap<>(16);
        map.put("petName", AliyunInfoConst.host + student.getPetName());
        map.put("list", mapPageInfo1);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse optBackMusic(HttpSession session, Integer status) {
        Long studentId = super.getStudentId(session);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
        if (studentExpansion == null){
            studentExpansion = new StudentExpansion();
            studentExpansion.setAudioStatus(status);
            simpleStudentExpansionMapper.insert(studentExpansion);
        } else {
            studentExpansion.setAudioStatus(status);
            simpleStudentExpansionMapper.updateById(studentExpansion);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Map<String, Integer>> getBackMusicStatus(HttpSession session) {
        Long studentId = super.getStudentId(session);
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
        Map<String, Integer> map = new HashMap<>(16);
        if (studentExpansion == null || studentExpansion.getAudioStatus() == null) {
            map.put("state", 1);
        } else {
            map.put("state", studentExpansion.getAudioStatus());
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deleteRepeatLogoutLogs() {
        List<RunLog> runLogs = runLogMapper.selectList(new EntityWrapper<RunLog>().eq("type", 1).orderBy("operate_user_id", true).orderBy("create_time", true));

        List<Long> logIds = new ArrayList<>();
        int size = runLogs.size();
        RunLog runLog;
        for (int i = 0; i < size; i++) {
            runLog = runLogs.get(i);
            if (i > 0 && runLogs.get(i - 1).getLogContent().contains("退出登录") && runLog.getLogContent().contains("退出登录")) {
                logIds.add(runLog.getId());
            }
        }
        runLogMapper.deleteBatchIds(logIds);
        return ServerResponse.createBySuccess();
    }

    private List<Map<String, Object>> getAllMedalImgUrl(Student student, List<Map<String, Object>> medalImgUrlList) {
        List<Map<String, Object>> medalImgUrlListTemp = new ArrayList<>(medalImgUrlList.size());
        int sex = student.getSex() == null ? 1 : student.getSex();
        medalImgUrlList.forEach(map -> {
            Map<String, Object> mapTemp = new HashMap<>(16);
            if (map.get("imgUrl").toString().contains("#")) {
                mapTemp.put("imgUrl", sex == 1 ? AliyunInfoConst.host +  map.get("imgUrl").toString().split("#")[0] : AliyunInfoConst.host +  map.get("imgUrl").toString().split("#")[1]);
            } else {
                mapTemp.put("imgUrl", AliyunInfoConst.host + map.get("imgUrl"));
            }
            mapTemp.put("id", map.get("id"));
            medalImgUrlListTemp.add(mapTemp);
        });

        return medalImgUrlListTemp;
    }

    private ChildMedalVo getChildMedalInfo(Student student, long medalId) {
        List<Map<String, String>> childInfo = simpleMedalMapper.selectChildrenInfo(student, medalId);

        List<String> medalImgUrl = new ArrayList<>(childInfo.size());
        StringBuilder sb = new StringBuilder();
        childInfo.forEach(info -> {
            medalImgUrl.add(info.get("imgUrl"));
            sb.append(info.get("content"));
        });

        ChildMedalVo childMedalVo = new ChildMedalVo();
        childMedalVo.setMedalImgUrl(medalImgUrl);
        childMedalVo.setContent(sb.toString());
        return childMedalVo;
    }

    private Map<String, String> getLevelInfo(LevelVo levelVo, Student student, Map<String, String> parentMap) {
        List<Level> levels = simpleLevelMapper.selectList(new EntityWrapper<Level>().orderBy("id", true));
        double gold = BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        // 获取当前勋章子勋章索引
        Map<String, String> childMap = null;
        int size = levels.size();
        int preSize = size - 1;
        Level level;
        Level nextLevel;
        for (int i = 0; i < size; i++) {
            level = levels.get(i);
            if (!parentMap.containsKey(level.getLevelName())) {
                childMap = new HashMap<>(16);
                parentMap.put(level.getLevelName(), level.getLevelName());
            }
            if (childMap != null) {
                childMap.put(level.getChildName(), level.getChildName());
            }
            if (i < preSize) {
                nextLevel = levels.get(i + 1);
                // 判断当前金币所处的等级
                boolean flag = ((i == 0 && gold < level.getGold()) || gold >= level.getGold()) && gold < nextLevel.getGold();
                if (flag) {
                    levelVo.setLevelImgUrl(level.getImgUrlLevel());
                    levelVo.setChildName(level.getImgUrlWord());
                    break;
                }
            } else {
                levelVo.setLevelImgUrl(level.getImgUrlLevel());
                levelVo.setChildName(level.getImgUrlWord());
                break;
            }
        }
        return childMap;
    }

    private void toAward(List<Medal> children, Student byWorship, List<Student> list, int[] complete, int[] totalPlan) {
        if (list.size() > 0) {
            Date worshipFirstTime = list.get(0).getWorshipFirstTime();
            int day = this.getDay(worshipFirstTime);

            award(list, children, day, complete, totalPlan);

            // 当前被膜拜的学生膜拜次数为全国最高,为其加上标识
            byWorship.setWorshipFirstTime(new Date());
            simpleStudentMapper.updateByPrimaryKeySelective(byWorship);
        }
    }

    private void award(List<Student> list, List<Medal> children, int day, int[] complete, int[] totalPlan) {
        // 奖励之前第一名应得的勋章
        List<Award> awards;
        for (Student aList : list) {
            awards = simpleAwardMapper.selectMedalByStudentIdAndMedalType(aList, children);
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
                simpleAwardMapper.insert(award);
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
            awards.forEach(item -> simpleAwardMapper.updateByPrimaryKeySelective(item));
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
            award.setCurrentPlan(totalPlan[i]);
        } else {
            award.setGetFlag(2);
            award.setCanGet(2);
            award.setCurrentPlan(complete[i]);
        }
        award.setTotalPlan(totalPlan[i]);
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

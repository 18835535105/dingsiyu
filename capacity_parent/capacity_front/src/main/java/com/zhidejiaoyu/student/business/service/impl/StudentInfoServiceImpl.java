package com.zhidejiaoyu.student.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.dto.EndValidTimeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.DurationUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.WeekUtil;
import com.zhidejiaoyu.common.utils.goldUtil.GoldUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.level.ChildMedalVo;
import com.zhidejiaoyu.common.vo.student.level.LevelVo;
import com.zhidejiaoyu.student.business.service.StudentInfoService;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.WorshipRedisOpt;
import com.zhidejiaoyu.student.common.validTime.GetValidTimeTip;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class StudentInfoServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements StudentInfoService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private RankOpt rankOpt;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private GetValidTimeTip getValidTimeTip;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Resource
    private WorshipRedisOpt worshipRedisOpt;

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveStudentInfo(HttpSession session, Student student, String oldPassword,
                                                  String newPassword) {

        Student studentInfo = super.getStudent(session);
        this.packageStudentInfo(student, studentInfo);

        // 根据完善程度奖励金币
        double scale = this.completeInfo(studentInfo, oldPassword, newPassword);

        // 完善信息后保存奖励信息，获取奖励金币页提示语
        String tip = this.saveAwardInfo(studentInfo, scale);

        // 首次修改密码奖励
        this.firstUpdatePasswordAward(studentInfo, oldPassword, newPassword);

        GoldUtil.addStudentGold(studentInfo, studentInfo.getSystemGold());
        session.setAttribute(UserConstant.CURRENT_STUDENT, studentInfo);
        return ServerResponse.createBySuccessMessage(tip);
    }

    /**
     * 封装学生完善的信息
     *
     * @param student     学生填写信息
     * @param studentInfo 原学生信息
     */
    private void packageStudentInfo(Student student, Student studentInfo) {
        String headUrl = student.getHeadUrl();
        String headName = headUrl.substring(headUrl.lastIndexOf("."));
        studentInfo.setHeadUrl(headUrl);
        studentInfo.setHeadName(headName);
        studentInfo.setUpdateTime(new Date());

        // 招生账号不修改年级信息
        if (Objects.equals(4, studentInfo.getRole()) || StringUtils.isNotEmpty(studentInfo.getGrade())) {
            studentInfo.setGrade(studentInfo.getGrade());
        } else {
            studentInfo.setGrade(student.getGrade());
        }

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
        studentInfo.setNickname(StringUtils.isEmpty(student.getNickname()) ? "默认昵称" : student.getNickname());
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

            int gold = 10;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            GoldLogUtil.saveStudyGoldLog(student.getId(), "首次修改密码", gold);
        }
    }

    /**
     * 学生完善信息保存奖励信息
     *
     * @param student
     * @param scale   信息完成度
     * @return 完善信息后提示语
     */
    private String saveAwardInfo(Student student, double scale) {
        String tip;
        int gold;
        if (scale == 1) {
            // 完善完必填信息和选填信息（算修改密码），奖励金币30个
            gold = 30;
            int awardContentType = 11;
            goldAwardAsync.dailyAward(student, awardContentType);
        } else {
            // 完善完必填信息，奖励金币20个
            gold = 20;
            int awardContentType = 10;
            goldAwardAsync.dailyAward(student, awardContentType);
        }
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
        tip = "恭喜获得" + gold + "枚金币，已收入囊中。";
        log.info("id为 " + student.getId() + " 的学生在 " + DateUtil.DateTime(new Date()) + " 首次完善信息达 " + scale * 100 + "%，奖励金币#" + gold + "#枚！");

        GoldLogUtil.saveStudyGoldLog(student.getId(), "首次完善信息", gold);
        return tip;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServerResponse<String> worship(HttpSession session, Long userId) {
        Student student = getStudent(session);

        Map<String, Long> todayByWorshipedStudentIds = worshipRedisOpt.getTodayByWorshipedStudentIds(student.getId());
        if (todayByWorshipedStudentIds.containsKey(String.valueOf(userId))) {
            // 今天已经膜拜过该同学
            return ServerResponse.createByErrorCodeMessage(ResponseCode.TIME_LESS_ONE_DAY.getCode(), ResponseCode.TIME_LESS_ONE_DAY.getMsg());
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
        Student byWorship = studentMapper.selectById(userId);
        if (lastFirstCount == 0) {
            byWorship.setWorshipFirstTime(new Date());
            studentMapper.updateById(byWorship);

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
            List<Student> list = studentMapper.selectList(new QueryWrapper<Student>()
                    .isNotNull("worship_first_time").ne("id", userId));

            if (list.size() > 0) {
                toAward(children, byWorship, list, complete, totalPlan);
            }

            // 将之前第一名的学生的勋章第一名标识删去
            list.forEach(student1 -> studentMapper.updateWorshipFirstTimeToNull(student1.getId()));

            // 当前被膜拜的学生膜拜次数为全国最高,为其加上标识
            byWorship.setWorshipFirstTime(new Date());
            studentMapper.updateById(byWorship);
        } else if (canGetCount < children.size() && count + 1 == lastFirstCount) {
            // 当前被膜拜的学生成为全国并列第一名
            // 计算上个第一名保持的时间
            StudentExample studentExample = new StudentExample();
            studentExample.createCriteria().andWorshipFirstTimeIsNotNull();
            List<Student> list = studentMapper.selectList(new QueryWrapper<Student>().isNotNull("worship_first_time"));
            toAward(children, byWorship, list, complete, totalPlan);
        }

        // 保存膜拜信息
        Worship worship = new Worship();
        worship.setStudentIdByWorship(userId);
        worship.setStudentIdWorship(student.getId());
        worship.setWorshipTime(new Date());
        worshipMapper.insert(worship);
        worshipRedisOpt.saveTodayWorshipedStudentId(student.getId(), userId);

        executorService.execute(() -> {
            // 众望所归勋章
            medalAwardAsync.enjoyPopularConfidence(student);
            rankOpt.optWorshipRank(byWorship);
        });

        return ServerResponse.createBySuccessMessage("膜拜成功");
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> calculateValidTime(HttpSession session, EndValidTimeDto dto) {

        Student student = super.getStudent(session);

        // 学生超过30分钟无操作后，session过期会导致student为空，点击退出按钮会出现NPE，在此进行处理
        if (student == null) {
            return ServerResponse.createBySuccess();
        }

        // 当前学习的有效时长是否已经保存
        int count = durationMapper.countByLoginOutTime(student.getId(), DateUtil.formatYYYYMMDDHHMMSS(new Date()));
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("本次学习获得金币：0 个");
        }

        long onlineTimeBetweenThisAndLast = DurationUtil.getOnlineTimeBetweenThisAndLast(student, (Date) session.getAttribute(TimeConstant.LOGIN_TIME));
        dto.setOnlineTime(onlineTimeBetweenThisAndLast);

        // 判断有效时长是否大于上个模块退出至当前模块退出时间差, 如果大于，置为最大时间差；否则正常保存
        long validTime = checkTimeDifference(student, dto);
        if (validTime == 0) {
            // 如果 validTime == 0，说明该条记录与上次保存的记录是重复的，不再保存
            return ServerResponse.createBySuccessMessage("本次学习获得金币：0 个");
        }

        Date loginTime = DateUtil.parseYYYYMMDDHHMMSS((Date) session.getAttribute(TimeConstant.LOGIN_TIME));

        Duration duration = packageDuration(dto, student, loginTime, session);
        try {
            durationMapper.insert(duration);
        } catch (Exception e) {
            log.error("保存时长信息出错", e);
        }

        String tip = null;
        Integer classify = dto.getClassify();
        if (classify != null) {
            if (classify <= 6) {
                tip = getValidTimeTip.saveGoldAward(session, student, classify, validTime, loginTime);
            } else if (classify >= 14 && classify <= 22) {
                int type = classify - 13;
                tip = getValidTimeTip.saveSimpleGoldAward(session, student, type, validTime, loginTime);
            }
        } else {
            tip = "本次学习获得金币：0 个。";
            log.error("保存学生[{} -{} - {}]有效时长classify=[null], 请求参数=[{}]", student.getId(), student.getAccount(), student.getStudentName(), dto.toString());
        }
        session.removeAttribute(TimeConstant.BEGIN_VALID_TIME);

        executorService.execute(() -> this.saveAward(session, classify, student));

        return ServerResponse.createBySuccessMessage(tip);
    }

    /**
     * 校验有效时长是否大于上个模块保存后至当前时间的时间差，如果大于将有效时长置为时间差，否则不操作
     *
     * @param student
     * @return
     */
    private Long checkTimeDifference(Student student, EndValidTimeDto dto) {
        try {
            // 最大可保存时间
            long maxTime = dto.getOnlineTime();
            if (maxTime < dto.getValid()) {
                log.warn("学生 [{} -{} - {}] 保存有效时长过大！classify=[{}], courseId=[{}], unitId=[{}], validTime=[{}s], 实际最大可保存为[{}s], num=[{}]",
                        student.getId(), student.getAccount(), student.getStudentName(), dto.getClassify(), dto.getCourseId(), dto.getUnitId(), dto.getValid(), maxTime, dto.getNum());
                dto.setValid(maxTime);
                return maxTime;
            }
        } catch (Exception e) {
            log.warn("获取有效时长出错！不影响正常使用！", e);
        }
        return dto.getValid();
    }

    private void saveAward(HttpSession session, Integer classify, Student student) {
        // 辉煌荣耀勋章：今天学习效率>目标学习效率 currentPlan ++；否则不操作，每天第一次登录的时候查看昨天是否有更新辉煌荣耀勋章，如果没更新，将其 currentPlan 置为0
        medalAwardAsync.honour(student, (int) DurationUtil.getTodayValidTime(session), (int) DurationUtil.getTodayOnlineTime(session));

        // 学习总有效时长金币奖励
        goldAwardAsync.totalValidTime(student);

        // 熟词句相关勋章
        medalAwardAsync.tryHand(student, this.getModel(classify));

        // 记录每周活动学习时长
        weekActivityRankOpt.updateWeekActivitySchoolRank(student);
    }

    private int getModel(Integer classify) {
        if (classify == null) {
            return 0;
        }
        if (classify < 7) {
            return classify;
        }
        // 单词记忆模块
        if (classify == 16 || classify == 19 || classify == 20 || classify == 17 || classify == 18) {
            return 1;
        }
        // 默写模块
        if (classify == 21 || classify == 22) {
            return 3;
        }
        return 0;
    }

    private Duration packageDuration(EndValidTimeDto dto, Student student, Date loginTime, HttpSession session) {

        // 区分当前学习是自由学习还是一键学习，如果为空说明是自由学习
        Object attribute = session.getAttribute(SessionConstant.STUDY_FLAG);
        int studyFlag = attribute == null ? 2 : Integer.parseInt(attribute.toString());

        Duration duration = new Duration();
        duration.setCourseId(dto.getCourseId());
        Integer classify = dto.getClassify();
        duration.setStudyModel(classify);
        duration.setUnitId(dto.getUnitId());
        duration.setValidTime(dto.getValid());
        duration.setLoginTime(loginTime);
        duration.setStudentId(student.getId());
        duration.setOnlineTime(dto.getOnlineTime());
        duration.setLoginOutTime(new Date());
        duration.setLearningModel(studyFlag);
        duration.setGroup((Integer) session.getAttribute(SessionConstant.STUDY_GROUP));

        session.removeAttribute(SessionConstant.STUDY_GROUP);

        if (classify != null) {
            // 判断是不是单词流程相关的模块
            boolean flag = (classify >= 0 && classify <= 3) || classify == 27 || classify == 35 || classify == 36;
            if (flag) {
                // 单词学习计划
                CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(student.getId(), 1);
                if (capacityStudentUnit != null) {
                    StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selectCurrentPlan(student.getId(),
                            capacityStudentUnit.getStartunit(), capacityStudentUnit.getEndunit(), 1);
                    if (studentStudyPlan != null) {
                        duration.setStudyCount(studentStudyPlan.getCurrentStudyCount());
                        duration.setStudyPlanId(studentStudyPlan.getId());
                    }
                }
            }
        }

        return duration;
    }

    @Override
    public ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword) {

        if (!Objects.equals(nowPassword, oldPassword)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMsg());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<LevelVo> getLevel(HttpSession session, Long stuId, Integer pageNum, Integer pageSize) {
        Student student;
        boolean showFist = true;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = studentMapper.selectById(stuId);
            Student currentStudent = getStudent(session);

            List<Worship> worships = worshipMapper.selectSevenDaysInfoByStudent(currentStudent);
            if (worships.size() > 0) {
                // 上次膜拜时间
                Date lastWorshipTime = worships.get(0).getWorshipTime();
                if (Objects.equals(DateUtil.formatYYYYMMDD(lastWorshipTime), DateUtil.formatYYYYMMDD(new Date()))) {
                    // 今天已经膜拜过其他人
                    showFist = false;
                }
                long count = worships.stream().filter(worship -> worship.getStudentIdByWorship().equals(student.getId())).count();
                if (count > 0) {
                    // 本周已膜拜过该同学，不能再次膜拜
                    showFist = false;
                }
            }
        }

        LevelVo levelVo = new LevelVo();
        levelVo.setHeadUrl(AliyunInfoConst.host + student.getHeadUrl());
        levelVo.setShowFist(showFist);
        levelVo.setNickname(student.getNickname());
        // 获取当前勋章父勋章的索引
        Map<String, String> parentMap = new HashMap<>(16);

        // 获取学生当前的等级信息
        Map<String, String> childMap = getLevelInfo(levelVo, student, parentMap);

        levelVo.setChildLevelIndex(childMap == null ? 1 : childMap.size());
        levelVo.setParentLevelIndex(parentMap.size() == 0 ? 0 : parentMap.size() - 1);

        // 获取学生已获取的勋章图片url
        PageInfo<String> pageInfo = getHadMedalByPage(pageNum, pageSize, student);
        levelVo.setMedalImgUrl(pageInfo);

        return ServerResponse.createBySuccess(levelVo);
    }

    @Override
    public ServerResponse<PageInfo<String>> getMedalByPage(HttpSession session, Long stuId, Integer pageNum, Integer pageSize) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = studentMapper.selectById(stuId);
        }

        Integer sex = student.getSex();
        PageHelper.startPage(pageNum, pageSize);
        List<String> urlList = medalMapper.selectHadBigMedalImgUrl(student);
        PageInfo<String> pageInfo1 = new PageInfo<>(urlList);

        List<String> urls = new ArrayList<>(urlList.size());
        urlList.forEach(url -> {
            if (url != null && url.contains("#")) {
                urls.add(sex == 1 ? AliyunInfoConst.host + url.split("#")[0] : AliyunInfoConst.host + url.split("#")[1]);
            } else {
                urls.add(AliyunInfoConst.host + url);
            }
        });
        PageInfo<String> pageInfo = new PageInfo<>(urls);
        pageInfo.setPages(pageInfo1.getPages());
        pageInfo.setTotal(pageInfo1.getTotal());
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<Map<String, Object>> getAllMedal(HttpSession session, Long stuId, Integer pageNum, Integer pageSize) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = studentMapper.selectById(stuId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> medalImgUrlList = medalMapper.selectMedalImgUrl(student);
        List<Map<String, Object>> medalImgUrlListTemp = getAllMedalImgUrl(student, medalImgUrlList);

        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(medalImgUrlList);
        PageInfo<Map<String, Object>> mapPageInfo1 = new PageInfo<>(medalImgUrlListTemp);
        mapPageInfo1.setTotal(mapPageInfo.getTotal());
        mapPageInfo1.setPages(mapPageInfo.getPages());

        Map<String, Object> map = new HashMap<>(16);
        map.put("petName", student.getPetName());
        map.put("list", mapPageInfo1);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<ChildMedalVo> getChildMedal(HttpSession session, Long stuId, Long medalId) {
        Student student;
        if (stuId == null) {
            student = getStudent(session);
        } else {
            student = studentMapper.selectById(stuId);
        }
        ChildMedalVo childMedalInfo = getChildMedalInfo(student, medalId);
        return ServerResponse.createBySuccess(childMedalInfo);
    }

    @Override
    public ServerResponse<Map<String, Object>> getWorship(HttpSession session, Integer type, Integer pageNum, Integer pageSize) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>(16);
        // 本周我被膜拜的次数
        Date date = new Date();
        Date firstDayOfWeek = WeekUtil.getFirstDayOfWeek(date);
        Date lastDayOfWeek = WeekUtil.getLastDayOfWeek(date);
        int count = worshipMapper.countByWorshipedThisWeed(student, DateUtil.formatYYYYMMDD(firstDayOfWeek), DateUtil.formatYYYYMMDD(lastDayOfWeek));
        map.put("count", count);

        // 膜拜记录
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, String>> mapList = worshipMapper.selectStudentNameAndTime(student, type);
        map.put("list", new PageInfo<>(mapList));

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Student getByUuid(String uuid) {
        return studentMapper.selectByUuid(uuid);
    }

    @Override
    public Student getByOpenId(String openId) {
        return studentMapper.selectByOpenId(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGold(String openId, Integer gold) {
        Student student = studentMapper.selectByOpenId(openId);
        gold = GoldUtil.addSmallAppGold(student, gold);

        GoldLogUtil.saveStudyGoldLog(student.getId(), "观看夺分队长每日复习学习视频", gold);
    }

    @Override
    public boolean goldCountLimit(Long studentId) {
        String key = RedisKeysConst.STUDENT_DAY_TOTAL_GOLD + ":" + DateUtil.formatYYYYMMDD(new Date());
        Object o = redisTemplate.opsForHash().get(key, studentId);
        return o != null && (int) o >= GoldUtil.MAX_GOLD;
    }

    @Override
    public boolean goldSmallAppCountLimit(String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        Long studentId = student.getId();
        String key = RedisKeysConst.STUDENT_SMALL_APP_DAY_TOTAL_GOLD + ":" + DateUtil.formatYYYYMMDD(new Date());
        Object o = redisTemplate.opsForHash().get(key, studentId);
        if (o != null && (int) o >= GoldUtil.SMALL_APP_MAX_GOLD) {
            return true;
        }
        return this.goldCountLimit(studentId);
    }

    /**
     * 获取已经获取的勋章图片
     *
     * @param pageNum
     * @param pageSize
     * @param student
     * @return
     */
    private PageInfo<String> getHadMedalByPage(Integer pageNum, Integer pageSize, Student student) {
        Integer sex = student.getSex();
        PageHelper.startPage(pageNum, pageSize);
        List<String> urlList = medalMapper.selectHadMedalImgUrl(student);
        List<String> urls = new ArrayList<>(urlList.size());
        urlList.forEach(url -> {
            if (url != null && url.contains("#")) {
                urls.add(sex == 1 ? url.split("#")[0] : url.split("#")[1]);
            } else {
                urls.add(url);
            }
        });
        return new PageInfo<>(urls);
    }

    private List<Map<String, Object>> getAllMedalImgUrl(Student student, List<Map<String, Object>> medalImgUrlList) {
        List<Map<String, Object>> medalImgUrlListTemp = new ArrayList<>(medalImgUrlList.size());
        Integer sex = student.getSex();
        medalImgUrlList.forEach(map -> {
            Map<String, Object> mapTemp = new HashMap<>(16);
            if (map.get("imgUrl").toString().contains("#")) {
                mapTemp.put("imgUrl", sex == 1 ? AliyunInfoConst.host + map.get("imgUrl").toString().split("#")[0] : AliyunInfoConst.host + map.get("imgUrl").toString().split("#")[1]);
            } else {
                mapTemp.put("imgUrl", AliyunInfoConst.host + map.get("imgUrl"));
            }
            mapTemp.put("id", map.get("id"));
            medalImgUrlListTemp.add(mapTemp);
        });

        return medalImgUrlListTemp;
    }

    private ChildMedalVo getChildMedalInfo(Student student, long medalId) {
        List<Map<String, String>> childInfo = medalMapper.selectChildrenInfo(student, medalId);

        List<String> medalImgUrl = new ArrayList<>(childInfo.size());
        StringBuilder sb = new StringBuilder();
        childInfo.forEach(info -> {
            medalImgUrl.add(GetOssFile.getPublicObjectUrl(info.get("imgUrl")));
            sb.append(info.get("content"));
        });

        ChildMedalVo childMedalVo = new ChildMedalVo();
        childMedalVo.setMedalImgUrl(medalImgUrl);
        childMedalVo.setContent(sb.toString());
        return childMedalVo;
    }

    private Map<String, String> getLevelInfo(LevelVo levelVo, Student student, Map<String, String> parentMap) {
        List<Level> levels = levelMapper.selectList(new QueryWrapper<Level>().orderBy(true, true, "id"));
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
                    levelVo.setLevelImgUrl(GetOssFile.getPublicObjectUrl(level.getImgUrlLevel()));
                    levelVo.setChildName(GetOssFile.getPublicObjectUrl(level.getImgUrlWord()));
                    break;
                }
            } else {
                levelVo.setLevelImgUrl(GetOssFile.getPublicObjectUrl(level.getImgUrlLevel()));
                levelVo.setChildName(GetOssFile.getPublicObjectUrl(level.getImgUrlWord()));
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
            studentMapper.updateById(byWorship);
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
                    complete[i] = Math.min(day, totalPlan[i]);
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
    private static void setAwardState(int[] complete, int[] totalPlan, Award award, int i) {
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

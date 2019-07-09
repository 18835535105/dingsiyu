package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.MacIpUtil;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.ValidateCode;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.LoginService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登陆业务实现层
 *
 * @author qizhentao
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements LoginService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudyFlowMapper studyFlowMapper;

    @Autowired
    private StudentFlowMapper studentFlowMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private StudentExpansionMapper studentExpansionMapper;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private JoinSchoolMapper joinSchoolMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private LocationUtil locationUtil;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private RankOpt rankOpt;

    public Student loginJudge(String account, String password) {
        Student st = new Student();
        st.setAccount(account);
        st.setPassword(password);
        return studentMapper.loginJudge(st);
    }

    @Override
    public ServerResponse<String> updatePassword(String password, HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student.setPassword(password);
        int state = studentMapper.updateByPrimaryKeySelective(student);
        if (state == 1) {
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            return ServerResponse.createBySuccessMessage("修改成功");
        } else {
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 单词首页数据
     */
    @Override
    public ServerResponse<Object> index(HttpSession session) {
        Student student = super.getStudent(session);
        // 学生id
        Long studentId = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 判断学生是否有智能版课程
        int count = studentStudyPlanMapper.countByStudentIdAndType(studentId, 1);
        if (count == 0) {
            result.put("hasCapacity", false);
        } else {
            result.put("hasCapacity", true);
        }

        // 判断学生是否需要进行游戏测试
        int gameCount = testRecordMapper.countGameCount(student);
        if (gameCount == 0) {
            // 第一次进行游戏测试
            result.put("game", true);
        }

        // 获取学生当前正在学习的单元信息
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(studentId, 1);
        if (capacityStudentUnit == null) {
            logger.error("学生[{}]-[{}]没有智能版课程！", student.getId(), student.getStudentName());
            return ServerResponse.createBySuccess(result);
        }

        // 判断学生是否已经学完教师分配的所有计划，如果已学完所有计划，开始之旅按钮将被替换并且不能被点击
        if (isLearnedAllPlan(studentId)) {
            result.put("learnedAllPlan", true);
        }

        // 封装学生当前学习的课程信息
        this.packageUnitInfo(result, capacityStudentUnit);

        // 封装学生相关信息
        this.packageStudentInfo(student, result);

        // 封装时长信息
        this.getIndexTime(session, student, result);

        // 1.学生当前单词模块学的那个单元
        Integer unitId = Integer.valueOf(capacityStudentUnit.getUnitId().toString());

        // 封装各个学习模块的学习状况信息
        this.packageModelInfo(studentId, result, unitId);

        // 封装无用字段
        this.ignoreField(result);

        // 获取学生需要执行的节点信息
        this.getNode(session, result, student);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 封装无用字段，这些字段在项目中已没有具体含义，但防止前端因字段缺少报错，暂时保留这些字段
     * 并将对应值写死
     *
     * @param result
     */
    private void ignoreField(Map<String, Object> result) {
        result.put("amount1", 0);
        result.put("amount2", 0);
        result.put("amount3", 0);
        result.put("amount0", 0);
        result.put("hide", false);
    }

    /**
     * 封装各个学习模块的学习状况信息
     *
     * @param studentId
     * @param result
     * @param unitId
     */
    private void packageModelInfo(Long studentId, Map<String, Object> result, Integer unitId) {
        // 单词图鉴模块, 单元下共有多少带图片的单词
        Long countWord = null;
        Map<String, Object> map;

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        for (int i = 0; i < 4; i++) {
            map = new HashMap<>(16);
            if(i == 0){
                // 单词图鉴单元总单词数
                countWord = unitVocabularyMapper.selectWordPicCountByUnitId(unitId);
            } else if (i == 1) {
                // 单元下一共有多少单词
                countWord = unitVocabularyMapper.selectWordCountByUnitId((long) unitId);
            }

            //-- 2.某学生某单元某模块单元闯关测试得了多少分max
            Integer point = testRecordMapper.selectPoint(studentId, unitId, "单元闯关测试", i);

            //-- 3.某学生某单元某模块单词 学了多少 ./
            Integer sum = learnMapper.selectNumberByStudentId(studentId, unitId, i);

            map.put("state", false);

            //-- 4.某学生某单元某模块学习速度;
            //select SUM(valid_time) from duration where unit_id = 1 and student_id = 1 and study_model = '1'
            Integer sumValid = durationMapper.valid_timeIndex(studentId, unitId, i);
            if (sumValid == null) {
                sumValid = 0;
            }
            int speed = (int) (BigDecimalUtil.div(sum, sumValid) * 3600);

            // 封装返回结果
            // 已做单元闯关
            if (point != null) {
                // 分数
                map.put("point", point);
                // 速度
                map.put("speed", speed);
                // 方框状态
                if (point >= 80) {
                    map.put("condition", 1);
                } else {
                    map.put("condition", 2);
                }
                map.put("sum", "");
                map.put("countWord", "");
            } else { // 未做单元闯关

                map.put("sum", sum);
                map.put("countWord", countWord);
                // 方框状态
                map.put("condition", 3);
                // 分数
                map.put("point", "");
                // 速度
                map.put("speed", speed);
            }
            result.put(i + "", map);
        }
    }

    /**
     * 封装学生相关信息
     *
     * @param student
     * @param result
     */
    private void packageStudentInfo(Student student, Map<String, Object> result) {
        // 学生id
        result.put("student_id", student.getId());
        result.put("role", "1");
        // 账号
        result.put("account", student.getAccount());
        // 姓名
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", AliyunInfoConst.host + student.getHeadUrl());
        // 宠物
        result.put("partUrl", AliyunInfoConst.host + student.getPartUrl());
        // 宠物名
        result.put("petName", student.getPetName());
        result.put("schoolName", student.getSchoolName());
    }

    /**
     * 封装学生当前学习的课程信息
     *
     * @param result
     * @param capacityStudentUnit
     */
    private void packageUnitInfo(Map<String, Object> result, CapacityStudentUnit capacityStudentUnit) {
        // 当前单词所学课程id
        result.put("course_id", capacityStudentUnit.getCourseId());
        // 当前单词所学课程名
        result.put("course_name", capacityStudentUnit.getCourseName());
        // 当前单词所学单元id
        result.put("unit_id", capacityStudentUnit.getUnitId());
        // 根据单元id查询单元名 - 需要根据学生单元id实时去查询单元名
        result.put("unit_name", capacityStudentUnit.getUnitName());
        result.put("version", capacityStudentUnit.getVersion());
    }

    /**
     * 判断学生是否已经学完教师分配的所有计划
     *
     * @param studentId
     * @return
     */
    private boolean isLearnedAllPlan(Long studentId) {
        int count = studentStudyPlanMapper.countUnlearnedPlan(studentId, 1);
        return count == 0;
    }

    /**
     * 获取学生需要执行的节点信息
     *
     * @param session
     * @param result
     * @param stu
     */
    private void getNode(HttpSession session, Map<String, Object> result, Student stu) {
        // 判断学生是否需要进行智能复习
        if (session.getAttribute("needCapacityReview") != null) {
            StudyFlow studyFlow;
            StringBuilder sb = new StringBuilder();
            // 上次登录期间学生的单词学习信息
            Duration duration = durationMapper.selectLastLoginDuration(stu.getId());
            if (duration != null) {
                List<Learn> learns = learnMapper.selectLastLoginStudy(stu.getId(), duration.getLoginTime(), duration.getLoginOutTime(), null);
                if (learns.size() > 0) {
                    // 存储单词id及单元
                    List<Map<String, Object>> maps = ReviewServiceImpl.packageLastLoginLearnWordIds(learns);

                    String[] str = {"单词图鉴", "慧记忆", "慧听写", "慧默写"};
                    Integer memoryCount;
                    for (int i = 0; i < 4; i++) {
                        memoryCount = capacityReviewMapper.countCapacityByUnitIdAndWordId(stu.getId(), maps, i, str[i]);
                        if (memoryCount != null && memoryCount > 0) {
                            sb.append(str[i]).append("-");
                        }
                    }
                }
            }

            String needReviewStr = sb.toString();
            if (needReviewStr.length() == 0) {
                // 不需要进行智能复习
                studyFlow = studyFlowMapper.getFlowInfoByStudentId(stu.getId());
            } else {
                // 需要进行智能复习
                studyFlow = studyFlowMapper.selectById(7);
                session.setAttribute("needReview", needReviewStr);
            }
            if (studyFlow == null) {
                logger.error("学生[{}]-[{}]还没有初始化智能版流程节点！", stu.getId(), stu.getStudentName());
                return;
            }
            if (studyFlow.getModelName().contains("单元闯关")) {
                String token = TokenUtil.getToken();
                result.put("token", token);
                session.setAttribute("token", token);
            }
            result.put("needReview", needReviewStr);
            result.put("nodeId", studyFlow.getId());
            result.put("nodeName", studyFlow.getFlowName());
            result.put("flowName", studyFlow.getModelName());
            session.removeAttribute("needCapacityReview");
        } else {
            // 获取智能化节点数据
            StudyFlow flow;
            if (session.getAttribute("needReview") != null) {
                // 需要进行智能复习
                flow = studyFlowMapper.selectById(7);
                result.put("needReview", session.getAttribute("needReview"));
            } else {
                flow = studyFlowMapper.getFlowInfoByStudentId(stu.getId());
                result.put("needReview", "");
            }
            if(flow != null){
                result.put("nodeId", flow.getId());
                result.put("nodeName", flow.getFlowName());

                if (flow.getModelName().contains("单元闯关")) {
                    String token = TokenUtil.getToken();
                    result.put("token", token);
                    session.setAttribute("token", token);
                }
            }
            // 学生当前节点模块名
            result.put("flowName", studentFlowMapper.getStudentFlow(stu.getId()));
        }
    }


    /**
     * 例句首页数据
     */
    @Override
    public ServerResponse<Object> sentenceIndex(HttpSession session) {

        Student student = super.getStudent(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);
        result.put("role", "1");

        // 学生id
        result.put("student_id", student.getId());
        // 账号
        result.put("account", student.getAccount());
        // 姓名
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", AliyunInfoConst.host + student.getHeadUrl());
        result.put("schoolName", student.getSchoolName());

        this.getIndexTime(session, student, result);

        return ServerResponse.createBySuccess(result);
    }

    private void getIndexTime(HttpSession session, Student student, Map<String, Object> result) {
        // 有效时长
        Integer valid = getTodayValidTime(student.getId());
        // 在线时长
        Integer online = getTodayOnlineTime(session);
        // 今日学习效率
        if (valid != null && online != null) {
            if (valid >= online) {
                logger.warn("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            }
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }
        result.put("online", online);
        result.put("valid", valid);
    }

    @Override
    public Integer judgePreschoolTest(Long id) {
        return testRecordMapper.judgePreschoolTest(id);
    }

    /**
     * 点击头像,需要展示的信息
     * <p>
     * 今日已学单词/例句   date_format(learn_time, '%Y-%m-%d')
     * 总金币/今日金币
     * 我的等级/下一个等级
     * 距离下一级还差多少金币
     */
    @Override
    public ServerResponse<Object> clickPortrait(HttpSession session) {

        Student student = getStudent(session);
        Long studentId = student.getId();

        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(studentId, 1);
        if (capacityStudentUnit == null) {
            logger.error("学生：[{}]-[{}] 没有初始化智能版课程！", studentId, student.getStudentName());
            return ServerResponse.createBySuccess(ResponseCode.FORBIDDEN.getCode(), ResponseCode.FORBIDDEN.getMsg());
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put("sex",student.getSex());
        // 获取今日已学单词
        int learnWord = learnMapper.getTodayWord(DateUtil.formatYYYYMMDD(new Date()), studentId);
        // 获取今日已学例句
        int learnSentence = learnMapper.getTodaySentence(DateUtil.formatYYYYMMDD(new Date()), studentId);
        //获取今日已学课文数
        int learnTeks=learnMapper.getTodyTeks(DateUtil.formatYYYYMMDD(new Date()),studentId);
        map.put("learnWord", learnWord);
        map.put("learnSentence", learnSentence);
        map.put("learnTeks", learnTeks);

        // 获取我的总金币
        int myGold = (int) BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        map.put("myGold", myGold);
        getMyLevelInfo(map, myGold);

        // 获取今日获得金币
        getTodayGold(studentId, map);

        return ServerResponse.createBySuccess(map);
    }

    /**
     * 封装今日得到的金币数
     *
     * @param studentId
     * @param map
     */
    private void getTodayGold(Long studentId, Map<String, Object> map) {
        List<String> list = runLogMapper.getStudentGold(DateUtil.formatYYYYMMDD(new Date()), studentId);
        getTodayGold(map, list);
    }

    /**
     * 封装我的等级信息
     *
     * @param map
     * @param myGold
     */
    private void getMyLevelInfo(Map<String, Object> map, int myGold) {
        getMyLevelInfo(map, myGold, redisOpt);
    }

    /**
     * 封装今日得到的金币数
     *
     * @param map
     * @param list
     */
    public static void getTodayGold(Map<String, Object> map, List<String> list) {
        double count = 0.0;
        String regex = "#(.*)#";
        Pattern pattern = Pattern.compile(regex);
        for (String str : list) {
            // 匹配类
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                count += Double.parseDouble(matcher.group(1));
            }
        }
        map.put("myThisGold", count);
    }

    /**
     * 封装我的等级信息
     *
     * @param map
     * @param myGold
     */
    public static void getMyLevelInfo(Map<String, Object> map, int myGold, RedisOpt redisOpt) {
        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

        int myrecord = 0;
        // 下一等级索引
        int j = 1;
        int size = levels.size();
        for (int i = 0; i < size; i++) {
            // 循环的当前等级分数
            int levelGold = (int) levels.get(i).get("gold");
            // 下一等级分数
            int nextLevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");
            // 下一等级索引
            int si = (i + 1) < size ? (i + 1) : i;
            boolean flag = (myGold >= myrecord && myGold < nextLevelGold) || j == size;
            if (flag) {
                // 我的等级
                map.put("childName", levels.get(i).get("child_name"));
                // 距离下一等级还差多少金币
                map.put("jap", (nextLevelGold - myGold));
                // 我的等级图片
                map.put("imgUrl", AliyunInfoConst.host + levels.get(i).get("img_url"));
                // 下一个等级名/ 下一个等级需要多少金币 / 下一个等级图片
                // 下一级等级名
                map.put("childNameBelow", levels.get(si).get("child_name"));
                // 下一级金币数量
                map.put("japBelow", (nextLevelGold));
                // 下一级等级图片
                map.put("imgUrlBelow", AliyunInfoConst.host + levels.get(si).get("img_url"));
                break;
            }
            myrecord = levelGold;
            j++;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse loginJudge(String account, String password, HttpSession session, HttpServletRequest request, String code) {

        Student stu = this.loginJudge(account, password);

        // 1.账号/密码错误
        if (stu == null) {
            logger.warn("学生账号[{}]账号或密码输入错误，登录失败。", account);
            return ServerResponse.createByErrorMessage("账号或密码输入错误");
            // 2.账号已关闭
        } else if (stu.getStatus() != null && stu.getStatus() == 2) {
            return ServerResponse.createByErrorMessage("此账号已关闭");

        } else if (stu.getStatus() != null && stu.getStatus() == 3) {
            // 账号被删除
            return ServerResponse.createByErrorMessage("此账号已被删除");
            // 3.正确
        } else {
            // 封装返回数据
            Map<String, Object> result = new HashMap<>(16);

            // 将登录的学生放入指定 key 用于统计在线人数
            redisTemplate.opsForZSet().add(RedisKeysConst.ZSET_ONLINE_USER, stu.getId(), 1);

            // 学生首次登陆系统，初始化其账号有效期，勋章信息
            initAccountTime(stu);

            // 账号有效期
            Date date = stu.getAccountTime();
            // 当前时间
            Date current = new Date();
            // 2.此账号已失效
            if (date == null || date.getTime() < current.getTime()) {
                return ServerResponse.createByErrorMessage("此账号已失效");
            }
            // 3.账号即将过期，请及时续期
            long difference = (date.getTime() - current.getTime()) / 86400000;
            long l = Math.abs(difference);
            if (l <= 1) {
                result.put("accountDate", "账号即将过期，请及时续期");
            }

            // 学生id
            result.put("student_id", stu.getId());
            // 账号
            result.put("account", stu.getAccount());
            // 姓名
            result.put("studentName", stu.getStudentName());
            // 头像
            result.put("headUrl", AliyunInfoConst.host + stu.getHeadUrl());

            // 记录登录信息
            String ip = saveLoginRunLog(stu, request);

            isStudentEx(stu);

            // 当日首次登陆奖励5金币（日奖励）
            saveDailyAward(stu);

            // 一个账户只能登陆一台
            judgeMultipleLogin(session, stu);

            // 2.判断是否需要完善个人信息
            if (!StringUtils.isNotBlank(stu.getHeadUrl())) {

                // 学校
                result.put("school_name", stu.getSchoolName());
                // 到期时间
                result.put("account_time", stu.getAccountTime());

                // 2.1 跳到完善个人信息页面
                logger.info("学生[{} -> {} -> {}]登录成功前往完善信息页面。", stu.getId(), stu.getAccount(), stu.getStudentName());
                return ServerResponse.createBySuccess("2", result);
            }

            // 判断学生是否需要进行智能复习,学生登录时在session中增加该字段，在接口 /login/vocabularyIndex 如果获取到该字段不为空，
            // 判断学生是否需要进行智能复习，如果该字段为空不再判断是否需要进行智能复习
            session.setAttribute("needCapacityReview", true);

            // 判断学生是否是在加盟校半径 1 公里外登录
            final String finalIP = ip;
            executorService.execute(() -> this.isOtherLocation(stu, finalIP));

            // 正常登陆
            logger.info("学生[{} -> {} -> {}]登录成功。", stu.getId(), stu.getAccount(), stu.getStudentName());
            return ServerResponse.createBySuccess("1", result);
        }
    }

    /**
     * 判断学生是否是在加盟校半径 1 公里外登录
     *
     * @param stu
     * @param ip  学生登录的 ip 地址
     */
    private void isOtherLocation(Student stu, String ip) {
        JoinSchool joinSchool = this.getJoinSchool(stu, ip);
        if (joinSchool == null) {
            return;
        }

        // 校验距离
        this.checkDistance(stu, ip, joinSchool);
    }

    private void checkDistance(Student stu, String ip, JoinSchool joinSchool) {
        LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude(ip);

        LongitudeAndLatitude schoolLongitudeAndLatitude = new LongitudeAndLatitude();
        schoolLongitudeAndLatitude.setLatitude(joinSchool.getLatitude());
        schoolLongitudeAndLatitude.setLongitude(joinSchool.getLongitude());

        int distance = locationUtil.getDistance(longitudeAndLatitude, schoolLongitudeAndLatitude);
        // 学生距加盟校的最远距离
        final int radius = 1000;
        if (distance > radius) {
            logger.warn("学生 [{} - {} - {}] 登录距离距加盟校 [{}] 超过 [{}] 米！", stu.getId(), stu.getAccount(), stu.getStudentName(), joinSchool.getSchoolName(), radius);
            Date date = new Date();
            // 学生在加盟校 1000 米之外登录，保存异地登录记录
            Location location = new Location();
            location.setAccount(stu.getAccount());
            location.setCreateTime(date);
            location.setIp(ip);
            location.setLocation(longitudeAndLatitude.getAddresses());
            location.setStudentId(stu.getId());
            location.setUpdateTime(date);
            try {
                locationMapper.insert(location);
            } catch (Exception e) {
                logger.error("保存学生 [{} - {} - {}]异地登录信息失败！", stu.getId(), stu.getAccount(), stu.getStudentName(), e);
            }
        }
    }

    /**
     * 获取加盟校信息并做校验
     *
     * @param stu
     * @param ip
     * @return
     */
    private JoinSchool getJoinSchool(Student stu, String ip) {
        if (stu.getTeacherId() == null) {
            logger.warn("学生 [{} - {} - {}] 没有所属教师！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        if (StringUtils.isEmpty(ip)) {
            logger.warn("没有获取到学生 [{} - {} - {}] 的登录 ip", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        // 学生的校管 id
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(stu.getTeacherId());
        if (schoolAdminId == null) {
            schoolAdminId = Integer.parseInt(stu.getTeacherId().toString());
        }

        // 查询学生所属加盟校地址
        JoinSchool joinSchool = joinSchoolMapper.selectByUserId(schoolAdminId);

        if (joinSchool == null) {
            logger.warn("没有获取到学生 [{} - {} - {}] 所属的加盟校信息！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        if (StringUtils.isEmpty(joinSchool.getLatitude()) || StringUtils.isEmpty(joinSchool.getLongitude())) {
            logger.warn("学生 [{} - {} - {}] 所属的加盟校信息中没有坐标信息！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }
        return joinSchool;
    }

    private void judgeMultipleLogin(HttpSession session, Student stu) {
        Object object = redisTemplate.opsForHash().get(RedisKeysConst.LOGIN_SESSION, stu.getId());
        if (object != null) {
            Long oldStudentId = null;
            Map<String, Object> oldSessionMap = RedisOpt.getSessionMap(object.toString());
            if (oldSessionMap != null && oldSessionMap.get(UserConstant.CURRENT_STUDENT) != null) {
                oldStudentId = ((Student) oldSessionMap.get(UserConstant.CURRENT_STUDENT)).getId();
            }

            // 如果账号 session 相同说明是同一个浏览器中，并且不是同一个账号，不再更改其 session 中登录信息
            if (Objects.equals(oldStudentId, stu.getId()) && Objects.equals(object, session.getId())) {
                return;
            }

            // 如果账号登录的session不同，保存前一个session的信息
            if (oldSessionMap != null) {
                saveDurationInfo(oldSessionMap);
                saveLogoutLog(stu, runLogMapper, logger);
            }
        }

        // 学生首次在当前浏览器登录时记录其登录信息
        Date loginTime = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        session.setAttribute(UserConstant.CURRENT_STUDENT, stu);
        session.setAttribute(TimeConstant.LOGIN_TIME, loginTime);

        Map<String, Object> sessionMap = new HashMap<>(16);
        sessionMap.put(UserConstant.CURRENT_STUDENT, stu);
        sessionMap.put(TimeConstant.LOGIN_TIME, loginTime);
        sessionMap.put("sessionId", session.getId());

        redisTemplate.opsForHash().put(RedisKeysConst.SESSION_MAP, session.getId(), sessionMap);
        redisTemplate.opsForHash().put(RedisKeysConst.LOGIN_SESSION, stu.getId(), session.getId());
    }

    /**
     * 学生首次登陆系统初始化其账号有效期
     *
     * @param student
     */
    private void initAccountTime(Student student) {
        boolean flag = redisOpt.firstLogin(student.getId());
        if (flag) {
            student.setAccountTime(new Date(System.currentTimeMillis() + student.getRank() * 24 * 60 * 60 * 1000L));
            studentMapper.updateById(student);


            executorService.execute(() -> {
                // 初始化学生勋章信息
                dailyAwardAsync.initAward(student);

                // 初始化学生排行数据
                initRankInfo(student);
            });


        }
    }

    /**
     * 初始化学生排行数据
     *
     * @param student
     */
    private void initRankInfo(Student student) {
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        double goldCount = BigDecimalUtil.add(student.getOfflineGold(), student.getSystemGold());
        rankOpt.addOrUpdate(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), goldCount);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, student.getId(), goldCount);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_GOLD_RANK, student.getId(), goldCount);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_CCIE_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_CCIE_RANK, student.getId(), 0.0);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_MEDAL_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_MEDAL_RANK, student.getId(), 0.0);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_WORSHIP_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_WORSHIP_RANK , student.getId(), 0.0);
    }

    public static void main(String[] args) {
        String str = "20180101";
        System.out.println(str.substring(0, 4)+"-"+str.substring(4,6)+"-"+str.substring(6,8));
    }
    /**
     * 学生当天首次登陆奖励5金币，并计入日奖励信息
     *
     * @param stu
     */
    private void saveDailyAward(Student stu) {
        int count = runLogMapper.countStudentTodayLogin(stu);
        if (count == 1) {
            executorService.execute(() -> {
                // 每日首次登陆日奖励
                dailyAwardAsync.firstLogin(stu);

                // 当天首次登陆将学生每日闯关获取金币数清空
                this.resetTestGold(stu);

                // 如果昨天辉煌荣耀奖励没有更新，将指定的奖励当前进度置为0
                medalAwardAsync.updateHonour(stu);

                // 将天道酬勤中未达到领取条件的勋章重置
                medalAwardAsync.resetLevel(stu);

                // 值得元老勋章
                medalAwardAsync.oldMan(stu);
            });
        }
    }

    private void resetTestGold(Student stu) {
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(stu.getId());
        if (studentExpansion == null) {
            studentExpansion = new StudentExpansion();
            studentExpansion.setStudentId(stu.getId());
            studentExpansion.setTestGoldAdd(0);
            try {
                studentExpansionMapper.insert(studentExpansion);
            } catch (Exception e) {
                logger.error("当日首次登陆重置学生闯关类获取金币数量失败[{}]->[{}]", stu.getId(), stu.getStudentName(), e);
            }
        } else {
            studentExpansion.setTestGoldAdd(0);
            try {
                studentExpansionMapper.updateById(studentExpansion);
            } catch (Exception e) {
                logger.error("当日首次登陆重置学生闯关类获取金币数量失败[{}]->[{}]", stu.getId(), stu.getStudentName(), e);
            }
        }
    }

    private String saveLoginRunLog(Student stu, HttpServletRequest request) {
        String ip = null;
        try {
            ip = MacIpUtil.getIpAddr(request);
        } catch (Exception e) {
            logger.error("获取学生登录IP地址出错，error=[{}]", e.getMessage());
        }

        RunLog runLog = new RunLog(stu.getId(), 1, "学生[" + stu.getStudentName() + "]登录,ip=[" + ip +"]", new Date());
        try {
            runLogMapper.insert(runLog);
        } catch (Exception e) {
            logger.error("学生 {} -> {} 登录信息记录失败！ExceptionMsg:{}", stu.getId(), stu.getStudentName(), e.getMessage(), e);
        }
        return ip;
    }

    public static void saveLogoutLog(Student student, RunLogMapper runLogMapper, Logger logger) {
        // 查询学生登录日志中最后一条记录时登录信息还是退出信息
        RunLog lastRunLog = runLogMapper.selectLastRunLogByOperateUserId(student.getId());
        if (lastRunLog == null || !lastRunLog.getLogContent().contains("退出登录")) {
            RunLog runLog = new RunLog(student.getId(), 1, "学生[" + student.getStudentName() + "]退出登录", new Date());
            try {
                runLogMapper.insert(runLog);
            } catch (Exception e) {
                logger.error("记录学生 [{}]->[{}] 退出登录信息失败！", student.getId(), student.getStudentName(), e);
            }
        }
    }

    @Override
    public void loginOut(HttpSession session, HttpServletRequest request) {
        session.invalidate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDurationInfo(Map<String, Object> sessionMap) {
        if (sessionMap != null) {
            Student student = (Student) sessionMap.get(UserConstant.CURRENT_STUDENT);
            Date loginTime = DateUtil.parseYYYYMMDDHHMMSS((Date) sessionMap.get(TimeConstant.LOGIN_TIME));
            Date loginOutTime = DateUtil.parseYYYYMMDDHHMMSS(new Date());
            //存放登入退出时间
            redisTemplate.opsForHash().put(RedisKeysConst.STUDENT_LOGINOUT_TIME, student.getId(), DateUtil.DateTime(new Date()));
            if (loginTime != null && loginOutTime != null) {
                // 判断当前登录时间是否已经记录有在线时长信息，如果没有插入记录，如果有无操作
                int count = durationMapper.countOnlineTimeWithLoginTime(student, loginTime);
                if (count == 0) {
                    redisTemplate.opsForHash().delete(RedisKeysConst.LOGIN_SESSION, student.getId());

                    Long onlineTime = (loginOutTime.getTime() - loginTime.getTime()) / 1000;
                    Duration duration = new Duration();
                    duration.setStudentId(student.getId());
                    duration.setOnlineTime(onlineTime);
                    duration.setLoginTime(loginTime);
                    duration.setLoginOutTime(loginOutTime);
                    duration.setValidTime(0L);
                    durationMapper.insert(duration);
                }
            }
        }
    }

    @Override
    public void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ValidateCode vCode = new ValidateCode(74,31,4,150);
        session.removeAttribute("validateCode");
        vCode.write(response.getOutputStream());
        session.setAttribute("validateCode", vCode.getCode());
        vCode.write(response.getOutputStream());
    }

    @Override
    public Object getRiepCount(HttpSession session) {
        Student student = getStudent(session);
        Map<String,Object> map=new HashMap<>();
        map.put("partUrl", AliyunInfoConst.host + student.getPartUrl());
        Integer count = studentMapper.getVocabularyCountByStudent(student.getId());
        map.put("vocabularyCount",count);
        Integer sentenceCount = studentMapper.getSentenceCountByStudent(student.getId());
        map.put("sentenceCount",sentenceCount);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object getModelStatus(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>();
        boolean isHave = false;
        if (type.equals(1)) {
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selByStudentIdAndType(student.getId(), type);
            if (capacityStudentUnit != null) {
                isHave = true;
            }
        }
        if (type.equals(2) || type.equals(3)) {
            List<Map<String, Object>> maps = null;
            if(type==2){
                maps=studentStudyPlanMapper.selBySentenceStudentId(student.getId(), type);
            }else{
                maps = studentStudyPlanMapper.selByStudentId(student.getId(), type);
            }
            if (maps != null && maps.size() > 0) {
                isHave = true;
            }
        }
        if (type.equals(4)) {
            StudentStudyPlan letterPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
            StudentStudyPlan symbolPlan = studentStudyPlanMapper.selLetterByStudentId(student.getId());
            if (letterPlan != null || symbolPlan != null) {
                isHave = true;
            }
        }
        map.put("isHave", isHave);
        return ServerResponse.createBySuccess(map);
    }
}

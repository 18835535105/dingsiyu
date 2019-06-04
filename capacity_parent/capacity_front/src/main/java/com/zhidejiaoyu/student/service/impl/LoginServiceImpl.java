package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.MacIpUtil;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
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
import org.junit.Test;
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

    @Autowired
    private CapacityReviewMapper capacityMapper;

    @Autowired
    private StudentWorkDayMapper studentWorkDayMapper;

    @Autowired
    private CalendarMapper calendarMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudyFlowMapper studyFlowMapper;

    @Resource
    private UnitSentenceMapper unitSentenceMapper;

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
    private SentenceUnitMapper sentenceUnitMapper;

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

    @Override
    public Student LoginJudge(String account, String password) {
        Student st = new Student();
        st.setAccount(account);
        st.setPassword(password);
        return studentMapper.LoginJudge(st);
    }

    @Override
    public Integer judgeUser(Long id) {
        return studentMapper.judgeUser(id);
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

        //-- 1.学生当前单词模块学的那个单元
        Integer unitId = null;
        if (student.getUnitId() != null) {
            unitId = Integer.valueOf(capacityStudentUnit.getUnitId().toString());
        }

        if (unitId == null) {
            return ServerResponse.createBySuccess(result);
        }

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
        result.put("headUrl", student.getHeadUrl());
        // 宠物
        result.put("partUrl", student.getPartUrl());
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
        Long studentId = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);
        result.put("role", "1");

        // 学生id
        result.put("student_id", student.getId());
        // 当前例句所学课程id
        result.put("course_id", student.getSentenceCourseId());
        // 当前例句所学课程名
        result.put("course_name", student.getSentenceCourseName());
        // 当前例句所学单元id
        result.put("unit_id", student.getSentenceUnitId());
        // 当前例句所学单元名
        // result.put("unit_name", stu.getSentenceUnitName());
        // 根据单元id查询单元名
        result.put("unit_name", sentenceUnitMapper.getUnitNameByUnitId(student.getSentenceUnitId().longValue()));
        // 账号
        result.put("account", student.getAccount());
        // 姓名
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", student.getHeadUrl());
        result.put("schoolName", student.getSchoolName());

        this.getIndexTime(session, student, result);

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        //-- 1.查询学生当前单词模块学的那个单元
        Integer unitId = student.getSentenceUnitId();

        // 一共有多少例句/.
        Long countWord = unitSentenceMapper.selectSentenceCountByUnitId((long) unitId);

        for (int i = 4; i < 7; i++) {
            Map<String, Object> a = new HashMap<String, Object>();
            //-- 如果 2 = NULL 就跳过4步执行5步   condition = 3(方框为空)
            //-- 如果 2 != NULL 执行4步跳过第5步 , 如果第2步>=80 condition = 1(方框为√), 如果第3步<80 condition = 2(方框为×)

            if (unitId == null) {
                return ServerResponse.createBySuccess(result);
            }

            //-- 2.某学生某单元某模块得了多少分
            //select point from test_record where student_id = #{} and unit_id = #{} and genre = '单元闯关测试' and study_model = '慧记忆'
            Integer point = testRecordMapper.selectPoint(studentId, unitId, "单元闯关测试", i);

            //-- 3.某学生某单元某模块单词学了多少 ./
            //select COUNT(id) from learn where student_id = #{} and unit_id = #{} and study_model = '慧记忆' GROUP BY vocabulary_id
            Integer sum = learnMapper.selectNumberByStudentId(studentId, unitId, i);

            if (point != null && sum != null) {
                //-- 4.某学生某单元某模块学习速度;  单词已学个数/(有效时长m/3600)
                //select SUM(valid_time) from duration where unit_id = 1 and student_id = 1 and study_model = '慧记忆'
                Integer sumValid = durationMapper.valid_timeIndex(studentId, unitId, i);

                Integer speed = sumValid == null ? 0 : (int) (BigDecimalUtil.div(sum, sumValid)*3600);
                // 分数
                a.put("point", point + "");
                // 速度
                a.put("speed", speed + "");
                // 方框状态
                if (point >= 80) {
                    a.put("condition", 1);
                } else {
                    a.put("condition", 2);
                }
                a.put("sum", "");
                a.put("countWord", "");
            } else {
                a.put("sum", sum + "");
                a.put("countWord", countWord + "");
                // 方框状态
                a.put("condition", 3);
                // 分数
                a.put("point", "");
                // 计算学习速度
                Integer sumValid = durationMapper.valid_timeIndex(studentId, unitId, i);
                Integer speed = (int) (BigDecimalUtil.div(sum, sumValid == null ? 0 : sumValid) * 3600);
                // 速度
                a.put("speed", speed);
            }

            result.put(i + "", a);
        }

        // 封装返回的数据 - 智能记忆智能复习数量
        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = s.format(new Date());

        CapacityReview cr = new CapacityReview();
        cr.setUnit_id(Long.valueOf(unitId));
        cr.setStudent_id(studentId);
        cr.setPush(datetime);
        // 听力模块许复习量
        cr.setClassify("4");
        Integer d = capacityMapper.countCapacity_memory(cr);
        // 翻译模块许复习量
        cr.setClassify("5");
        Integer e = capacityMapper.countCapacity_memory(cr);
        // 默写模块许复习量
        cr.setClassify("6");
        Integer f = capacityMapper.countCapacity_memory(cr);
        result.put("amount4", d);
        result.put("amount5", e);
        result.put("amount6", f);
        // 是否需要隐藏学习模块
        if (d >= 10 || e >= 10 || f >= 10) {
            result.put("hide", true);
            //宠物图片
            // result.put("partWGUrl", "../../static/img/edit-user-msg/tips1-6.png");
        } else {
            result.put("hide", false);
        }

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
                logger.error("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            }
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }
        result.put("online", online);
        result.put("valid", valid);
    }

    /**
     * 从session中获取学生id(本类方法)
     *
     * @param session
     * @return
     */
    @SuppressWarnings("unused")
    private long StudentIdBySession(HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        return student.getId();
        //return 3155;
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
        Double myGoldD = studentMapper.myGold(studentId);
        BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
        int myGold = Integer.parseInt(mybd.toString());
        map.put("myGold", myGold);

        // 获取等级规则
        List<Map<String, Object>> levels = levelMapper.selectAll();

        // 我的等级myChildName
        int myrecord = 0;
        int myauto = 1;
        int bre = 0; // 跳出循环

        for (int i = 0; i < levels.size(); i++) {
            // 循环的当前等级分数
            int levelGold = (int) levels.get(i).get("gold");
            // 下一等级分数
            int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");
            // 下一等级索引
            int si = (i + 1) < levels.size() ? (i + 1) : i;

            if (myGold >= myrecord && myGold < xlevelGold) {
                map.put("childName", levels.get(i).get("child_name"));// 我的等级
                map.put("jap", (xlevelGold - myGold)); // 距离下一等级还差多少金币
                map.put("imgUrl", levels.get(i).get("img_url"));// 我的等级图片
                // 下一个等级名/ 下一个等级需要多少金币 / 下一个等级图片
                map.put("childNameBelow", levels.get(si).get("child_name"));// 下一级等级名
                map.put("japBelow", (xlevelGold)); // 下一级金币数量
                map.put("imgUrlBelow", levels.get(si).get("img_url"));// 下一级等级图片
                break;
                // 等级循环完还没有确定等级 = 最高等级
            } else if (myauto == levels.size()) {
                map.put("childName", levels.get(i).get("child_name"));// 我的等级
                map.put("jap", (xlevelGold - myGold)); // 距离下一等级还差多少金币
                map.put("imgUrl", levels.get(i).get("img_url"));// 我的等级图片
                // 下一个等级名/ 下一个等级需要多少金币 / 下一个等级图片
                map.put("childNameBelow", levels.get(si).get("child_name"));// 下一级等级名
                map.put("japBelow", (xlevelGold)); // 下一级金币数量
                map.put("imgUrlBelow", levels.get(si).get("img_url"));// 下一级等级图片
                break;
            }

            myrecord = levelGold;
            myauto++;
        }

        // 获取今日获得金币 date_format(learn_time, '%Y-%m-%d')
        List<String> list = runLogMapper.getStudentGold(DateUtil.formatYYYYMMDD(new Date()), studentId);
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

        return ServerResponse.createBySuccess(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse loginJudge(String account, String password, HttpSession session, HttpServletRequest request, String code) {

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        Student stu = LoginJudge(account, password);

        // 1.账号/密码错误
        if (stu == null) {
            logger.warn("学生账号[{}]账号或密码输入错误，登录失败。", account);
            return ServerResponse.createByErrorMessage("账号或密码输入错误");
            // 2.账号已关闭
        }else if(stu.getStatus() != null && stu.getStatus()==2) {
        	return ServerResponse.createByErrorMessage("此账号已关闭");

        } else if (stu.getStatus() != null && stu.getStatus() == 3) {
            // 账号被删除
            return ServerResponse.createByErrorMessage("此账号已被删除");
            // 3.正确
        } else {
            // 将登录的学生放入指定 key 用于统计在线人数
            redisTemplate.opsForSet().add(RedisKeysConst.ONLINE_USER, stu.getId());

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
            result.put("headUrl", stu.getHeadUrl());

            // 记录登录信息
            String ip = saveLoginRunLog(stu, request);

            isStudentEx(stu);

            // 当日首次登陆奖励5金币（日奖励）
            saveDailyAward(stu);

            // 一个账户只能登陆一台
            judgeMultipleLogin(session, stu);

            // 每次登陆默认打开背景音
            openBackAudio(stu);
            // 2.判断是否需要完善个人信息
            if (!StringUtils.isNotBlank(stu.getHeadUrl())) {

                // 学校
                result.put("school_name", stu.getSchoolName());
                // 到期时间
                result.put("account_time", stu.getAccountTime());

                // 学生首次登陆系统，为其初始化7个工作日,以便判断其二级标签
               // initStudentWorkDay(stu);

                // 2.1 跳到完善个人信息页面
                logger.info("学生[{} -> {} -> {}]登录成功前往完善信息页面。", stu.getId(), stu.getAccount(), stu.getStudentName());
                return ServerResponse.createBySuccess("2", result);
            }

            // 判断学生是否需要进行智能复习,学生登录时在session中增加该字段，在接口 /login/vocabularyIndex 如果获取到该字段不为空，
            // 判断学生是否需要进行智能复习，如果该字段为空不再判断是否需要进行智能复习
            session.setAttribute("needCapacityReview", true);

            // 值得元老勋章
            medalAwardAsync.oldMan(stu);

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

    /**
     * 每次登陆默认打开背景音乐
     *
     * @param student
     */
    private void openBackAudio(Student student) {
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion != null) {
            studentExpansion.setAudioStatus(1);
            studentExpansionMapper.updateById(studentExpansion);
        } else {
            studentExpansion = new StudentExpansion();
            studentExpansion.setStudentId(student.getId());
            studentExpansion.setAudioStatus(1);
            studentExpansionMapper.insert(studentExpansion);
        }
    }

    /**
     * 判断学生是否可以学习同步班课程
     *
     * @param student
     * @param result
     * @return
     */
    private void hasCapacityCourse(Student student, Map<String, Object> result) {
        int count = capacityStudentUnitMapper.countByType(student, 1);
        if (count > 0) {
            result.put("capacity", true);
        }
    }

    /**
     * 判断学生是否可以学习同步班句型课程
     *
     * @param student
     * @param result
     * @return
     */
    private void hasCapacitySentence(Student student, Map<String, Object> result) {
        int count = capacityStudentUnitMapper.countByType(student, 2);
        if (count > 0) {
            result.put("capacitySentence", true);
        }
    }

    /**
     * 判断学生是否可以学习同步班课文课程
     *
     * @param student
     * @param result
     * @return
     */
    private void hasCapacityTeks(Student student, Map<String, Object> result) {
        int count = capacityStudentUnitMapper.countByType(student, 3);
        if (count > 0) {
            result.put("capacityTeks", true);
        }
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
     * @param stu
     */
    private void initAccountTime(Student stu) {
        Long stuId = stu.getId();
        Integer count = runLogMapper.selectLoginCountByStudentId(stuId);
        if (count == 0) {
            stu.setAccountTime(new Date(System.currentTimeMillis() + stu.getRank() * 24 * 60 * 60 * 1000L));
            studentMapper.updateById(stu);

            // 初始化学生勋章信息
            executorService.execute(() -> dailyAwardAsync.initAward(stu));
        }
    }

    /**
     * 为学生初始化7个工作日，用于判断二级标签
     *
     * @param stu
     */
    private void initStudentWorkDay(Student stu) {
        StudentWorkDay studentWorkDay = new StudentWorkDay();
        String today = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String endDay = calendarMapper.selectAfterSevenDay(today);

        studentWorkDay.setStudentId(stu.getId());
        studentWorkDay.setWorkDayBegin(today);
        studentWorkDay.setWorkDayEnd(endDay);

        studentWorkDayMapper.insert(studentWorkDay);
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
        RunLog runLog = new RunLog(student.getId(), 1, "学生[" + student.getStudentName() + "]退出登录", new Date());
        try {
            runLogMapper.insert(runLog);
        } catch (Exception e) {
            logger.error("记录学生 [{}]->[{}] 退出登录信息失败！", student.getId(), student.getStudentName(), e);
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
            if (loginTime != null && loginOutTime != null) {
                // 判断当前登录时间是否已经记录有在线时长信息，如果没有插入记录，如果有无操作
                int count = durationMapper.countOnlineTimeWithLoginTime(student, loginTime);
                if (count == 0) {
                    // 学生 session 失效时将该学生从在线人数中移除
                    redisTemplate.opsForSet().remove(RedisKeysConst.ONLINE_USER, student.getId());
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
        map.put("partUrl",student.getPartUrl());
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

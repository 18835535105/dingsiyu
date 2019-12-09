package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentUnitMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class LoginServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements LoginService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

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
    private SysUserMapper sysUserMapper;

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
    private TeacherMapper teacherMapper;

    @Autowired
    private RedisOpt redisOpt;

    /**
     * @param password    新密码
     * @param session
     * @param oldPassword 旧密码
     * @param studentId   对比 学生id
     * @return
     */
    @Override
    public ServerResponse<String> updatePassword(String password, HttpSession session, String oldPassword, Long studentId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (!Objects.equals(student.getId(), studentId)) {
            log.error("学生 {}->{} 试图修改学生 {} 的密码！", student.getId(), student.getStudentName(), studentId);
            return ServerResponse.createByErrorMessage("无权限修改他人密码！");
        }
        if (!Objects.equals(student.getPassword(), oldPassword)) {
            return ServerResponse.createByErrorMessage("原密码输入错误！");
        }
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

        // 判断学生是否需要进行游戏测试
        int gameCount = testRecordMapper.countGameCount(student);
        if (gameCount == 0) {
            // 第一次进行游戏测试
            result.put("game", true);
        }

        // 判断学生是否有智能版课程
        int count = studentStudyPlanMapper.countByStudentIdAndType(studentId, 1);
        if (count == 0) {
            result.put("hasCapacity", false);
        } else {
            result.put("hasCapacity", true);
        }

        // 获取学生当前正在学习的单元信息
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(studentId, 1);
        if (capacityStudentUnit != null) {
            // 封装学生当前学习的课程信息
            this.packageUnitInfo(result, capacityStudentUnit);
            // 1.学生当前单词模块学的那个单元
            Integer unitId = Integer.valueOf(capacityStudentUnit.getUnitId().toString());
            // 封装各个学习模块的学习状况信息
            this.packageModelInfo(studentId, result, unitId);
        }

        // 判断学生是否已经学完教师分配的所有计划，如果已学完所有计划，开始之旅按钮将被替换并且不能被点击
        if (isLearnedAllPlan(studentId)) {
            result.put("learnedAllPlan", true);
        }

        // 封装学生相关信息
        this.packageStudentInfo(student, result);

        // 封装时长信息
        this.getIndexTime(session, student, result);

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
            if (i == 0) {
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

                map.put("sum", Math.min(sum, countWord));
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
        result.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        // 宠物
        result.put("partUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
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
            if (flow != null) {
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
        result.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        result.put("schoolName", student.getSchoolName());

        this.getIndexTime(session, student, result);

        return ServerResponse.createBySuccess(result);
    }

    private void getIndexTime(HttpSession session, Student student, Map<String, Object> result) {
        // 有效时长
        Integer valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长
        Integer online = (int) DurationUtil.getTodayOnlineTime(session);
        // 今日学习效率
        if (valid >= online) {
            logger.warn("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            valid = online - 1;
            result.put("efficiency", "99%");
        } else {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        }
        result.put("online", online);
        result.put("valid", valid);
    }

    @Override
    public ServerResponse<Object> clickPortrait(HttpSession session, Integer type) {

        Student student = getStudent(session);
        Long studentId = student.getId();

        Map<String, Object> map = new HashMap<>(16);
        map.put("sex", student.getSex());
        if (type == 1) {
            // 获取今日已学单词
            int learnWord = learnMapper.getTodayWord(DateUtil.formatYYYYMMDD(new Date()), studentId);
            map.put("learnWord", learnWord);
        } else if (type == 2) {
            // 获取今日已学例句
            int learnSentence = learnMapper.getTodaySentence(DateUtil.formatYYYYMMDD(new Date()), studentId);
            map.put("learnSentence", learnSentence);
        } else if (type == 3) {
            //获取今日已学课文数
            int learnTeks = learnMapper.getTodyTeks(DateUtil.formatYYYYMMDD(new Date()), studentId);
            map.put("learnTeks", learnTeks);
        } else if (type == 4) {
            // 获取今日学习字母
            int letterCount = learnMapper.countTodayLearnedLetter(studentId);
            map.put("letter", letterCount);

            // 今日已学音标个数
            int phoneticSymbolCount = learnMapper.countTodayLearnedPhoneticSymbol(studentId);
            map.put("phoneticSymbol", phoneticSymbolCount);
        } else if (type == 7) {
            //获取今日学习语法数
            int syntaxCount = learnMapper.countSyntax(studentId);
            map.put("syntaxCount", syntaxCount);
        }

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

    public static void main(String[] args) {
        Long l1 = 1L;
        Integer i1 = 1;
        Long l2 = 1L;
//        System.out.println(l1.equals(i1));
//        System.out.println(l1.equals(l2));
    }

    @Override
    public void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ValidateCode vCode = new ValidateCode(74, 31, 4, 150);
        session.removeAttribute("validateCode");
        vCode.write(response.getOutputStream());
        session.setAttribute("validateCode", vCode.getCode());
        vCode.write(response.getOutputStream());
    }

    @Override
    public Object getRiepCount(HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>();
        map.put("partUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        Integer count = studentMapper.getVocabularyCountByStudent(student.getId());
        map.put("vocabularyCount", count);
        Integer sentenceCount = studentMapper.getSentenceCountByStudent(student.getId());
        map.put("sentenceCount", sentenceCount);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public Object getModelStatus(HttpSession session, Integer type) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>();
        boolean isHave = false;
        //判断是否有智慧单词课程
        if (type.equals(1)) {
            CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selByStudentIdAndType(student.getId(), type);
            if (capacityStudentUnit != null) {
                isHave = true;
            }
        }
        //判断是否有句型，课文课程
        if (type.equals(2) || type.equals(3)) {
            int count = studentStudyPlanMapper.countByStudentId(student.getId(), type);
            if (count > 0) {
                isHave = true;
            }
        }
        //判断是否有字母课程
        if (type.equals(4)) {
            StudentStudyPlan letterPlan = studentStudyPlanMapper.selSymbolByStudentId(student.getId());
            StudentStudyPlan symbolPlan = studentStudyPlanMapper.selLetterByStudentId(student.getId());
            if (letterPlan != null || symbolPlan != null) {
                isHave = true;
            }
        }
        //判断是否有绝招好课课程
        if (type.equals(5)) {
            int count = simpleStudentUnitMapper.countAllUnlockByStudentId(student.getId());
            if (count > 0) {
                isHave = true;
            } else {
                isHave = false;
            }

        }
        //判断是否有阅读课程
        if (type.equals(6)) {
            StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selReadByStudentId(student.getId());
            if (studentStudyPlan != null) {
                isHave = true;
            }
        }
        //判断是否有语法课程
        if (type.equals(7)) {
            if (student.getSchoolName().contains("英语队长涿鹿鼓楼小学体验中心") ||
                    student.getAccount().contains("2035") ||
                    student.getAccount().contains("3557") ||
                    student.getAccount().contains("3586")) {
                int i = studentStudyPlanMapper.countByStudentId(student.getId(), type);
                if (i > 0) {
                    isHave = true;
                }
            }

        }
        map.put("isHave", isHave);
        return ServerResponse.createBySuccess(map);
    }


}

package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.SaltConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.ValidateCode;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.personal.InitRedPointThread;
import com.zhidejiaoyu.student.service.LoginService;
import com.zhidejiaoyu.student.utils.CountMyGoldUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private AwardMapper awardMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private CapacityReviewMapper capacityMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudentWorkDayMapper studentWorkDayMapper;

    @Autowired
    private CalendarMapper calendarMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudyFlowMapper studyFlowMapper;

    @Resource
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private MedalMapper medalMapper;

    @Autowired
    private CountMyGoldUtil countMyGoldUtil;

    @Autowired
    private InitRedPointThread initRedPointThread;
    
    @Autowired
    private StudentFlowMapper studentFlowMapper;

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

        // 学生id
        Long student_id = StudentIdBySession(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 判断个人中心是否显示红点
        initRedPointThread.redPoint(getStudent(session), result);

        Student stu = studentMapper.indexData(student_id);

        //Integer role = studentMapper.judgeUser(Long.valueOf(student_id));
        // 业务员
        if (stu.getRole() !=null && stu.getRole() == 2) {
            result.put("role", "2");
            // 学生
        } else {
            result.put("role", "1");
        }

        // 学生id
        result.put("student_id", stu.getId());
        // 当前单词所学课程id
        result.put("course_id", stu.getCourseId());
        // 当前单词所学课程名
        result.put("course_name", stu.getCourseName());
        // 当前单词所学单元id
        result.put("unit_id", stu.getUnitId());
        // 根据单元id查询单元名 - 需要根据学生单元id实时去查询单元名
        result.put("unit_name", unitMapper.getUnitNameByUnitId(stu.getUnitId()));
        // 账号
        result.put("account", stu.getAccount());
        // 姓名
        result.put("studentName", stu.getStudentName());
        // 头像
        result.put("headUrl", stu.getHeadUrl());
        // 宠物
        result.put("partUrl", stu.getPartUrl());
        // 宠物名
        result.put("petName", stu.getPetName());

        String formatYYYYMMDD = DateUtil.formatYYYYMMDD(new Date());
        // 有效时长  !
        Integer valid = getValidTime(student_id, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 24:00:00");
        // 在线时长 !
        Integer online = getOnLineTime(session, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 24:00:00");
        result.put("online", LearnTimeUtil.valid_onlineTime(online));
        result.put("valid", LearnTimeUtil.valid_onlineTime(valid));
        // 今日学习效率 !
        if (valid != null && online != null) {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        //-- 1.学生当前单词模块学的那个单元
        Integer unit_id = stu.getUnitId() == null ? 0 : stu.getUnitId().intValue();

        // 查询是否需要阶段测试-隐藏单词学习模块
        TestRecordExample testRecordExample = new TestRecordExample();
        testRecordExample.createCriteria().andStudentIdEqualTo(student_id).andGenreEqualTo("阶段测试")
                .andCourseIdEqualTo(stu.getCourseId());
        /*int testCount = 30;
        // 查询所学课程已学多少单词
        int count = learnMapper.countLearnWordByStudentIdAndCourseIdAndStudyModel(student_id, stu.getCourseId(), 1);
        if(testCount == count) {
            // 强制去阶段
        	result.put("stage", true);
        	// 宠物图片
            // result.put("partWGUrl", "../../static/img/edit-user-msg/tips1-6.png");
        } else {
            result.put("stage", false);
        }*/

        // 单词图鉴模块, 单元下共有多少带图片的单词
        Long countWord = null;

        for (int i = 0; i < 7; i++) {
            Map<String, Object> a = new HashMap<String, Object>();
            //-- 如果 2 = NULL 就跳过4步执行5步   condition = 3(方框为空)
            //-- 如果 2 != NULL 执行4步跳过第5步 , 如果第2步>=80 condition = 1(方框为√), 如果第3步<80 condition = 2(方框为×)

            if (unit_id == null) {
                return ServerResponse.createBySuccess(result);
            }

            if(i == 0){
                // 单词图鉴单元总单词数
                countWord = unitVocabularyMapper.selectWordPicCountByUnitId(unit_id);
            }else if(i == 1){
                // 单元下一共有多少单词
                countWord = unitVocabularyMapper.selectWordCountByUnitId((long) unit_id);
            }

            //-- 2.某学生某单元某模块单元闯关测试得了多少分max
            Integer point = testRecordMapper.selectPoint(student_id, unit_id, "单元闯关测试", i);

            //-- 3.某学生某单元某模块单词 学了多少 ./
            Integer sum = learnMapper.selectNumberByStudentId(student_id, unit_id, i);

            if(countWord.equals(sum)){
                a.put("state", true);
            }else{
                a.put("state", false);
            }

            //-- 4.某学生某单元某模块学习速度;  
            //select SUM(valid_time) from duration where unit_id = 1 and student_id = 1 and study_model = '1'
            Integer sumValid = durationMapper.valid_timeIndex(student_id, unit_id, i);
            if (sumValid == null) {
            	sumValid = 0;
            }
            Integer speed = (int) (BigDecimalUtil.div(sum, sumValid)*3600);

            // 封装返回结果
            if (point != null && sum != null) { // 已做单元闯关
                a.put("point", point + ""); // 分数
                a.put("speed", speed + ""); // 速度
                if (point >= 80) {       // 方框状态
                    a.put("condition", 1);
                } else {
                    a.put("condition", 2);
                }
                a.put("sum", ""); // ./
                a.put("countWord", ""); // /.
            } else { // 未做单元闯关

                a.put("sum", sum + ""); // ./
                a.put("countWord", countWord + ""); // /.
                a.put("condition", 3); // 方框状态
                a.put("point", ""); // 分数
                a.put("speed", speed + ""); // 速度
            }
            result.put(i + "", a);


        }

        // 封装返回的数据 - 智能记忆智能复习数量
        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = s.format(new Date());

        CapacityReview cr = new CapacityReview();
        cr.setUnit_id(Long.valueOf(unit_id));
        cr.setStudent_id(Long.valueOf(student_id));
        cr.setPush(datetime);
        // 慧记忆模块需复习量
        cr.setClassify("1");
        Integer a = capacityMapper.countCapacity_memory(cr);
        // 慧听写模块需复习量
        cr.setClassify("2");
        Integer b = capacityMapper.countCapacity_memory(cr);
        // 慧默写模块需复习量
        cr.setClassify("3");
        Integer c = capacityMapper.countCapacity_memory(cr);
        // 单词图鉴需复习量
        cr.setClassify("0");
        Integer d = capacityMapper.countCapacity_memory(cr);
        result.put("amount1", a);
        result.put("amount2", b);
        result.put("amount3", c);
        result.put("amount0", d);
        // 听力模块许复习量
        cr.setClassify("4");
        Integer e = capacityMapper.countCapacity_memory(cr);
        // 翻译模块许复习量
        cr.setClassify("5");
        Integer f = capacityMapper.countCapacity_memory(cr);
        // 默写模块许复习量
        cr.setClassify("6");
        Integer g = capacityMapper.countCapacity_memory(cr);
        result.put("amount4", e);
        result.put("amount5", f);
        result.put("amount6", g);
        // 是否需要隐藏学习模块
        if (a >= 20 || b >= 10 || c >= 10 || d >=10) {
            result.put("hide", true);
         // 温故宠物图片
            // result.put("partWGUrl", "../../static/img/edit-user-msg/tips1-5.png");
        } else {
            result.put("hide", false);
        }

        // 获取智能化节点数据
        StudyFlow flow = studyFlowMapper.getFlowInfoByStudentId(student_id);
        if(flow!=null){
            result.put("nodeId", flow.getId());
            result.put("nodeName", flow.getFlowName());
        }
        
        // 学生当前节点模块名
        result.put("flowName", studentFlowMapper.getStudentFlow(stu.getId()));
        
        return ServerResponse.createBySuccess(result);
    }

    /**
     * 例句首页数据
     */
    @Override
    public ServerResponse<Object> sentenceIndex(HttpSession session) {

        // 学生id
        Long student_id = StudentIdBySession(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<String, Object>(16);

        // 判断个人中心按钮是否需要红点提示
        initRedPointThread.redPoint(getStudent(session), result);

        Student stu = studentMapper.indexData(student_id);

        Integer role = studentMapper.judgeUser(Long.valueOf(student_id));
        // 业务员
        if (role == 2) {
            result.put("role", "2");
            // 学生
        } else {
            result.put("role", "1");
        }

        // 学生id
        result.put("student_id", stu.getId());
        // 当前例句所学课程id
        result.put("course_id", stu.getSentenceCourseId());
        // 当前例句所学课程名
        result.put("course_name", stu.getSentenceCourseName());
        // 当前例句所学单元id
        result.put("unit_id", stu.getSentenceUnitId());
        // 当前例句所学单元名
        // result.put("unit_name", stu.getSentenceUnitName());
        // 根据单元id查询单元名
        result.put("unit_name", unitMapper.getUnitNameByUnitId(stu.getSentenceUnitId().longValue()));
        // 账号
        result.put("account", stu.getAccount());
        // 姓名
        result.put("studentName", stu.getStudentName());
        // 头像
        result.put("headUrl", stu.getHeadUrl());

        String formatYYYYMMDD = DateUtil.formatYYYYMMDD(new Date());
        // 有效时长  !
        Integer valid = getValidTime(student_id, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 24:00:00");
        // 在线时长 !
        Integer online = getOnLineTime(session, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 24:00:00");
        result.put("online", LearnTimeUtil.valid_onlineTime(online));
        result.put("valid", LearnTimeUtil.valid_onlineTime(valid));
        // 今日学习效率 !
        if (valid != null && online != null) {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        //-- 1.查询学生当前单词模块学的那个单元
        Integer unit_id = stu.getSentenceUnitId();

        // 查看黄金记忆点是否需要隐藏单词学习模块
        // 当前时间
        //String dateTime = DateUtil.DateTime();
        // 1.例句
        //Integer count_a = capacityMemoryMapper.selectCountPush(student_id, unit_id, dateTime, 4);
        // 2.例句
        //Integer count_b = capacityMemoryMapper.selectCountPush(student_id, unit_id, dateTime, 5);
        // 3.例句
        //Integer count_c = capacityMemoryMapper.selectCountPush(student_id, unit_id, dateTime, 6);
        // 是否需要隐藏学习模块
        //if (count_a >= 10 || count_b >= 10 || count_c >= 10) {
        //    result.put("hide", true);
            // 宠物图片
        //    result.put("partWGUrl", "../../static/img/edit-user-msg/tips1-6.png");
        //} else {
        //    result.put("hide", false);
        //}

        // 一共有多少例句/.
        Long countWord = unitSentenceMapper.selectSentenceCountByUnitId((long) unit_id);

        for (int i = 4; i < 7; i++) {
            Map<String, Object> a = new HashMap<String, Object>();
            //-- 如果 2 = NULL 就跳过4步执行5步   condition = 3(方框为空)
            //-- 如果 2 != NULL 执行4步跳过第5步 , 如果第2步>=80 condition = 1(方框为√), 如果第3步<80 condition = 2(方框为×)

            if (unit_id == null) {
                return ServerResponse.createBySuccess(result);
            }

            //-- 2.某学生某单元某模块得了多少分
            //select point from test_record where student_id = #{} and unit_id = #{} and genre = '单元闯关测试' and study_model = '慧记忆'
            Integer point = testRecordMapper.selectPoint(student_id, unit_id, "单元闯关测试", i);

            //-- 3.某学生某单元某模块单词学了多少 ./
            //select COUNT(id) from learn where student_id = #{} and unit_id = #{} and study_model = '慧记忆' GROUP BY vocabulary_id
            Integer sum = learnMapper.selectNumberByStudentId(student_id, unit_id, i);

            if (point != null && sum != null) {
                //-- 4.某学生某单元某模块学习速度;  单词已学个数/(有效时长m/3600)
                //select SUM(valid_time) from duration where unit_id = 1 and student_id = 1 and study_model = '慧记忆'
                Integer sumValid = durationMapper.valid_timeIndex(student_id, unit_id, i);

                Integer speed = sumValid == null ? 0 : (int) (BigDecimalUtil.div(sum, sumValid)*3600);
                a.put("point", point + ""); // 分数
                a.put("speed", speed + ""); // 速度
                if (point >= 80) {       // 方框状态
                    a.put("condition", 1);
                } else {
                    a.put("condition", 2);
                }
                a.put("sum", ""); // ./
                a.put("countWord", ""); // /.
            } else {
                a.put("sum", sum + ""); // ./
                a.put("countWord", countWord + ""); // /.
                a.put("condition", 3); // 方框状态
                a.put("point", ""); // 分数
                // 计算学习速度
                Integer sumValid = durationMapper.valid_timeIndex(student_id, unit_id, i);
                Integer speed = (int) (BigDecimalUtil.div(sum, sumValid == null ? 0 : sumValid) * 3600);
                a.put("speed", speed); // 速度
            }

            result.put(i + "", a);
        }

        // 封装返回的数据 - 智能记忆智能复习数量
        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = s.format(new Date());

        CapacityReview cr = new CapacityReview();
        cr.setUnit_id(Long.valueOf(unit_id));
        cr.setStudent_id(student_id);
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

        Map<String, Object> map = new HashMap<String, Object>();
        long studentId = StudentIdBySession(session);

        // 获取今日已学单词
        int learnWord = learnMapper.getTodayWord(DateUtil.formatYYYYMMDD(new Date()), studentId);
        // 获取今日已学例句
        int learnSentence = learnMapper.getTodaySentence(DateUtil.formatYYYYMMDD(new Date()), studentId);
        map.put("learnWord", learnWord);
        map.put("learnSentence", learnSentence);

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
        int count = 0;
        String regex = "#(.*)#";
        Pattern pattern = Pattern.compile(regex);
        for (String str : list) {
            Matcher matcher = pattern.matcher(str);//匹配类
            while (matcher.find()) {
                count += Integer.parseInt(matcher.group(1));
            }
        }
        map.put("myThisGold", count);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse LoginJudge(String account, String password, HttpSession session, String code) {
        /*Object verifyCode = session.getAttribute("validateCode");
        if(verifyCode == null || !verifyCode.equals(code)){
            return ServerResponse.createByErrorMessage("验证码错误");
        }*/

        removeSessionAttribute(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 根据传入的账号密码, 查询个人信息, 加密查询
        Md5Hash md5 = new Md5Hash(password, SaltConstant.SALT);
        Student stu = LoginJudge(account, md5.toString());
        // 没查到不加密查询
        if(stu == null){
            stu = LoginJudge(account, password);
        }

        // 1.账号/密码错误
        if (stu == null) {
            return ServerResponse.createByErrorMessage("账号或密码输入错误");
            // 2.账号已关闭
        }else if(stu.getStatus() != null && stu.getStatus()==2) {
        	return ServerResponse.createByErrorMessage("此账号已关闭");

        } else if (stu.getStatus() != null && stu.getStatus() == 3) {
            // 账号被删除
            return ServerResponse.createByErrorMessage("此账号已被删除");
            // 3.正确
        } else {

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
            // 判断用户是学生还是业务员
            Integer role = stu.getRole();
            if (role == null || role == 1) {
                // 学生
                result.put("role", "1");
            } else {
                // 业务员
                result.put("role", "2");
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
            saveLoginRunLog(stu);

            // 当日首次登陆奖励5金币（日奖励）
            saveDailyAward(stu);

            // 当前用户信息放到session
            session.setAttribute(UserConstant.CURRENT_STUDENT, stu);
            // 登陆时间放入session
            session.setAttribute(TimeConstant.LOGIN_TIME, DateUtil.parseYYYYMMDDHHMMSS(new Date()));

            // 2.判断是否需要完善个人信息
            if (!StringUtils.isNotBlank(stu.getHeadUrl())) {

                // 学校
                result.put("school_name", stu.getSchoolName());
                // 到期时间
                result.put("account_time", stu.getAccountTime());

                // 学生首次登陆系统，为其初始化7个工作日,以便判断其二级标签
                initStudentWorkDay(stu);

                // 2.1 跳到完善个人信息页面
                return ServerResponse.createBySuccess("2", result);
            }

            // 判断是否需要学前测试
            // 判断学生是否已经有推送的课程
            // 有推送课程，不需要学前测试
            int courseCount = studentUnitMapper.countUnitCountByStudentId(stu, commonMethod.getPhase(stu.getGrade()));
            // 没有推送课程，判断是否进行游戏测试
            if (courseCount == 0) {
                TestRecordExample testRecordExample = new TestRecordExample();
                testRecordExample.createCriteria().andStudentIdEqualTo(stu.getId()).andGenreEqualTo("学前游戏测试");
                List<TestRecord> testRecords = testRecordMapper.selectByExample(testRecordExample);
                int size = testRecords.size();
                if (size == 0) {
                    // 第一次进行游戏测试
                    return ServerResponse.createBySuccess("3", result);
                }
                
                // 第二次登陆
                // 游戏测试次数
                int count = Integer.parseInt(testRecords.get(0).getExplain().split("#")[1]);
                if (count == 1) {
                    // 进行游戏测试
                    return ServerResponse.createBySuccess("5", result);
                } else{
                    // 接进入摸底测试
                    return ServerResponse.createBySuccess("4", result);
                }

            }

            // 一个账户只能登陆一台
            redisTemplate.opsForHash().put("loginSession", stu.getId(), session.getId());

            countMyGoldUtil.countMyGold(stu);

            // 正常登陆
            return ServerResponse.createBySuccess("1", result);
        }
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
            stu.setAccountTime(new Date(System.currentTimeMillis() + stu.getRank() * 24 * 60 * 60 * 1000));

            // 初始化学生勋章信息
            initMedalInfo(stuId, stu);
        }
    }

    private void initMedalInfo(Long stuId, Student stu) {
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
        List<Long> ids = medalMapper.selectAllIdsByParentIds(parentIds);
        List<Award> awards = new ArrayList<>(ids.size());
        ids.forEach(id -> {
            Award award = new Award();
            award.setCanGet(2);
            award.setGetFlag(2);
            award.setType(3);
            award.setMedalType(id);
            award.setStudentId(stuId);
            awards.add(award);
        });

        try {
            awardMapper.insertList(awards);
        } catch (Exception e) {
            logger.error("初始化学生 [{}]->[{}] 勋章信息失败", stuId, stu.getStudentName(), e);
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
        StringBuilder sb;
        if (count == 1) {
            stu.setSystemGold(BigDecimalUtil.add(stu.getSystemGold(), 5));
            Award award = new Award();
            award.setCanGet(1);
            award.setGetFlag(1);
            award.setStudentId(stu.getId());
            award.setType(1);
            award.setAwardContentType(1);
            award.setCreateTime(new Date());
            award.setGetTime(new Date());
            try {
                awardMapper.insert(award);
                studentMapper.updateByPrimaryKeySelective(stu);
                sb = new StringBuilder("学生").append(stu.getStudentName()).append("今日首次登陆系统，奖励#5#金币");
                RunLog runLog = new RunLog(stu.getId(), 4, sb.toString(), new Date());
                runLogMapper.insert(runLog);
            } catch (Exception e) {
                logger.error("保存学生 {} -> {} 5个金币奖励信息失败！", stu.getId(), stu.getStudentName(), e);
            }
        }
    }

    private void saveLoginRunLog(Student stu) {
        RunLog runLog = new RunLog(stu.getId(), 1, "学生 " + stu.getStudentName() + " 登录", new Date());
        try {
            runLogMapper.insert(runLog);
        } catch (Exception e) {
            logger.error("学生 {} -> {} 登录信息记录失败！ExceptionMsg:{}", stu.getId(), stu.getStudentName(), e.getMessage(), e);
        }
    }

    /**
     * 判断当前用户是否已登录，防止用户正常登录期间通过url再次登录导致更新时长表信息出错问题
     * <ul>
     * <li>如果已登录，又重新登录，清除上次登录信息，并更新时长信息</li>
     * <li>如果是新登录，不做操作</li>
     * </ul>
     *
     * @param session
     */
    private void removeSessionAttribute(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (student != null) {
            saveDuration(session);
            Enumeration<String> attributeNames = session.getAttributeNames();
            StringBuilder sb = new StringBuilder();
            while (attributeNames.hasMoreElements()) {
                sb.append(attributeNames.nextElement()).append("@@");
            }
            if (sb.length() > 0) {
                String[] attrs = sb.toString().split("@@");
                for (String attr : attrs) {
                    session.removeAttribute(attr);
                }
            }
        }
    }

    @Override
    public void loginOut(HttpSession session) {
        session.invalidate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDuration(HttpSession session) {

        if (session.getAttribute(TimeConstant.LOGIN_TIME) == null) {
            return;
        }

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        saveLogoutInfo(student);

        Map<Integer, Duration> map = (Map<Integer, Duration>) session.getAttribute(TimeConstant.TOTAL_VALID_TIME);
        Date loginTime = DateUtil.parseYYYYMMDDHHMMSS((Date) session.getAttribute(TimeConstant.LOGIN_TIME));

        saveDurationInfo(student, map, loginTime);
    }

    /**
     * 记录学生退出信息
     *
     * @param student
     */
    private void saveLogoutInfo(Student student) {
        RunLog runLog = new RunLog(student.getId(), 1, "学生" + student.getStudentName() + "退出登录", new Date());
        try {
            runLogMapper.insert(runLog);
        } catch (Exception e) {
            logger.error("记录学生 {}->{} 退出登录信息失败！", student.getId(), student.getStudentName(), e);
        }
    }

    @Override
    public void saveDurationInfo(Student student, Map<Integer, Duration> map, Date loginTime) {
        Date loginOutTime = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        Assert.notNull(loginOutTime, "loginOutTime is null");
        Long onlineTime = (loginOutTime.getTime() - loginTime.getTime()) / 1000;

        if (map != null) {
            // 在线时长只保留一个即可，防止重复累计
            final int[] i = {0};

            map.forEach((key, value) -> {
                value.setLoginOutTime(loginOutTime);
                if (i[0] == 0) {
                    value.setOnlineTime(onlineTime);
                } else {
                    value.setOnlineTime(0L);
                }
                i[0] = i[0]++;
                durationMapper.updateByPrimaryKeySelective(value);
            });
        } else {
            Duration duration = new Duration();
            duration.setStudentId(student.getId());
            duration.setOnlineTime(onlineTime);
            duration.setLoginTime(loginTime);
            duration.setLoginOutTime(loginOutTime);
            duration.setValidTime(0L);
            durationMapper.insert(duration);
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


    @Test
    public void add() {
        int count = 0;
        String regex = "#(.*)#";
        Pattern pattern = Pattern.compile(regex);
        List<String> list = new ArrayList<String>();
        list.add("id为 3155 的学生在 2018-05-24 11:17:22 当日学习两个单元，奖励#20#枚金币");
        list.add("id为 3155 的学生在 2018-05-24 11:17:22 当日学习两个单元，奖励#20#枚金币");
        list.add("id为 3155 的学生在 2018-05-24 11:17:22 当日学习两个单元，奖励#2#枚金币");
        for (String str : list) {
            Matcher matcher = pattern.matcher(str);//匹配类
            while (matcher.find()) {
                count += Integer.parseInt(matcher.group(1));
                //System.out.println(matcher.group(1));//打印中间字符
            }
        }
        System.out.println(count);
    }

}

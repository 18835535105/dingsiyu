package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.MacIpUtil;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.SimpleValidateCode;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleLearnTimeUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimpleLoginServiceSimple;
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
public class SimpleLoginServiceImplSimple extends SimpleBaseServiceImpl<SimpleStudentMapper, Student> implements SimpleLoginServiceSimple {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleDurationMapper simpleDurationMapper;

    @Autowired
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

	@Autowired
    private SimpleUnitVocabularyMapper simpleUnitVocabularyMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private DailyAwardAsync awardAsync;

    @Autowired
    private SimpleCapacityReviewMapper capacityMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleCapacityStudentUnitMapper simpleCapacityStudentUnitMapper;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Override
    public Student LoginJudge(String account, String password) {
        Student st = new Student();
        st.setAccount(account);
        st.setPassword(password);
        return simpleStudentMapper.LoginJudge(st);
    }


    @Override
    public Integer judgeUser(Long id) {
        return simpleStudentMapper.judgeUser(id);
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> updatePassword(String oldPassword, String password, HttpSession session) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();
        String account = student.getAccount();

        Integer state = simpleStudentMapper.updatePassword(account, password, studentId);
        if (state == 1) {
            student.setPassword(password);

            Award award = simpleAwardMapper.selectByAwardContentTypeAndType(studentId, 2, 12);
            if (award == null || award.getCanGet() == 2) {
                // 首次修改密码
                student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), 10));
                RunLog runLog = new RunLog(student.getId(), 4, "学生首次修改密码，奖励#10#金币", new Date());
                runLogMapper.insert(runLog);

                // 首次修改密码奖励
                goldAwardAsync.dailyAward(student, 12);
            }
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
        Long studentId = StudentIdBySession(session);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        Student stu = simpleStudentMapper.indexData(studentId);

        // 学生id
        result.put("student_id", stu.getId());
        // 课程包
        result.put("coursePackage", "课程中心");
        // 账号
        result.put("account", stu.getAccount());
        // 昵称
        result.put("studentName", stu.getStudentName());
        // 头像
        result.put("headUrl", stu.getHeadUrl());
        // 宠物
        result.put("partUrl", stu.getPartUrl());
        // 宠物名
        result.put("petName", stu.getPetName());
        result.put("schoolName", stu.getSchoolName());
        //性别
        if(1==stu.getSex()){
            result.put("sex","男");
        }else{
            result.put("sex","女");
        }

        // 判断学生是否有智能版单词
        int count = simpleCapacityStudentUnitMapper.countByType(stu, 1);
        result.put("hasCapacityWord", count > 0);

        String formatYYYYMMDD = SimpleDateUtil.formatYYYYMMDD(new Date());
        // 有效时长  !
        Integer valid = getValidTime(studentId, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
        // 在线时长 !
        Integer online = getOnLineTime(session, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
        // 今日学习效率 !
        if (valid != null && online != null) {
            if (valid >= online) {
                logger.error("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, stu);
            }
            String efficiency = SimpleLearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }
        result.put("online", online);
        result.put("valid", valid);

        // 所有模块正在学习的课程id
        Map<Integer, Map<String, Long>> allCourse = simpleSimpleStudentUnitMapper.getAllUnit(stu.getId());
        // 对应模块的课程id
        int courseId = 0;

        for (int i = 1; i <= 9; i++) {

            Map<String, Object> resultMap = new HashMap<>(16);
            // 当前模块未开启
            resultMap.put("open", false);

            // 1.获取模块对应在学的单元id
            if(allCourse.containsKey(i)) {
            	courseId = allCourse.get(i).get("course_id") == null ? 0 : allCourse.get(i).get("course_id").intValue();
                // 当前模块已开启
            	resultMap.put("open", true);
            }

            // 课程下一共有多少单词 /.
            int countWord = redisOpt.wordCountInCourse((long) courseId);

            // 课程已学 ./
            Integer sum = learnMapper.selectCourseWordNumberByStudentId(studentId, courseId, i);

            //-- 4.某课程某模块学习速度;
            Integer sumValid = simpleDurationMapper.valid_timeIndex(studentId, courseId, i+13);
            if (sumValid == null) {
            	sumValid = 0;
            }
            int speed = (int) (BigDecimalUtil.div(sum, sumValid)*3600);

            // 继续学习状态
            resultMap.put("state", 2);
            resultMap.put("sum", sum + "");
            resultMap.put("countWord", countWord + "");
            // 速度
            resultMap.put("speed", speed + "");

            // 3.开始学习状态
            if(sum == 0) {
            	// learn表是否有数据
            	Integer status = learnMapper.getModelLearnInfo(studentId, testModelStr(i));
            	if(status == null) {
                    // 开始学习
            		resultMap.put("state", 1);
            	}
            }
            courseId = 0;
            result.put(i + "", resultMap);
        }

        // 值得元老勋章
        medalAwardAsync.oldMan(stu);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 需要测试的模块
     *
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     */
    private boolean testModel(int type) {
    	if(type == 1 || type == 2 || type == 3 || type == 4 || type == 6 || type == 8 || type == 9) {
    		return true;
    	}else {
    		return false;
    	}
	}

    private String testModelStr(int type) {
    	if(type == 1) {
    		return "单词辨音";
    	}else if(type == 2) {
    		return "词组辨音";
    	}else if(type == 3) {
    		return "快速单词";
    	}else if(type == 4) {
    		return "快速词组";
    	}else if(type == 5) {
    		return "词汇考点";
    	}else if(type == 6) {
    		return "快速句型";
    	}else if(type == 7) {
    		return "语法辨析";
    	}else if(type == 8) {
    		return "单词默写";
    	}else {
    		return "词组默写";
    	}
    }

    /**
     * 例句首页数据
     */
    @Override
    public ServerResponse<Object> sentenceIndex(HttpSession session) {

        // 学生id
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        Integer role = student.getRole();
        // 业务员
        if (role == 2) {
            result.put("role", "2");
            // 学生
        } else {
            result.put("role", "1");
        }

        // 学生id
        result.put("student_id", studentId);
        // 当前例句所学课程id
        result.put("course_id", student.getSentenceCourseId());
        // 当前例句所学课程名
        result.put("course_name", student.getSentenceCourseName());
        // 当前例句所学单元id
        result.put("unit_id", student.getSentenceUnitId());
        // 当前例句所学单元名
        // result.put("unit_name", stu.getSentenceUnitName());
        // 根据单元id查询单元名
        result.put("unit_name", unitMapper.getUnitNameByUnitId(student.getSentenceUnitId().longValue()));
        // 账号
        result.put("account", student.getAccount());
        // 姓名
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", student.getHeadUrl());

        // 有效时长  !
        Integer valid = super.getTodayValidTime(studentId);
        // 在线时长 !
        Integer online = super.getTodayOnlineTime(session);
        result.put("online", SimpleLearnTimeUtil.validOnlineTime(online));
        result.put("valid", SimpleLearnTimeUtil.validOnlineTime(valid));
        // 今日学习效率 !
        if (valid != null && online != null) {
            String efficiency = SimpleLearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        } else {
            result.put("efficiency", "0%");
        }

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        //-- 1.查询学生当前单词模块学的那个单元
        Integer unitId = student.getSentenceUnitId();

        // 一共有多少单词/.
        Long countWord = simpleUnitVocabularyMapper.selectWordCountByUnitId((long) unitId);

        for (int i = 4; i < 7; i++) {
            Map<String, Object> a = new HashMap<String, Object>();
            //-- 如果 2 = NULL 就跳过4步执行5步   condition = 3(方框为空)
            //-- 如果 2 != NULL 执行4步跳过第5步 , 如果第2步>=80 condition = 1(方框为√), 如果第3步<80 condition = 2(方框为×)

            if (unitId == null) {
                return ServerResponse.createBySuccess(result);
            }

            //-- 2.某学生某单元某模块得了多少分
            //select point from test_record where student_id = #{} and unit_id = #{} and genre = '单元闯关测试' and study_model = '慧记忆'
            Integer point = simpleTestRecordMapper.selectPoint(studentId, unitId, "单元闯关测试", i);

            //-- 3.某学生某单元某模块单词学了多少 ./
            //select COUNT(id) from learn where student_id = #{} and unit_id = #{} and study_model = '慧记忆' GROUP BY vocabulary_id
            Integer sum = learnMapper.selectNumberByStudentId(studentId, unitId, i);

            if (point != null && sum != null) {
                //-- 4.某学生某单元某模块学习速度;  单词已学个数/(有效时长m/3600)
                //select SUM(valid_time) from duration where unit_id = 1 and student_id = 1 and study_model = '慧记忆'
                Integer sumValid = simpleDurationMapper.valid_timeIndex(studentId, unitId, i);

                Integer speed = (int) (BigDecimalUtil.div(sum, sumValid)*3600);
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
                Integer sumValid = simpleDurationMapper.valid_timeIndex(studentId, unitId, i);
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
        cr.setUnit_id(Long.valueOf(unitId));
        cr.setStudent_id(Long.valueOf(studentId));
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
            result.put("partWGUrl", "static/img/edit-user-msg/tips1-6.png");
        } else {
            result.put("hide", false);
        }

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public Integer validTime(HttpSession session) {
        long student_id = StudentIdBySession(session);
        return simpleDurationMapper.selectValid_time(student_id, SimpleDateUtil.formatYYYYMMDD(new Date()) + " 00:00:00", SimpleDateUtil.formatYYYYMMDD(new Date()) + " 24:00:00");
    }

    @Override
    public Integer onlineTime(HttpSession session) {
        long student_id = StudentIdBySession(session);
        return simpleDurationMapper.selectOnline_time(student_id, SimpleDateUtil.formatYYYYMMDD(new Date()) + " 00:00:00", SimpleDateUtil.formatYYYYMMDD(new Date()) + " 24:00:00");
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
        return simpleTestRecordMapper.judgePreschoolTest(id);
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
        Student student = getStudent(session);

        // 获取今日已学单词
        int learnWord = learnMapper.getTodayWord(SimpleDateUtil.formatYYYYMMDD(new Date()), studentId);
        // 获取今日已学例句
        int learnSentence = learnMapper.getTodaySentence(SimpleDateUtil.formatYYYYMMDD(new Date()), studentId);
        map.put("learnWord", learnWord);
        map.put("learnSentence", learnSentence);
        map.put("sex",student.getSex());
        // 获取我的总金币
        Double myGoldD = simpleStudentMapper.myGold(studentId);
        BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
        int myGold = Integer.parseInt(mybd.toString());
        map.put("myGold", myGold);

        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

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
        List<String> list = runLogMapper.getStudentGold(SimpleDateUtil.formatYYYYMMDD(new Date()), studentId);
        double count = 0;
        String regex = "#(.*)#";
        Pattern pattern = Pattern.compile(regex);
        for (String str : list) {
            Matcher matcher = pattern.matcher(str);//匹配类
            while (matcher.find()) {
                count += Double.parseDouble(matcher.group(1));
            }
        }
        map.put("myThisGold", count);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse loginJudge(String account, String password, HttpSession session, String code, HttpServletRequest request) {

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        Student stu = LoginJudge(account, password);

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
            saveLoginRunLog(stu, request);

            // 当日首次登陆奖励5金币（日奖励）
            saveDailyAward(stu);

            // 判断学生是否有同步版课程，没有同步版课程不能进入智能版学习
            boolean hasCapacityCourse = this.hasCapacityCourse(stu);
            if (hasCapacityCourse) {
                result.put("capacity", true);
            }

            // 判断学生是否有同步版课程，没有同步版句子课程不能进入智能版学习
            boolean hasCapacityCourseSentence=this.hasCapacitySentence(stu);
            if(hasCapacityCourseSentence){
                result.put("capacitySentence", true);
            }

            // 判断学生是否有同步版课程，没有同步版课文课程不能进入智能版学习
            boolean hasCapacityTeks=this.hasCapacityTeks(stu);
            if(hasCapacityTeks){
                result.put("capacityTeks", true);
            }
            //判断学生是否有同步版阅读课程，没有同步版课文课程不能进入智能版学习
            result.put("capacityRead",false);
            //判断学生是否有同步版阅读课程，没有同步版课文课程不能进入智能版学习
            result.put("capacityAxisMotif",false);

            // 一个账户只能登陆一台
            judgeMultipleLogin(session, stu);

            // 2.判断是否需要完善个人信息
            if (!StringUtils.isNotBlank(stu.getHeadUrl()) || StringUtils.isEmpty(stu.getPetName())) {

                // 学校
                result.put("school_name", stu.getSchoolName());
                // 到期时间
                result.put("account_time", stu.getAccountTime());

                // 2.1 跳到完善个人信息页面
                return ServerResponse.createBySuccess("2", result);
            }

            // 正常登陆
            return ServerResponse.createBySuccess("1", result);
        }
    }

    @Override
    public boolean hasCapacityCourse(Student student) {
        int count = simpleCapacityStudentUnitMapper.countByType(student, 1);
        return count > 0;
    }

    /**
     * 判断学生是否可以学习同步班句型课程
     *
     * @param student
     * @return
     */
    private boolean hasCapacitySentence(Student student) {
        int count = simpleCapacityStudentUnitMapper.countByType(student, 2);
        return count > 0;
    }

    /**
     * 判断学生是否可以学习同步班课文课程
     *
     * @param student
     * @return
     */
    private boolean hasCapacityTeks(Student student) {
        int count = simpleCapacityStudentUnitMapper.countByType(student, 3);
        return count > 0;
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

        Date loginTime = SimpleDateUtil.parseYYYYMMDDHHMMSS(new Date());
        session.setAttribute(UserConstant.CURRENT_STUDENT, stu);
        session.setAttribute(TimeConstant.LOGIN_TIME, loginTime);

        Map<String, Object> sessionMap = new HashMap<>(16);
        sessionMap.put(UserConstant.CURRENT_STUDENT, stu);
        sessionMap.put(TimeConstant.LOGIN_TIME, loginTime);
        sessionMap.put("sessionId", session.getId());

        redisTemplate.opsForHash().put(RedisKeysConst.SESSION_MAP, session.getId(), sessionMap);
        redisTemplate.opsForHash().put(RedisKeysConst.LOGIN_SESSION, stu.getId(), session.getId());
    }

    public static void saveLogoutLog(Student student, SimpleRunLogMapper simpleRunLogMapper, Logger logger) {
        // 查询学生登录日志中最后一条记录时登录信息还是退出信息
        RunLog lastRunLog = simpleRunLogMapper.selectLastRunLogByOperateUserId(student.getId());
        if (lastRunLog == null || !lastRunLog.getLogContent().contains("退出登录")) {
            RunLog runLog = new RunLog(student.getId(), 1, "学生[" + student.getStudentName() + "]退出登录", new Date());
            try {
                simpleRunLogMapper.insert(runLog);
            } catch (Exception e) {
                logger.error("记录学生 [{}]->[{}] 退出登录信息失败！", student.getId(), student.getStudentName(), e);
            }
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
            stu.setAccountTime(new Date(System.currentTimeMillis() + stu.getRank() * 24 * 60 * 60 * 1000L));

            // 初始化学生勋章信息
            awardAsync.initAward(stu);
        }
    }

    /**
     * 学生当天首次登陆奖励5金币，并计入日奖励信息
     *
     * @param stu
     */
    private void saveDailyAward(Student stu) {
        int count = runLogMapper.countStudentTodayLogin(stu);
        if (count == 1) {
            // 每日首次登陆奖励
            dailyAwardAsync.firstLogin(stu);

            // 如果昨天辉煌荣耀奖励没有更新，将指定的奖励当前进度置为0
            medalAwardAsync.updateHonour(stu);
        }
    }

    private void saveLoginRunLog(Student stu, HttpServletRequest request) {
        String ip = null;
        try {
            ip = MacIpUtil.getIpAddr(request);
        } catch (Exception e) {
            logger.error("获取学生登录IP地址出错，error=[{}]", e.getMessage());
        }

        RunLog runLog = new RunLog(stu.getId(), 1, "学生[" + stu.getStudentName() + "]登录,ip=[" + ip + "]", new Date());
        try {
            runLogMapper.insert(runLog);
        } catch (Exception e) {
            logger.error("学生 {} -> {} 登录信息记录失败！ExceptionMsg:{}", stu.getId(), stu.getStudentName(), e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDurationInfo(Map<String, Object> sessionMap) {
        if (sessionMap != null) {
            Student student = (Student) sessionMap.get(UserConstant.CURRENT_STUDENT);
            Date loginTime = SimpleDateUtil.parseYYYYMMDDHHMMSS((Date) sessionMap.get(TimeConstant.LOGIN_TIME));
            Date loginOutTime = SimpleDateUtil.parseYYYYMMDDHHMMSS(new Date());
            if (loginTime != null && loginOutTime != null) {
                // 判断当前登录时间是否已经记录有在线时长信息，如果没有插入记录，如果有无操作
                int count = simpleDurationMapper.countOnlineTimeWithLoginTime(student, loginTime);
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
                    simpleDurationMapper.insert(duration);
                }
            }
        }
    }

    @Override
    public void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        SimpleValidateCode vCode = new SimpleValidateCode(150, 40, 4, 150);
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

package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.DurationUtil;
import com.zhidejiaoyu.common.utils.ValidateCode;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.impl.IndexServiceImpl;
import com.zhidejiaoyu.student.common.SaveGoldLog;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import com.zhidejiaoyu.common.constant.PetImageConstant;
import com.zhidejiaoyu.student.business.service.simple.SimpleLoginServiceSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SimpleCapacityReviewMapper capacityMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleCapacityStudentUnitMapper simpleCapacityStudentUnitMapper;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private GoldLogMapper goldLogMapper;

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
                int gold = 10;
                student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
                SaveGoldLog.saveStudyGoldLog(student.getId(), "首次修改密码", gold);

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
        Student student = super.getStudent(session);
        // 学生id
        Long studentId = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 学生id
        result.put("student_id", student.getId());
        // 课程包
        result.put("coursePackage", "课程中心");
        // 账号
        result.put("account", student.getAccount());
        // 昵称
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        // 宠物
        result.put("partUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        // 宠物名
        result.put("petName", student.getPetName());
        result.put("schoolName", student.getSchoolName());
        //性别
        if (1 == student.getSex()) {
            result.put("sex", "男");
        } else {
            result.put("sex", "女");
        }

        // 判断学生是否有智能版单词
        int count = simpleCapacityStudentUnitMapper.countByType(student, 1);
        result.put("hasCapacityWord", count > 0);

        // 有效时长  !
        Integer valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长 !
        Integer online = (int) DurationUtil.getTodayOnlineTime(session);
        // 今日学习效率 !
        if (valid >= online) {
            logger.error("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            valid = online - 1;
            result.put("efficiency", "99%");
        } else {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        }
        result.put("online", online);
        result.put("valid", valid);

        // 所有模块正在学习的课程id
        Map<Integer, Map<String, Long>> allCourse = simpleSimpleStudentUnitMapper.getAllUnit(student.getId());
        // 对应模块的课程id
        int courseId = 0;

        for (int i = 1; i <= 9; i++) {

            Map<String, Object> resultMap = new HashMap<>(16);
            // 当前模块未开启
            resultMap.put("open", false);

            // 1.获取模块对应在学的单元id
            if (allCourse.containsKey(i)) {
                courseId = allCourse.get(i).get("course_id") == null ? 0 : allCourse.get(i).get("course_id").intValue();
                // 当前模块已开启
                resultMap.put("open", true);
            }

            // 课程下一共有多少单词 /.
            int countWord = redisOpt.wordCountInCourse((long) courseId);

            // 课程已学 ./
            Integer sum = learnMapper.selectCourseWordNumberByStudentId(studentId, courseId, i);

            //-- 4.某课程某模块学习速度;
            Integer sumValid = simpleDurationMapper.valid_timeIndex(studentId, courseId, i + 13);
            if (sumValid == null) {
                sumValid = 0;
            }
            int speed = (int) (BigDecimalUtil.div(sum, sumValid) * 3600);

            // 继续学习状态
            resultMap.put("state", 2);
            resultMap.put("sum", sum + "");
            resultMap.put("countWord", countWord + "");
            // 速度
            resultMap.put("speed", speed + "");

            // 3.开始学习状态
            if (sum == 0) {
                // learn表是否有数据
                Integer status = learnMapper.getModelLearnInfo(studentId, testModelStr(i));
                if (status == null) {
                    // 开始学习
                    resultMap.put("state", 1);
                }
            }
            courseId = 0;
            result.put(i + "", resultMap);
        }

        // 值得元老勋章
        medalAwardAsync.oldMan(student);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 需要测试的模块
     *
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     */
    private boolean testModel(int type) {
        if (type == 1 || type == 2 || type == 3 || type == 4 || type == 6 || type == 8 || type == 9) {
            return true;
        } else {
            return false;
        }
    }

    private String testModelStr(int type) {
        if (type == 1) {
            return "单词辨音";
        } else if (type == 2) {
            return "词组辨音";
        } else if (type == 3) {
            return "快速单词";
        } else if (type == 4) {
            return "快速词组";
        } else if (type == 5) {
            return "词汇考点";
        } else if (type == 6) {
            return "快速句型";
        } else if (type == 7) {
            return "语法辨析";
        } else if (type == 8) {
            return "单词默写";
        } else {
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
        result.put("headUrl", AliyunInfoConst.host + student.getHeadUrl());

        // 有效时长  !
        int valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长 !
        int online = (int) DurationUtil.getTodayOnlineTime(session);
        result.put("online", LearnTimeUtil.validOnlineTime(online));
        result.put("valid", LearnTimeUtil.validOnlineTime(valid));
        // 今日学习效率 !
        String efficiency = LearnTimeUtil.efficiency(valid, online);
        result.put("efficiency", efficiency);

        // i参数 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
        //-- 1.查询学生当前单词模块学的那个单元
        Integer unitId = student.getSentenceUnitId();

        // 一共有多少单词/.
        Long countWord = simpleUnitVocabularyMapper.selectWordCountByUnitId((long) unitId);

        for (int i = 4; i < 7; i++) {
            Map<String, Object> a = new HashMap<String, Object>();
            //-- 如果 2 = NULL 就跳过4步执行5步   condition = 3(方框为空)
            //-- 如果 2 != NULL 执行4步跳过第5步 , 如果第2步>=80 condition = 1(方框为√), 如果第3步<80 condition = 2(方框为×)

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

                Integer speed = (int) (BigDecimalUtil.div(sum, sumValid) * 3600);
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
            result.put("partWGUrl", PetImageConstant.WIN);
        } else {
            result.put("hide", false);
        }

        return ServerResponse.createBySuccess(result);
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
        long studentId = super.getStudentId(session);
        Student student = getStudent(session);

        // 获取今日已学单词
        int learnWord = learnMapper.getTodayWord(DateUtil.formatYYYYMMDD(new Date()), studentId);
        // 获取今日已学例句
        int learnSentence = learnMapper.getTodaySentence(DateUtil.formatYYYYMMDD(new Date()), studentId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("learnWord", learnWord);
        map.put("learnSentence", learnSentence);
        map.put("sex", student.getSex());
        // 获取我的总金币
        int myGold = (int) BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        map.put("myGold", myGold);

        this.getMyLevelInfo(map, myGold, redisOpt);

        // 获取今日获得金币
        Integer todayGold = goldLogMapper.sumTodayAddGold(student.getId());
        map.put("myThisGold", todayGold == null ? 0 : todayGold);

        return ServerResponse.createBySuccess(map);
    }

    /**
     * 封装我的等级信息
     *
     * @param map
     * @param myGold
     */
    private void getMyLevelInfo(Map<String, Object> map, int myGold, RedisOpt redisOpt) {
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
    public boolean hasCapacityCourse(Student student) {
        int count = simpleCapacityStudentUnitMapper.countByType(student, 1);
        return count > 0;
    }

    @Override
    public void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ValidateCode vCode = new ValidateCode(150, 40, 4, 150);
        session.removeAttribute("validateCode");
        vCode.write(response.getOutputStream());
        session.setAttribute("validateCode", vCode.getCode());
        vCode.write(response.getOutputStream());
    }

}

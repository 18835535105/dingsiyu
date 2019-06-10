package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.SeniorityVo;
import com.zhidejiaoyu.common.Vo.simple.ccieVo.CcieVo;
import com.zhidejiaoyu.common.Vo.simple.ccieVo.MyCcieVo;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.simple.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtilDateUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDatesUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.TimeUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.WeekUtil;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimplePersonalCentreServiceSimple;
import com.zhidejiaoyu.student.utils.simple.CcieUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 消息中心
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SimplePersonalCentreServiceImplSimple extends SimpleBaseServiceImpl<SimpleCcieMapper, Ccie> implements SimplePersonalCentreServiceSimple {

    /**
     * 消息中心mapper接口
     */
    @Autowired
    private SimpleNewsMapper simpleNewsMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleDurationMapper simpleDurationMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleUnitVocabularyMapper simpleUnitVocabularyMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleUnitSentenceMapper simpleUnitSentenceMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleTeacherMapper simpleTeacherMapper;

    @Autowired
    private SimpleCcieMapper ccieMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Autowired
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Autowired
    private SimplePayCardMapper simplePayCardMapper;

    @Autowired
    private SimplePayLogMapper simplePayLogMapper;

    @Autowired
    private SimpleWorshipMapper worshipMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleMessageBoardMapper simpleMessageBoardMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private SimpleGradeMapper simpleGradeMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleRankingMapper simpleRankingMapper;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Override
    public ServerResponse<Object> personalIndex(HttpSession session) {
        Map<String, Object> map = new HashMap<>(16);
        // 获取当前学生信息
        Student student = getStudent(session);
        Long id = student.getId();
        map.put("name", student.getStudentName());
        map.put("headUrl", student.getHeadUrl());
        map.put("nickname", student.getNickname());
        map.put("sex", student.getSex() == null || student.getSex() == 1 ? "男" : "女");

        // 判断有哪些模块有未处理的信息
        redPoint(map, id);

        // 获取我的总金币
        Double myGoldD = simpleStudentMapper.myGold(id);
        BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
        int myGold = Integer.parseInt(mybd.toString());
        map.put("myGold", myGold);

        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

        // 我的等级myChildName
        int myrecord = 0;
        int myauto = 1;

        for (int i = 0; i < levels.size(); i++) {
            // 循环的当前等级分数
            int levelGold = (int) levels.get(i).get("gold");
            // 下一等级分数
            int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");
            // 下一等级索引
            int si = (i + 1) < levels.size() ? (i + 1) : i;

            if (myGold >= myrecord && myGold < xlevelGold) {
                // 我的等级
                map.put("levelName", levels.get(i).get("child_name"));
                break;
                // 等级循环完还没有确定等级 = 最高等级
            } else if (myauto == levels.size()) {
                break;
            }
            myrecord = levelGold;
            myauto++;
        }

        return ServerResponse.createBySuccess(map);
    }

    /**
     * 判断有哪些模块有需要处理的消息
     *
     * @param map
     * @param studentId
     */
    private void redPoint(Map<String, Object> map, Long studentId) {
        // 消息中心是否有未读消息
        unReadNews(map, studentId);

        // 我的证书是否有未查阅的证书
        myCcie(map, studentId);

        // 查看留言反馈是否有未查看的回复
        myFeedBack(map, studentId);

        // 可抽奖次数
        myLucky(map, studentId);
    }

    /**
     * 可抽奖次数
     *
     * @param map
     * @param studentId
     */
    private void myLucky(Map<String, Object> map, Long studentId) {
        Student student = simpleStudentMapper.selectById(studentId);
        map.put("lucky", student.getEnergy() == null ? 0 : (student.getEnergy() / 5));
    }

    private void myFeedBack(Map<String, Object> map, Long studentId) {
        int i = unReadFeedBackCount(studentId);
        map.put("messageRead", i);
    }

    private int unReadFeedBackCount(Long studentId) {
        MessageBoardExample messageBoardExample = new MessageBoardExample();
        MessageBoardExample.Criteria criteria = messageBoardExample.createCriteria();
        criteria.andStudentIdEqualTo(studentId).andReadFlagEqualTo(3).andRoleEqualTo(1);
        return simpleMessageBoardMapper.countByExample(messageBoardExample);
    }

    private void myCcie(Map<String, Object> map, Long studentId) {
        EntityWrapper<Ccie> ccieEntityWrapper = new EntityWrapper<>();
        ccieEntityWrapper.eq("student_id", studentId).eq("read_flag", 0);
        int i = ccieMapper.selectCount(ccieEntityWrapper);
        map.put("ccieRead", i);
    }

    private void unReadNews(Map<String, Object> map, Long studentId) {
        NewsExample example = new NewsExample();
        example.createCriteria().andStudentidEqualTo(studentId).andReadEqualTo(2);
        int i = simpleNewsMapper.countByExample(example);
        map.put("read", i);
    }

    /**
     * 消息通知
     */
    @Override
    public ServerResponse<Object> newsCentre(HttpSession session) {
        // 获取当前学生信息
        Long id = studentIdBySession(session);

        NewsExample example = new NewsExample();
        example.createCriteria().andStudentidEqualTo(id);
        PageHelper.startPage(1, 30);
        List<News> list = simpleNewsMapper.selectByExample(example);

        return ServerResponse.createBySuccess(list);
    }


    /**
     * 1.删除
     * 2.根据通知id标记为已读
     * 3.全部标记为已读
     */
    @Override
    public ServerResponse<String> newsUpdate(HttpSession session, Integer state, Integer[] id) {
        // 获取当前学生信息
        Long studentId = studentIdBySession(session);

        // 1.删除(根据消息通知id)
        if (state == 1) {
            for (Integer intId : id) {
                simpleNewsMapper.deleteByPrimaryKey(Long.valueOf(intId));
            }
            return ServerResponse.createBySuccessMessage("删除完成");
            // 2.根据通知id标记为已读(根据消息通知id)
        } else if (state == 2) {
            News ne = new News();
            for (Integer intId : id) {
                ne.setId(Long.valueOf(intId));
                ne.setRead(1);
                simpleNewsMapper.updateByPrimaryKeySelective(ne);
            }
            return ServerResponse.createBySuccessMessage("已标记为已读");
            // 3.全部标记为已读(根据学生id)
        } else if (state == 3) {
            simpleNewsMapper.updateByRead(studentId);
            return ServerResponse.createBySuccessMessage("已全部标记为已读");
        }

        return ServerResponse.createByError();
    }

    /**
     * 消息中心-每周时长页面信息
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> weekDurationIndex(HttpSession session) {
        // 获取当前学生信息
        Long studentId = studentIdBySession(session);

        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");

        // 返回结果集
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 学生在该系统学习过的年份 select date_format(learn_time, '%Y') from learn where student_id = 3155 and learn_time IS NOT NULL GROUP BY  date_format(learn_time, '%Y')
        List<Integer> years = learnMapper.selectLearn_times(studentId);
        if (years.size() > 0) {
            // 把年份从小到大排序
            Collections.sort(years);
        }

        // 获取当前时间所在年的周数
        int ye = WeekUtil.getWeekOfYear(new Date()) - 1;

        // 获取当前年份
        int we = SimpleDateUtil.DateYYYY();

        // 遍历学生学习过的年份
        for (Integer year : years) {

            // 根据遍历的年份获取有多少周
            int yearMax = WeekUtil.getMaxWeekNumOfYear(year) - 1; // 如:52

            // 遍历一年中的每周
            for (int i = 0; i <= yearMax; i++) {
                // 用于封装一周的数据
                Map<String, Object> map = new LinkedHashMap<String, Object>();

                // 3.周的起始日期
                Date weekStart = WeekUtil.getFirstDayOfWeek(year, i);
                // 4.周的结尾日期
                Date weekEnd = WeekUtil.getLastDayOfWeek(year, i);
                map.put("statsDate", s.format(weekStart));// 每周开始日期
                map.put("endDate", s.format(weekEnd));// 每周结束日期
                map.put("week", "第" + (i + 1) + "周"); // 第几周
                //map.put("weekSort", (i+1));// 周,该字段用于排序
                map.put("state", false); // 不是本周
                // 查询循环周的总有效时长(m)
                Integer duration = simpleDurationMapper.totalTime(SimpleDateUtil.formatYYYYMMDD(weekStart), SimpleDateUtil.formatYYYYMMDD(weekEnd), studentId);
                String timeStrBySecond = TimeUtil.getTimeStrBySecond(duration);
                int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 12600) * 100));
                if (plan <= 100) {
                    map.put("pluSign", false);// 不显示+号
                    map.put("duration", plan);// 百分比, 用于展示进度条
                } else {
                    map.put("pluSign", true);// 显示+号
                    map.put("duration", 100);// 百分比, 用于展示进度条
                }
                map.put("durationStr", timeStrBySecond);// 总有效时长格式: 00小时00分00秒

                // 到达当前时间周停止循环,响应数据
                if (year == we) {
                    if (i == ye) {
                        // 本周
                        map.put("week", "本周");
                        map.put("state", true); // 本周, 有继续学习按钮
                        result.add(map);
                        // 翻转list顺序 - 学习年份,周倒序展示
                        Collections.reverse(result);
                        return ServerResponse.createBySuccess(result);
                    }
                }
                result.add(map);
            }
        }
        // 翻转list顺序
        Collections.reverse(result);
        return ServerResponse.createBySuccess(result);
    }

    /**
     * 从session中获取学生id(本类方法)
     *
     * @param session
     * @return
     */
    private long studentIdBySession(HttpSession session) {
        // 获取当前学生信息
        Student student = getStudent(session);
        return student.getId();
    }

    /**
     * 每周总学习量
     * 每周六个模块个个的学习量
     */
    @Override
    public ServerResponse<Object> weekQuantity(HttpSession session) {
        // 获取当前学生id
        Long studentId = studentIdBySession(session);

        // 设置响应需要的日期格式
        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");

        // 设置返回封装结果集
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 学生在该系统学习过的年份
        List<Integer> years = learnMapper.selectLearn_times(studentId);
        if (years.size() > 0) {
            // 把年份从小到大排序
            Collections.sort(years);
        }

        // 获取当前时间所在年的周数
        int ye = WeekUtil.getWeekOfYear(new Date()) - 1;

        // 获取当前时间所在周
        int we = SimpleDateUtil.DateYYYY();

        for (Integer year : years) {

            // 1.根据年获取有多少周
            int yearMax = WeekUtil.getMaxWeekNumOfYear(year) - 1;

            for (int i = 0; i <= yearMax; i++) {
                // 封装返回结果集
                Map<String, Object> map = new LinkedHashMap<String, Object>();

                // 3.周的起始日期
                Date weekStart = WeekUtil.getFirstDayOfWeek(year, i);
                // 4.周的结尾日期
                Date weekEnd = WeekUtil.getLastDayOfWeek(year, i);

                map.put("statsDate", s.format(weekStart));// 每周开始日期
                map.put("endDate", s.format(weekEnd));// 每周结束日期
                map.put("week", "第" + (i + 1) + "周"); // 第几周
                //map.put("weekSort", (i+1));// 周,该字段用于排序
                map.put("state", false); // 不是本周

                // 每周总学习量
                //Integer count = learnMapper.weekCountQuantity(DateUtil.formatYYYYMMDD(weekStart), DateUtil.formatYYYYMMDD(weekEnd), studentId);
                //map.put("countQuantity", count); // 每周总学习量

                // 每周总学习量
                // 每周六个模块个个的学习量
                Map<String, Object> countMap = learnMapper.mapWeekCountQuantity(SimpleDateUtil.formatYYYYMMDD(weekStart), SimpleDateUtil.formatYYYYMMDD(weekEnd), studentId);
                Long duration = (Long) countMap.get("count");
                int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 300) * 100));
                if (plan <= 100) {
                    map.put("plusSign", false); // 进度条不显示+号
                    map.put("duration", plan); // 本分比进度条
                } else {
                    map.put("plusSign", true); // 进度条显示+号
                    map.put("duration", 100); // 本分比进度条
                }
                map.put("count", duration); // 周总学习量
                map.put("a", countMap.get("a")); // 周慧记忆学习量
                map.put("b", countMap.get("b")); // 周慧听写学习量
                map.put("c", countMap.get("c")); // 周慧默写学习量
                map.put("d", countMap.get("d")); // 周例句听力学习量
                map.put("e", countMap.get("e")); // 周例句翻译学习量
                map.put("f", countMap.get("f")); // 周例句默写学习量

                // 到达当前时间周停止循环
                if (year == we) {
                    if (i == ye) {
                        // 本周
                        map.put("week", "本周");
                        map.put("state", true); // 本周, 有继续学习按钮
                        result.add(map);
                        // 翻转list顺序
                        Collections.reverse(result);
                        return ServerResponse.createBySuccess(result);
                    }
                }
                result.add(map);
            }
        }

        return null;
    }

    /**
     * 课程统计
     * <p>
     * 1. 根据学生id查出学生学过那些课本
     * 2. 根据课本查询六个模块各自的 "(单词学习量/总单词量)=学习量百分比",课程总单词量,学习效率
     * 3. 根据课程和模块查询个个单元的,学习量,和单元总词量,单元测试失败标示
     * 注:课程总词量例句和词汇分别查一次就行
     * 单元总词量同上
     * <p>
     * 1. 前端先把单元圈遍历出来
     * 2.
     */
    @Override
    public ServerResponse<Object> courseStatistics(HttpSession session, int page, int rows) throws Exception {
        // 获取当前学生id
        Long studentId = studentIdBySession(session);

        // 返回数据集包含分页
        Map<String, Object> resultMapAll = new HashMap<>(16);

        // 封装所有课程信息
        List<Map<String, Object>> result = new ArrayList<>();

        // Map = id, course_name, version, label, learn_count
        // 获取学生学习过得课程id,课程名,版本,标签,课程学习遍数 : id, course_name, version, label, learn_count
        PageHelper.startPage(page, rows);
        List<Map<String, Object>> courses = simpleCourseMapper.courseLearnInfo(studentId);

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(courses);
        resultMapAll.put("page", page);
        resultMapAll.put("total", pageInfo.getPages());

        // 各个课程下单词总个数
        Map<Long, Map<Long, Long>> courseWordCountMap = null;
        // 各个课程下所有单元 id 和 单元名
        Map<Long, List<Map<String, Object>>> allUnitInCourse = null;
        // 各个课程下的最大单元 id
        Map<Long, Map<Long, Long>> maxUnitIdMap = null;
        // 各个单元总单词数
        Map<Long, Map<Long, Long>> unitTotalWordMap = null;
        // 所有单元单元闯关测试历史最高成绩
        Map<Long, Map<Long, Integer>> unitTestMaxPointMap = null;
        // 所有单元已学习单词数
        Map<Long, Map<Long, Long>> unitLearnWordMap = null;
        if (courses.size() > 0) {
            List<Long> courseIds = new ArrayList<>(courses.size());
            courses.forEach(map -> courseIds.add((long) map.get("id")));

            maxUnitIdMap = simpleStudentMapper.selectMaxUnitIdMapByCourseIds(courseIds, studentId);

            courseWordCountMap = vocabularyMapper.countWordMapByCourseIds(courseIds);
            List<Map<String, Object>> allUnitInCourseList = unitMapper.selectIdAndUnitNameByCourseIds(courseIds);
            if (allUnitInCourseList.size() > 0) {
                allUnitInCourse = new HashMap<>(16);
                List<Map<String, Object>> list;
                for (Map<String, Object> objectMap : allUnitInCourseList) {
                    Map<String, Object> map = new HashMap<>(16);
                    if (allUnitInCourse.containsKey(objectMap.get("courseId"))) {
                        list = allUnitInCourse.get(objectMap.get("courseId"));
                        map.put("id", objectMap.get("id"));
                        map.put("unit_name", objectMap.get("unitName"));
                        list.add(map);
                        allUnitInCourse.put((Long) objectMap.get("courseId"), list);
                    } else {
                        list = new ArrayList<>();
                        map.put("id", objectMap.get("id"));
                        map.put("unit_name", objectMap.get("unitName"));
                        list.add(map);
                        allUnitInCourse.put((Long) objectMap.get("courseId"), list);
                    }
                }
            }
        }

        // 封装一个课程的数据
        Map<String, Object> resultMap;
        // 各个单元信息
        Map<String, Object> unitInfo;
        // 课程下单词总个数
        Long courseWordCount;
        // 获取课程下已学单词数
        Integer learnCountWord = 0;
        // 计算单词模块百分比值
        int planVocabulary = 0;
        Long courseId;
        // 当前所学课程单元id
        Long maxUnitId = null;
        // 当前单元单元闯关测试历史最高成绩
        Integer unitTestMaxPoint = null;
        // 单元总单词数
        Long unitTotalWord = null;
        // 单元已学习单词数
        Long unitLearnWord = null;
        // 循环学过的所有课程
        for (Map<String, Object> map : courses) {
            resultMap = new HashMap<>(16);

            // 当前课程id
            courseId = (Long) map.get("id");

            // 课程id
            resultMap.put("id", courseId);

            // 课程属于模块名
            String versionName = map.get("version").toString();
            String modelName = versionName.substring(versionName.indexOf(" ") + 1);
            // 模块名
            resultMap.put("modelName", modelName);

            // 课程名
            if (versionName.contains("高中英语") || versionName.contains("初中英语") || versionName.contains("小学英语")) {
                resultMap.put("versionLabel", map.get("version"));
            } else {
                // 版本 年级-上册/下册
                resultMap.put("versionLabel", map.get("version") + " " + (map.get("grade") == null ? "" : map.get("grade")) + "-" + (map.get("label") == null ? "" : map.get("label")));
            }

            // 课程学习的第几遍
            resultMap.put("learnCount", map.get("learn_count"));

            if (courseWordCountMap != null && courseWordCountMap.get(courseId) != null) {
                courseWordCount = courseWordCountMap.get(courseId).get("count");
                learnCountWord = learnMapper.countSimpleByCourseId(studentId, courseId);
                planVocabulary = ((int) (BigDecimalUtil.div(learnCountWord, courseWordCount) * 100));
            }

            if (planVocabulary > 0) {
                // 课程模块百分比值
                resultMap.put("duration", planVocabulary);
            } else if (learnCountWord > 0) {
                // 除数小于1, 并存在已学单词, 设置进度条为1
                resultMap.put("duration", 1);
            }

            // 获取课程下所有单元id, unit_name
            List<Map<String, Object>> allUnit;
            List<Long> unitIds = null;
            if (allUnitInCourse == null) {
                allUnit = new ArrayList<>(0);
            } else {
                allUnit = allUnitInCourse.get(courseId);

                unitIds = new ArrayList<>(allUnit.size());
                for (Map<String, Object> objectMap : allUnit) {
                    unitIds.add(objectMap.get("id") == null ? null : (Long) objectMap.get("id"));
                }
            }
            if (maxUnitIdMap != null && maxUnitIdMap.get(courseId) != null) {
                maxUnitId = maxUnitIdMap.get(courseId).get("unitId");
            }

            // 用于封装所有单元的状态
            List<Map> unitState = new ArrayList<>();

            if (unitIds != null) {
                List<Long> finalUnitIds = unitIds;
                Future<Map<Long, Map<Long, Integer>>> unitTestMaxPointMapFuture = executorService.submit(() -> simpleTestRecordMapper.selectUnitTestMaxPointMapByUnitIds(studentId, finalUnitIds, modelName));
                Future<Map<Long, Map<Long, Long>>> unitTotalWordMapFuture = executorService.submit(() -> simpleUnitVocabularyMapper.countTotalWordMapByUnitIds(finalUnitIds));
                Future<Map<Long, Map<Long, Long>>> unitLearnWordMapFuture = executorService.submit(() -> learnMapper.countUnitLearnWordMapByUnitIds(studentId, finalUnitIds, modelName));
                while (true) {
                    if (unitTestMaxPointMapFuture.isDone() && unitTotalWordMapFuture.isDone() && unitLearnWordMapFuture.isDone()) {
                       break;
                    }
                }
                unitTestMaxPointMap = unitTestMaxPointMapFuture.get();
                unitTotalWordMap = unitTotalWordMapFuture.get();
                unitLearnWordMap = unitLearnWordMapFuture.get();
            }

            int a = 0;
            for (Map<String, Object> unitMap : allUnit) {
                unitInfo = new HashMap<>(16);
                Long unitId = unitMap.get("id") == null ? null : Long.valueOf(unitMap.get("id").toString());
                if (unitId == null) {
                    continue;
                }

                unitInfo.put("unitId", unitId);
                unitInfo.put("unitName", unitMap.get("unit_name"));


                if (unitTestMaxPointMap != null && unitTestMaxPointMap.get(unitId) != null) {
                    unitTestMaxPoint = unitTestMaxPointMap.get(unitId).get("point");
                }

                if (a != 3) {
                    // 正在学的单元
                    if (Objects.equals(unitMap.get("id"), maxUnitId)) {
                        // 单元总单词数量
                        if (unitTotalWordMap != null && unitTotalWordMap.get(unitId) != null) {
                            unitTotalWord = unitTotalWordMap.get(unitId).get("count");
                        }

                        if (unitLearnWordMap != null && unitLearnWordMap.get(unitId) != null) {
                            unitLearnWord = unitLearnWordMap.get(unitId).get("count");
                        }

                        if ("词汇考点".equals(modelName) || "语法辨析".equals(modelName)) {
                            // 当前两个模块没有测试。
                            if (unitTotalWord != null && unitLearnWord != null && unitLearnWord >= unitTotalWord) {
                                // 已学完状态
                                unitInfo.put("color", 6);
                            }
                        } else if (unitTestMaxPoint == null) {
                            // 还未做单元测试3
                            unitInfo.put("color", 3);
                        } else if (unitTestMaxPoint >= 80) {
                            // 单元测试通过
                            unitInfo.put("color", 1);
                        } else {
                            // 单元测试未通过
                            unitInfo.put("color", 2);
                        }
                        if (unitTotalWord != null && unitLearnWord != null && unitLearnWord < unitTotalWord) {
                            // 正在学状态4
                            unitInfo.put("color", 4);
                        }
                        a = 3;
                    }

                    // 学过的单元
                    if (a == 0) {
                        if ("词汇考点".equals(modelName) || "语法辨析".equals(modelName)) {
                            // 已学完状态
                            unitInfo.put("color", 6);
                        } else if (unitTestMaxPoint != null && unitTestMaxPoint >= 80) {
                            // 状态1 单元测试通过
                            unitInfo.put("color", 1);
                        } else if (unitTestMaxPoint != null) {
                            // 状态2 单元测试未通过
                            unitInfo.put("color", 2);
                        } else {
                            // 未测试
                            unitInfo.put("color", 4);
                        }
                    }
                } else {
                    // 未学单元 状态5 空白
                    unitInfo.put("color", 5);
                }

                if (!unitInfo.containsKey("color") && !"词汇考点".equals(modelName) && !"语法辨析".equals(modelName)) {
                    // 还未做单元测试3
                    unitInfo.put("color", 3);
                }

                unitState.add(unitInfo);
            }
            // 课程封装单元
            resultMap.put("unitState", unitState);
            // 封装所有课程
            result.add(resultMap);
        }

        // 封装所有课程和分页信息
        resultMapAll.put("dataList", result);

        return ServerResponse.createBySuccess(resultMapAll);
    }

    /**
     * 我的报告
     * 3.课程统计
     * 点击某个课程某个模块下的某个单元 显示 已学/单词总量
     *
     * @param session
     * @param courseId   课程id
     * @param unitNumber 第几个单元
     * @param model      模块: 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
     * @return 所选单元已学单词数量 / 单元单词总量
     */
    @Override
    public ServerResponse<Object> courseStatisticsCount(HttpSession session, Integer courseId, Integer model,
                                                        Integer unitNumber) {
        Map<String, Object> result = new HashMap<String, Object>();

        // 获取当前学生id
        Long studentId = studentIdBySession(session);

        // 获取单元id
        Map<String, Object> mapDate = unitMapper.getUnitIdByCourseIdAndUnitNumber(courseId, unitNumber);
        Long unitId = (Long) mapDate.get("id");

        result.put("courseNameOne", mapDate.get("course_name"));// 课程名方式1
        result.put("courseNameTwo", mapDate.get("version") + "-" + mapDate.get("label"));// 课程名方式2

        // 查询单元下边有多少已学单词
        Integer LearnWord = learnMapper.countByWord(studentId, unitId, model);
        result.put("yet", LearnWord); // 已学单词/例句

        if (model < 4) {
            // 查询单元下边有多少单词
            Long countByWord = simpleUnitVocabularyMapper.selectWordCountByUnitId(unitId);
            result.put("count", countByWord); // 单元总单词/例句量
        } else if (model > 3) {
            // 查询单元下边有多少例句
            int countBySentence = simpleUnitSentenceMapper.countByUnitId(unitId);
            result.put("count", countBySentence); // 单元总单词/例句量
        }

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 我的排名
     *
     * @param model        本班排行模块  model = 1
     *                     本校模块 model = 2
     *                     全国模块 model = 3
     * @param queryType    空代表全部查询，1=今日排行 2=本周排行 3=本月排行
     * @param golds        金币 1=正序 2=倒叙  - 默认金币倒叙排行
     * @param badges       勋章 1=正序 2=倒叙
     * @param certificates 证书 1=正序 2=倒叙
     * @param worships     膜拜 1=正序 2=倒叙
     */
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public ServerResponse<Object> classSeniority(HttpSession session, Integer page, Integer rows,
                                                 String golds, String badges, String certificates, String worships, String model, Integer queryType) {

        Map<String, Object> result = new HashMap<>(16);

        // 获取当前学生信息
        Student student = getStudent(session);

        final String KEY = "simple_student_rank";
        final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + golds + ":" + badges + ":" + certificates + ":" + worships + ":" + model + ":" + queryType;
        try {
            Object object = redisTemplate.opsForHash().get(KEY, FIELD);
            if (object != null) {
                result = (Map<String, Object>) object;
                if (result.get("myDate") != null) {
                    Map<String, Object> myData = (Map<String, Object>) result.get("myDate");
                    if (Objects.equals((int) BigDecimalUtil.add(student.getOfflineGold(), student.getSystemGold()), myData.get("myGold"))) {
                        return ServerResponse.createBySuccess(result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
        }

        // 教师id
        Long teacherId = student.getTeacherId();
        // 班级id
        Long classId = student.getClassId();

        Integer schoolAdminId = super.getSchoolAdminId(student);

        // 获取 `清学版每个学生的信息`
        List<Map<String, Object>> students = simpleStudentMapper.selectSeniority(model, teacherId, classId, schoolAdminId);

        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

        // 用于封装我的排名,我的金币,我的等级,我被膜拜
        Map<String, Object> myMap = new HashMap<>(16);

        result.put("mySchool", student.getSchoolName());

        result.put("myClass", "暂无班级");

        // 我的排名(全部)
        Map<Long, Map<String, Object>> classLevel;
        if (queryType == null) {
            if ("3".equals(model)) {
                // 全国排名
                classLevel = simpleStudentMapper.selectLevelByStuId(student, 3, null);
            } else if ("2".equals(model)) {
                // 学校排名
                classLevel = simpleStudentMapper.selectLevelByStuId(student, 2, schoolAdminId);
            } else {
                // 班级排名
                classLevel = simpleStudentMapper.selectLevelByStuId(student, 1, null);
            }
            if (classLevel == null || classLevel.get(student.getId()) == null) {
                return ServerResponse.createBySuccess(result);
            }
            String myRankingDouble = (classLevel.get(student.getId())).get("rank") + "";
            if (myRankingDouble.contains(".")) {
                myRankingDouble = myRankingDouble.substring(0, myRankingDouble.indexOf("."));
            }
            // 我的排名
            // 全国排行未进入前100名标记为 未上榜
            if (Objects.equals("3", model) && StringUtils.isNotEmpty(myRankingDouble) && Integer.valueOf(myRankingDouble) > 100) {
                myMap.put("myRanking", "未上榜");
            } else {

                myMap.put("myRanking", myRankingDouble);

            }
        }

        // 我的排行 今日，本周，本月
        if (queryType != null) {
            // 查出来的顺序金币是从大到小
            List<Integer> queryTypeList = new ArrayList<>();

            // 我的排名(今日)
            if (queryType == 1) {
                queryTypeList = runLogMapper.getAllQueryType(SimpleDateUtil.formatYYYYMMDD(new Date()), model, student);
            }
            // 我的排名(本周)
            if (queryType == 2) {
                Date week = WeekUtil.getFirstDayOfWeek(new Date());
                queryTypeList = runLogMapper.getAllQueryType(week.toString(), model, student);
            }
            // 我的排名(本月)
            if (queryType == 3) {
                queryTypeList = runLogMapper.getAllQueryType(WeekUtil.getMonthOne(new Date()), model, student);
            }

            if (queryTypeList.contains(student.getId().intValue())) {
                myMap.put("myRanking", queryTypeList.lastIndexOf(student.getId().intValue())); // 我的金币排名
            } else {
                myMap.put("myRanking", "未上榜"); // 我的排名
            }
        }

        // 我的金币myGold
        int myGold = 0;
        // 全部排行
        if (queryType == null) {
            Double myGoldD = simpleStudentMapper.myGold(student.getId());
            BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
            myGold = Integer.parseInt(mybd.toString());
        } else {
            // 今日，本周，本月排行
            // 我的排名(今日)
            Map<Long, Map<String, Object>> m = new HashMap();
            if (queryType == 1) {
                m = runLogMapper.getGoldByStudentId(SimpleDateUtil.formatYYYYMMDD(new Date()), model, student);
            }
            // 我的排名(本周)
            if (queryType == 2) {
                Date week = WeekUtil.getFirstDayOfWeek(new Date());
                m = runLogMapper.getGoldByStudentId(week.toString(), model, student);
            }
            // 我的排名(本月)
            if (queryType == 3) {
                m = runLogMapper.getGoldByStudentId(WeekUtil.getMonthOne(new Date()), model, student);
            }

            if (m.containsKey(student.getId())) {
                myGold = Integer.parseInt(m.get(student.getId()).get("jb").toString());
            }
        }

        // 我被膜拜myMb
        int myMb = simpleStudentMapper.myMb(student.getId());
        // 我的等级myChildName
        String myChildName = "";
        if (myGold >= 50) {
            int myrecord = 0;
            int myauto = 1;
            for (int i = 0; i < levels.size(); i++) {
                // 循环的当前等级分数
                int levelGold = (int) levels.get(i).get("gold");
                // 下一等级分数
                int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");

                if (myGold >= myrecord && myGold < xlevelGold) {
                    myChildName = levels.get(i).get("child_name").toString();
                    break;
                    // 等级循环完还没有确定等级 = 最高等级
                } else if (myauto == levels.size()) {
                    myChildName = levels.get(i).get("child_name").toString();
                    break;
                }
                myrecord = levelGold;
                myauto++;
            }
        }

        myMap.put("myGold", myGold); // 我的金币
        myMap.put("myMb", myMb); // 我被膜拜
        myMap.put("myChildName", myChildName); // 我的等级
        myMap.put("stuId", student.getId());

        //    	-- 学生对应证书
        Map<Long, Map<String, Long>> ccieCount = ccieMapper.getMapKeyStudentCCie();
        //		-- 膜拜数据
        Map<Long, Map<String, Long>> worshipCount = worshipMapper.getMapKeyStudentWorship();
        //		-- 学生勋章
        //Map<Long, Map<String, Long>> runLogCount = runLogMapper.getMapKeyStudentrunLog();
        Map<Long, Map<String, Long>> runLogCount = simpleAwardMapper.getMapKeyStudentXZ();

        // 遍历学生信息, 并初始化学生证书，膜拜，勋章，等级
        for (Map<String, Object> stu : students) {
            // 学生id
            Long id = (Long) stu.get("id");

            // 获取当前学生证书数量
            //int ccieCount = ccieMapper.getCountCcieByStudentId(id);
            //stu.put("zs", ccieCount);
            if (ccieCount.containsKey(id)) {
                stu.put("zs", ccieCount.get(id).get("count"));
            } else {
                stu.put("zs", 0);
            }

            // 获取当前学生膜拜数量
            //int worship = worshipMapper.getCountWorshipByStudentId(id);
            //stu.put("mb", worship);
            if (worshipCount.containsKey(id)) {
                stu.put("mb", worshipCount.get(id).get("count"));
            } else {
                stu.put("mb", 0);
            }

            // 当前学生勋章个数
            //int xz = runLogMapper.getCountXZByStudentId(id);
            //stu.put("xz", 0);
            if (runLogCount.containsKey(id)) {
                stu.put("xz", runLogCount.get(id).get("count"));
            } else {
                stu.put("xz", 0);
            }

            // 学生金币
            Double goldd = (Double) stu.get("gold");
            int gold = Integer.parseInt(new BigDecimal(goldd).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
            stu.put("gold", gold);
            stu.put("headUrl", stu.get("head_url"));

            // 等级计算
            if (gold >= 50) {
                int record = 0;
                // 用于记录是不是最大值
                int auto = 1;

                for (int i = 0; i < levels.size(); i++) {
                    // 循环的当前等级分数
                    int levelGold = (int) levels.get(i).get("gold");
                    // 下一等级分数
                    int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");

                    if (gold >= record && gold < xlevelGold) {
                        stu.put("childName", levels.get(i).get("child_name"));
                        break;
                        // 等级循环完还没有确定等级 = 最高等级
                    } else if (auto == levels.size()) {
                        stu.put("childName", levels.get(i).get("child_name"));
                        break;
                    }

                    record = levelGold;
                    auto++;
                }
            } else {
                stu.put("childName", levels.get(0).get("child_name"));
            }
        }

        // 排序
        if ("1".equals(golds)) { // 按照金币
            //Collections.sort(students, new MapGoldAsc());
            Collections.sort(students, new MapGoldDesc());
        } else if ("2".equals(golds)) {
            Collections.sort(students, new MapGoldDesc());

        } else if ("1".equals(badges)) {// 按照勋章
            //Collections.sort(students, new MapXzAsc());
            Collections.sort(students, new MapXzDesc());
        } else if ("2".equals(badges)) {
            Collections.sort(students, new MapXzDesc());

        } else if ("1".equals(certificates)) {// 按照证书
            //Collections.sort(students, new MapZsAsc());
            Collections.sort(students, new MapZsDesc());
        } else if ("2".equals(certificates)) {
            Collections.sort(students, new MapZsDesc());

        } else if ("1".equals(worships)) {// 按照膜拜
            //Collections.sort(students, new MapMbAsc());
            worshipMapper.updState(student.getId());
            Collections.sort(students, new MapMbDesc());
        } else if ("2".equals(worships)) {
            worshipMapper.updState(student.getId());
            Collections.sort(students, new MapMbDesc());
        } else {
            // 默认本班金币倒叙
            Collections.sort(students, new MapGoldDesc());
        }

        // list最大限制到100
        List<Map<String, Object>> lista = new ArrayList<>(students.subList(0, students.size() > 100 ? 100 : students.size()));

        // 总参与人数
        myMap.put("number", lista.size());
        if ("3".equals(model)) {
            myMap.put("number", "99999+");
        }
        result.put("myDate", myMap); // 把我的信息封装到返回结果集中

        // 我的排名,根据选项实时更换排行
        changeRank(golds, worships, model, student, myMap, lista);

        result.put("page", page);
        if (lista.size() % rows == 0) {
            result.put("total", lista.size() / rows);
        } else {
            result.put("total", lista.size() / rows + 1);
        }

        // 对list分页
        page = (page - 1) * rows;
        rows = page + rows;
        List<Map<String, Object>> list = new ArrayList<>(lista.subList(page > lista.size() ? lista.size() : page, lista.size() > rows ? rows : lista.size()));
        // 把排行数据放到返回结果集中
        result.put("phDate", list);

        if (student.getClassId() != null) {
            Grade grade = simpleGradeMapper.selectById(student.getClassId());
            if (grade != null) {
                result.put("myClass", grade.getClassName());
            }
        }

        redisTemplate.opsForHash().put(KEY, FIELD, result);
        redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
        return ServerResponse.createBySuccess(result);
    }

    /**
     * 我的排名,根据选项实时更换排行
     *
     * @param golds
     * @param worships
     * @param model
     * @param student
     * @param myMap
     * @param lista
     */
    private void changeRank(String golds, String worships, String model, Student student, Map<String, Object> myMap, List<Map<String, Object>> lista) {
        Ranking ranking = simpleRankingMapper.selByStudentId(student.getId());
        int aa = 0;
        Long id = student.getId();
        for (Map m : lista) {
            aa++;
            if (m.get("id").equals(id)) {
                myMap.put("myRanking", aa);
                if (model.equals("1")) {
                    if (golds != null && golds != "") {
                        if (ranking.getGoldClassRank() != aa) {
                            ranking.setGoldClassRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                    if (worships != null && worships != "") {
                        if (ranking.getWorshipClassRank() != aa) {
                            ranking.setWorshipClassRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                }
                if (model.equals("2")) {
                    if (golds != null && golds != "") {
                        if (ranking.getGoldSchoolRank() != aa) {
                            ranking.setGoldSchoolRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                    if (worships != null && worships != "") {
                        if (ranking.getWorshipSchoolRank() != aa) {
                            ranking.setWorshipSchoolRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                }
                if (model.equals("3")) {
                    if (golds != null && golds != "") {
                        if (ranking.getGoldCountryRank() != aa) {
                            ranking.setGoldCountryRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                    if (worships != null && worships != "") {
                        if (ranking.getWorshipCountryRank() != aa) {
                            ranking.setWorshipCountryRank(aa);
                            simpleRankingMapper.updateById(ranking);
                        }
                    }
                }
            }
        }
    }


    /**
     * 我的排名
     *
     * @param model       本班排行模块  model = 1
     *                    本校模块 model = 2
     *                    全国模块 model = 3
     * @param queryType   空代表全部查询，1=今日排行 2=本周排行 3=本月排行
     * @param gold        金币 1=正序 2=倒叙  - 默认金币倒叙排行
     * @param badge       勋章 1=正序 2=倒叙
     * @param certificate 证书 1=正序 2=倒叙
     * @param worship     膜拜 1=正序 2=倒叙
     */
    @Override
    public ServerResponse<Object> rankingSeniority(HttpSession session, Integer page, Integer rows, String gold, String badge, String certificate, String worship, String model, Integer queryType) {
        if (!(gold.equals("1") || gold.equals("2")) && !(badge.equals("1") || badge.equals("2")) && !(certificate.equals("2") || certificate.equals("1")) && !(worship.equals("1") || worship.equals("2"))) {
            gold = "2";
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("page", page);
        Student student = getStudent(session);
        Integer start = page == 1 ? 0 : (page - 1) * rows;
        if (gold != null && !gold.equals("")) {
            // 获取当前学生信息
            final String KEY = "simple_student_rank";
            final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + "gold=" + gold + "model=" + model + "queryType=" + queryType;
            try {
                Object object = redisTemplate.opsForHash().get(KEY, FIELD);
                if (object != null) {
                    map = (Map<String, Object>) object;
                    return ServerResponse.createBySuccess(map);
                }
            } catch (Exception e) {
                log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
            }

            getGoldRanking(model, gold, student, map, start, rows);
            redisTemplate.opsForHash().put(KEY, FIELD, map);
            if(model.equals("3")){
                redisTemplate.expire(KEY, 7, TimeUnit.DAYS);
            }else{
                redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
            }
            return ServerResponse.createBySuccess(map);
        }
        if (badge != null && !badge.equals("")) {
            // 获取当前学生信息
            final String KEY = "simple_student_rank";
            final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + "badge=" + badge + "model=" + model + "queryType=" + queryType;
            try {
                Object object = redisTemplate.opsForHash().get(KEY, FIELD);
                if (object != null) {
                    map = (Map<String, Object>) object;
                    return ServerResponse.createBySuccess(map);
                }
            } catch (Exception e) {
                log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
            }
            getbadgeRanking(model, badge, student, map, start, rows);
            redisTemplate.opsForHash().put(KEY, FIELD, map);
            if(model.equals("3")){
                redisTemplate.expire(KEY, 7, TimeUnit.DAYS);
            }else{
                redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
            }
            return ServerResponse.createBySuccess(map);
        }
        if (certificate != null && !certificate.equals("")) {
            // 获取当前学生信息
            final String KEY = "simple_student_rank";
            final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + "certificate=" + certificate + "model=" + model + "queryType=" + queryType;
            try {
                Object object = redisTemplate.opsForHash().get(KEY, FIELD);
                if (object != null) {
                    map = (Map<String, Object>) object;
                    return ServerResponse.createBySuccess(map);
                }
            } catch (Exception e) {
                log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
            }
            getCertificaterRanking(model, certificate, student, map, start, rows);
            redisTemplate.opsForHash().put(KEY, FIELD, map);
            if(model.equals("3")){
                redisTemplate.expire(KEY, 7, TimeUnit.DAYS);
            }else{
                redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
            }
            return ServerResponse.createBySuccess(map);
        }
        if (worship != null && !worship.equals("")) {
            // 获取当前学生信息
            final String KEY = "simple_student_rank";
            final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + "worship=" + worship + "model=" + model + "queryType=" + queryType;
            try {
                Object object = redisTemplate.opsForHash().get(KEY, FIELD);
                if (object != null) {
                    map = (Map<String, Object>) object;
                    return ServerResponse.createBySuccess(map);
                }
            } catch (Exception e) {
                log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
            }
            getWorshipRanking(model, worship, student, map, start, rows);
            redisTemplate.opsForHash().put(KEY, FIELD, map);
            if(model.equals("3")){
                redisTemplate.expire(KEY, 7, TimeUnit.DAYS);
            }else{
                redisTemplate.expire(KEY, 3, TimeUnit.MINUTES);
            }
            return ServerResponse.createBySuccess(map);
        }
        return null;
    }

    //获取膜拜排行
    private void getWorshipRanking(String model, String worship, Student student, Map<String, Object> map, Integer start, Integer rows) {
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        Map<String, Object> studentMap = new HashMap<>();
        List<Map<String, Object>> getMap = new ArrayList<>();
        getClassAndSchool(student, map, levels, studentMap);
        Integer adminId = null;
        List<Integer> teacherIds=null;
        if(student.getTeacherId()!=null){
            adminId= simpleStudentMapper.selSchoolAdminId(student.getId());
            if(adminId==null){
                adminId=student.getTeacherId().intValue();
            }
            teacherIds= simpleTeacherMapper.getTeacherIdByAdminId(adminId);
        }
        Integer integer = simpleStudentMapper.selStudentNumberById(student.getClassId(), student.getTeacherId(), model, adminId,teacherIds,null);
        if (!model.equals("3")) {
            studentMap.put("number", integer);
        } else {
            studentMap.put("number", "99999+");
        }
        if (model.equals("3")) {
            map.put("total", (100 / rows) + 1);
        } else {
            if (integer > 100) {
                map.put("total", (100 / rows) + 1);
            } else {
                map.put("total", integer % rows > 0 ? integer / rows + 1 : integer / rows);
            }
        }
        if (start > 100) {
            return;
        }
        if (start + rows > 100) {
            rows = 100 - start;
        }
        List<Map<String, Object>> ranking = new ArrayList<>();
        List<Long> studentIds = new ArrayList<>();
        List<Map<String, Object>> totalRanking = new ArrayList<>();
        if (model.equals("1")) {
            //获取本班排行参与人数
            ranking = simpleStudentMapper.getRanking(student.getClassId(), student.getTeacherId(), null, worship, model, null, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(student.getClassId(), student.getTeacherId(), null, worship, model, null, 0, 100,teacherIds);
        } else if (model.equals("2")) {
            ranking = simpleStudentMapper.getRanking(null, student.getTeacherId(), null, worship, model, adminId, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(null, student.getTeacherId(), null, worship, model, adminId, 0, 100,teacherIds);
        } else if (model.equals("3")) {
            //获取本班排行参与人数
            ranking = simpleStudentMapper.getRanking(null, null, null, worship, model, null, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(null, null, null, worship, model, null, 0, 100,teacherIds);
        }
        for (int i = 0; i < ranking.size(); i++) {
            studentIds.add(Long.parseLong(ranking.get(i).get("id").toString()));
        }
        Map<Long, Map<String, Object>> longMapMap = ccieMapper.selCountCcieByStudents(studentIds);
        Map<Long, Map<String, Object>> longMapMap1 = simpleAwardMapper.selCountAwardByStudentIds(studentIds);
        for (Map<String, Object> stuMap : ranking) {
            if (longMapMap.get(stuMap.get("id")) != null) {
                stuMap.put("zs", longMapMap.get(stuMap.get("id")).get("ccie"));
            } else {
                stuMap.put("zs", 0);
            }
            if (longMapMap1.get(stuMap.get("id")) != null) {
                stuMap.put("xz", longMapMap1.get(stuMap.get("id")).get("award"));
            } else {
                stuMap.put("xz", 0);
            }
            Long gold1 = Math.round(Double.parseDouble(stuMap.get("gold").toString()));
            String level = getLevel(gold1.intValue(), levels);
            stuMap.put("childName", level);
            getMap.add(stuMap);
        }
        saveRanking(worship, student.getId(), map, getMap, studentMap, totalRanking, model, "worship");

    }

    //获取证书排行
    private void getCertificaterRanking(String model, String certificate, Student student, Map<String, Object> map, Integer start, Integer rows) {
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        Map<String, Object> studentMap = new HashMap<>();
        List<Map<String, Object>> getMap = new ArrayList<>();
        getClassAndSchool(student, map, levels, studentMap);
        Integer adminId=null;
        List<Integer> teacherIds=null;
        if(student.getTeacherId()!=null){
            adminId= simpleStudentMapper.selSchoolAdminId(student.getId());
            if(adminId==null){
                adminId=student.getTeacherId().intValue();
            }
            teacherIds= simpleTeacherMapper.getTeacherIdByAdminId(adminId);
        }
        Integer integer = simpleStudentMapper.selStudentNumberById(student.getClassId(), student.getTeacherId(), model, adminId,teacherIds,null);
        if (!model.equals("3")) {
            studentMap.put("number", integer);
        } else {
            studentMap.put("number", "99999+");
        }
        if (model.equals("3")) {
            map.put("total", (100 / rows) + 1);
        } else {
            if (integer > 100) {
                map.put("total", (100 / rows) + 1);
            } else {
                map.put("total", integer % rows > 0 ? integer / rows + 1 : integer / rows);
            }
        }
        if (start > 100) {
            return;
        }
        if (start + rows > 100) {
            rows = 100 - start;
        }
        List<Map<String, Object>> ccieRanking = new ArrayList<>();
        List<Map<String, Object>> ccieRanksing = new ArrayList<>();
        if (model.equals("1")) {
            ccieRanking = simpleStudentMapper.getCcieRanking(student.getClassId(), student.getTeacherId(), model, null, start, rows,teacherIds);
            ccieRanksing = simpleStudentMapper.getCcieRanking(student.getClassId(), student.getTeacherId(), model, null, 0, 100,teacherIds);
        }
        if (model.equals("2")) {
            ccieRanking = simpleStudentMapper.getCcieRanking(student.getClassId(), student.getTeacherId(), model, adminId, start, rows,teacherIds);
            ccieRanksing = simpleStudentMapper.getCcieRanking(student.getClassId(), student.getTeacherId(), model, adminId.intValue(), 0, 100,teacherIds);
        }
        if (model.equals("3")) {
            //获取本校排行参与人数
            ccieRanking = simpleStudentMapper.getCcieRanking(null, null, model, null, start, rows,teacherIds);
            ccieRanksing = simpleStudentMapper.getCcieRanking(null, null, model, null, 0, 100,teacherIds);

        }
        List<Long> studentIds = new ArrayList<>();
        for (int i = 0; i < ccieRanking.size(); i++) {
            studentIds.add(Long.parseLong(ccieRanking.get(i).get("id").toString()));
        }
        Map<Long, Map<String, Object>> longMapMap = worshipMapper.selCountWorshipByStudents(studentIds);
        Map<Long, Map<String, Object>> longMapMap1 = simpleAwardMapper.selCountAwardByStudentIds(studentIds);
        for (Map<String, Object> stuMap : ccieRanking) {
            if (longMapMap.get(stuMap.get("id")) != null) {
                stuMap.put("mb", longMapMap.get(stuMap.get("id")).get("worship"));
            } else {
                stuMap.put("mb", 0);
            }
            if (longMapMap1.get(stuMap.get("id")) != null) {
                stuMap.put("xz", longMapMap1.get(stuMap.get("id")).get("award"));
            } else {
                stuMap.put("xz", 0);
            }
            Long gold1 = Math.round(Double.parseDouble(stuMap.get("gold").toString()));
            String level = getLevel(gold1.intValue(), levels);
            stuMap.put("childName", level);
            getMap.add(stuMap);
        }
        saveRanking(certificate, student.getId(), map, getMap, studentMap, ccieRanksing, model, "ccie");
    }

    //获取勋章排行
    private void getbadgeRanking(String model, String badge, Student student, Map<String, Object> map, Integer start, Integer rows) {
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        Map<String, Object> studentMap = new HashMap<>();
        List<Map<String, Object>> getMap = new ArrayList<>();
        getClassAndSchool(student, map, levels, studentMap);
        Integer adminId=null;
        List<Integer> teacherIds=null;
        if(student.getTeacherId()!=null){
            adminId= simpleStudentMapper.selSchoolAdminId(student.getId());
            if(adminId==null){
                adminId=student.getTeacherId().intValue();
            }
            teacherIds= simpleTeacherMapper.getTeacherIdByAdminId(adminId);
        }
        Integer integer =0;
        if (!model.equals("3")) {
            integer= simpleStudentMapper.selStudentNumberById(student.getClassId(), student.getTeacherId(), model, adminId,teacherIds,null);
            studentMap.put("number", integer);
        } else {
            studentMap.put("number", "99999+");
        }
        if (start > 100) {
            return;
        }
        if (start + rows > 100) {
            rows = 100 - start;
        }
        if (model.equals("3")) {
            map.put("total", (100 / rows) + 1);
        } else {
            if (integer > 100) {
                map.put("total", (100 / rows) + 1);
            } else {
                map.put("total", integer % rows > 0 ? integer / rows + 1 : integer / rows);
            }
        }
        List<Map<String, Object>> medalRanking = new ArrayList<>();
        List<Map<String, Object>> medalRanksing = new ArrayList<>();
        if (model.equals("1")) {

            medalRanking = simpleStudentMapper.getMedalRanking(student.getClassId(), student.getTeacherId(), model, null, start, rows,teacherIds);
            medalRanksing = simpleStudentMapper.getMedalRanking(student.getClassId(), student.getTeacherId(), model, null, 0, 100,teacherIds);
        }
        if (model.equals("2")) {
            medalRanking = simpleStudentMapper.getMedalRanking(student.getClassId(), student.getTeacherId(), model, adminId, start, rows,teacherIds);
            medalRanksing = simpleStudentMapper.getMedalRanking(student.getClassId(), student.getTeacherId(), model, adminId, 0, 100,teacherIds);
        }
        if (model.equals("3")) {
            //获取本校排行参与人数
            medalRanking = simpleStudentMapper.getMedalRanking(null, null, model, null, start, rows,teacherIds);
            medalRanksing = simpleStudentMapper.getMedalRanking(null, null, model, null, 0, 100,teacherIds);
        }
        List<Long> studentIds = new ArrayList<>();
        for (int i = 0; i < medalRanking.size(); i++) {
            studentIds.add(Long.parseLong(medalRanking.get(i).get("id").toString()));
        }
        Map<Long, Map<String, Object>> longMapMap = worshipMapper.selCountWorshipByStudents(studentIds);
        Map<Long, Map<String, Object>> longMapMap1 = ccieMapper.selCountCcieByStudents(
                studentIds);
        for (Map<String, Object> stuMap : medalRanking) {
            if (longMapMap.get(stuMap.get("id")) != null) {
                stuMap.put("mb", longMapMap.get(stuMap.get("id")).get("worship"));
            } else {
                stuMap.put("mb", 0);
            }
            if (longMapMap1.get(stuMap.get("id")) != null) {
                stuMap.put("zs", longMapMap1.get(stuMap.get("id")).get("ccie"));
            } else {
                stuMap.put("zs", 0);
            }
            Long gold1 = Math.round(Double.parseDouble(stuMap.get("gold").toString()));
            String level = getLevel(gold1.intValue(), levels);
            stuMap.put("childName", level);
            getMap.add(stuMap);
        }
        saveRanking(badge, student.getId(), map, getMap, studentMap, medalRanksing, model, "award");
    }

    //获取金币排行
    private void getGoldRanking(String model, String gold, Student student, Map<String, Object> map, Integer start, Integer rows) {
        List<Map<String, Object>> levels = redisOpt.getAllLevel();
        Map<String, Object> studentMap = new HashMap<>();
        List<Map<String, Object>> getMap = new ArrayList<>();
        getClassAndSchool(student, map, levels, studentMap);
        Integer adminId=null;
        List<Integer> teacherIds=null;
        if(student.getTeacherId()!=null){
            adminId= simpleStudentMapper.selSchoolAdminId(student.getId());
            if(adminId==null){
                adminId=student.getTeacherId().intValue();
            }
            teacherIds= simpleTeacherMapper.getTeacherIdByAdminId(adminId);
        }
        Integer integer = simpleStudentMapper.selStudentNumberById(student.getClassId(), student.getTeacherId(), model, adminId,teacherIds,null);
        if (!model.equals("3")) {
            studentMap.put("number", integer);
        } else {
            studentMap.put("number", "99999+");
        }
        if (model.equals("3")) {
            map.put("total", (100 / rows) + 1);
        } else {
            if (integer > 100) {
                map.put("total", (100 / rows) + 1);
            } else {
                map.put("total", integer % rows > 0 ? integer / rows + 1 : integer / rows);
            }
        }
        if (start > 100) {
            return;
        }
        if (start + rows > 100) {
            rows = 100 - start;
        }
        List<Long> studentIds = new ArrayList<>();
        List<Map<String, Object>> ranking = new ArrayList<>();
        List<Map<String, Object>> totalRanking = new ArrayList<>();
        if (model.equals("1")) {
            //获取本班排行参与人数
            ranking = simpleStudentMapper.getRanking(student.getClassId(), student.getTeacherId(), gold, null, model, null, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(student.getClassId(), student.getTeacherId(), gold, null, model, null, 0, 100,teacherIds);
        } else if (model.equals("2")) {
            //获取本校排行参与人数
            ranking = simpleStudentMapper.getRanking(null, student.getTeacherId(), gold, null, model, adminId, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(null, student.getTeacherId(), gold, null, model, adminId, 0, 100,teacherIds);
        } else if (model.equals("3")) {
            //获取全国排行参与人数
            ranking = simpleStudentMapper.getRanking(null, null, gold, null, model, null, start, rows,teacherIds);
            totalRanking = simpleStudentMapper.getRanking(null, null, gold, null, model, null, 0, 100,teacherIds);
        }
        for (int i = 0; i < ranking.size(); i++) {
            studentIds.add(Long.parseLong(ranking.get(i).get("id").toString()));
        }
        Map<Long, Map<String, Object>> longMapMap = ccieMapper.selCountCcieByStudents(studentIds);
        Map<Long, Map<String, Object>> longMapMap1 = simpleAwardMapper.selCountAwardByStudentIds(studentIds);
        for (Map<String, Object> stuMap : ranking) {
            if (longMapMap.get(stuMap.get("id")) != null) {
                stuMap.put("zs", longMapMap.get(stuMap.get("id")).get("ccie"));
            } else {
                stuMap.put("zs", 0);
            }
            if (longMapMap1.get(stuMap.get("id")) != null) {
                stuMap.put("xz", longMapMap1.get(stuMap.get("id")).get("award"));
            } else {
                stuMap.put("xz", 0);
            }
            Long gold1 = Math.round(Double.parseDouble(stuMap.get("gold").toString()));
            String level = getLevel(gold1.intValue(), levels);
            stuMap.put("childName", level);
            getMap.add(stuMap);
        }
        saveRanking(gold, student.getId(), map, getMap, studentMap, totalRanking, model, "gold");
    }

    private void saveRanking(String order, Long id, Map<String, Object> map,
                             List<Map<String, Object>> getMap, Map<String, Object> studentMap,
                             List<Map<String, Object>> totalRnking, String model, String type) {
        Integer ranking = -1;
        worshipMapper.updState(id);
        for (int i = 0; i < totalRnking.size(); i++) {
            Long studentId = Long.parseLong(totalRnking.get(i).get("id").toString());
            if (id.equals(studentId)) {
                ranking = i + 1;
            }
        }
        if (type.equals("gold") || type.equals("worship")) {
            updateRanking(model, type, ranking, id);
        }
        if (ranking != -1) {
            studentMap.put("myRanking", ranking);
        } else {
            studentMap.put("myRanking", "未上榜");
        }
        map.put("myDate", studentMap);
        if (order.equals("2")) {
            map.put("phDate", getMap);
        } else {
            List<Map<String, Object>> getGolds = new ArrayList<>();
            for (int i = getMap.size() - 1; i >= 0; i--) {
                getGolds.add(getMap.get(i));
            }
            map.put("phDate", getGolds);
        }
    }

    private void updateRanking(String model, String type, Integer ranking, Long studentId) {
        if (ranking == -1) {
            ranking = 101;
        }
        Ranking rank = simpleRankingMapper.selByStudentId(studentId);
        if (model.equals("1")) {
            if (type.equals("gold")) {
                rank.setGoldClassRank(ranking);
            }
            if (type.equals("worship")) {
                rank.setWorshipClassRank(ranking);
            }
        }
        if (model.equals("2")) {
            if (type.equals("gold")) {
                rank.setGoldSchoolRank(ranking);
            }
            if (type.equals("worship")) {
                rank.setWorshipSchoolRank(ranking);
            }
        }
        if (model.equals("3")) {
            if (type.equals("gold")) {
                rank.setGoldCountryRank(ranking);
            }
            if (type.equals("worship")) {
                rank.setWorshipCountryRank(ranking);
            }
        }
        simpleRankingMapper.updateById(rank);
    }

    private void getClassAndSchool(Student student, Map<String, Object> map, List<Map<String, Object>> levels, Map<String, Object> studentMap) {
        studentMap.put("stuId", student.getId());
        Map<String, Object> classNameAndGoldAndMbAnd = simpleStudentMapper.getClassNameAndGoldAndMbAnd(student.getId());
        if (classNameAndGoldAndMbAnd.get("gold") == null) {
            studentMap.put("myGold", 0);
            studentMap.put("myChildName", "");
        } else {
            Long myGold = Math.round(Double.parseDouble(classNameAndGoldAndMbAnd.get("gold").toString()));
            studentMap.put("myGold", myGold);
            String level = getLevel(myGold.intValue(), levels);
            studentMap.put("myChildName", level);
        }

        studentMap.put("myMb", classNameAndGoldAndMbAnd.get("worship"));

        map.put("mySchool", student.getSchoolName());
        map.put("myClass", classNameAndGoldAndMbAnd.get("className"));
    }

    private String getLevel(Integer myGold, List<Map<String, Object>> levels) {
        String level = "";
        if (myGold >= 50) {
            int myrecord = 0;
            int myauto = 1;
            for (int i = 0; i < levels.size(); i++) {
                // 循环的当前等级分数
                int levelGold = (int) levels.get(i).get("gold");
                // 下一等级分数
                int xlevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");

                if (myGold >= myrecord && myGold < xlevelGold) {
                    level = levels.get(i).get("child_name").toString();
                    break;
                    // 等级循环完还没有确定等级 = 最高等级
                } else if (myauto == levels.size()) {
                    level = levels.get(i).get("child_name").toString();
                    break;
                }
                myrecord = levelGold;
                myauto++;
            }
            myrecord = 0;
            myauto = 0;
        }
        return level;
    }


    // 金币降序
    static class MapGoldDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("gold").toString());
            Integer v2 = Integer.valueOf(m2.get("gold").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }

    // 金币升序
    static class MapGoldAsc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("gold").toString());
            Integer v2 = Integer.valueOf(m2.get("gold").toString());
            if (v1 != null) {
                return v1.compareTo(v2);
            }
            return 0;
        }

    }

    // 勋章降序
    static class MapXzDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("xz").toString());
            Integer v2 = Integer.valueOf(m2.get("xz").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }

    // 勋章升序
    static class MapXzAsc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("xz").toString());
            Integer v2 = Integer.valueOf(m2.get("xz").toString());
            if (v1 != null) {
                return v1.compareTo(v2);
            }
            return 0;
        }

    }

    // 证书降序
    static class MapZsDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("zs").toString());
            Integer v2 = Integer.valueOf(m2.get("zs").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }

    // 证书升序
    static class MapZsAsc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("zs").toString());
            Integer v2 = Integer.valueOf(m2.get("zs").toString());
            if (v1 != null) {
                return v1.compareTo(v2);
            }
            return 0;
        }

    }

    // 膜拜降序
    static class MapMbDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("mb").toString());
            Integer v2 = Integer.valueOf(m2.get("mb").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }

    // 膜拜升序
    static class MapMbAsc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("mb").toString());
            Integer v2 = Integer.valueOf(m2.get("mb").toString());
            if (v1 != null) {
                return v1.compareTo(v2);
            }
            return 0;
        }

    }

    @Override
    public ServerResponse<Object> showCcie(HttpSession session, Integer model) {
        // 获取当前学生信息
        Student student = getStudent(session);
        Long studentId = student.getId();
        if (model == 10) {
            // 能力值测试
            model = 7;
        } else if (model == 11) {
            // 查询同步单词所有证书
            model = 1;
        } else {
            model += 10;
        }
        List<Map<String, Object>> listCcie = ccieMapper.selectAllCcieByStudentIdAndDate(studentId, model);
        if (listCcie.size() == 0) {
            return ServerResponse.createByErrorMessage("暂无证书！");
        }
        // 拼接证书内容
        StringBuilder sb = new StringBuilder();
        String[] time;

        MyCcieVo myCcieVo = new MyCcieVo();
        CcieVo ccieVo;
        // 用于判断测试名称是否已存在的中间变量值
        Map<String, Object> tempMap = new HashMap<>(16);
        List<Object> result = new ArrayList<>();
        List<CcieVo> ccieVos = new ArrayList<>();

        for (Map<String, Object> map : listCcie) {
            ccieVo = new CcieVo();
            time = map.get("time").toString().split("-");
            String ccieName = CcieUtil.getCcieName(Integer.valueOf(map.get("testType").toString()), Integer.valueOf(map.get("model").toString()));
            String modelAndTestType = CcieUtil.getModelAndTestType(Integer.valueOf(map.get("model").toString()), map.get("testName"));
            if (tempMap.get("testName") == null) {
                tempMap.put("testName", modelAndTestType);
                myCcieVo.setCcieTypeName(ccieName);
                if (model == 1) {
                    myCcieVo.setTestName(simpleCommonMethod.getCapacityStudyModel(Integer.valueOf(map.get("model").toString())));
                } else {
                    myCcieVo.setTestName(map.get("testName").toString());
                }
                myCcieVo.setGetTime(time[0] + "年" + time[1] + "月" + time[2] + "日");
            } else if (!Objects.equals(tempMap.get("testName"), modelAndTestType)) {
                result.add(myCcieVo);
                ccieVos = new ArrayList<>();
                myCcieVo = new MyCcieVo();
                myCcieVo.setCcieTypeName(ccieName);
                if (model == 1) {
                    myCcieVo.setTestName(simpleCommonMethod.getCapacityStudyModel(Integer.valueOf(map.get("model").toString())));
                } else {
                    myCcieVo.setTestName(map.get("testName").toString());
                }
                tempMap.put("testName", modelAndTestType);
                myCcieVo.setGetTime(time[0] + "年" + time[1] + "月" + time[2] + "日");
            }

            ccieVo.setCcieName(ccieName);

            sb.setLength(0);

            if ((map.get("point") != null) && (Integer.valueOf(map.get("point").toString()) > 0)) {
                ccieVo.setYear(time[0]);
                ccieVo.setMonth(time[1]);
                ccieVo.setDay(time[2]);
                ccieVo.setPetName(student.getPetName());
                ccieVo.setPoint(Integer.valueOf(map.get("point").toString()));
                ccieVo.setModelAndTestType(CcieUtil.getModelAndTestType(Integer.valueOf(map.get("model").toString()), map.get("testName")));
            } else {
                sb.append("于").append(time[0]).append("年").append(time[1]).append("月").append(time[2])
                        .append("日，在\"英语队长智能平台\"完成").append(map.get("testName"))
                        .append("。").append(map.get("encourageWord")).append("，特发此证，以资鼓励。");
            }

            ccieVo.setTestName(map.get("testName") == null ? "" : map.get("testName").toString());
            ccieVo.setContent(sb.toString());
            ccieVo.setCcie_no("证书编号：" + map.get("ccie_no"));
            ccieVo.setId(Long.valueOf(map.get("id").toString()));
            ccieVo.setStudent_id(Long.valueOf(map.get("student_id").toString()));
            ccieVo.setStudent_name(map.get("student_name").toString());
            ccieVo.setReadFlag(Objects.equals(map.get("readFlag").toString(), "1"));

            ccieVos.add(ccieVo);
            myCcieVo.setCcieVos(ccieVos);
        }
        result.add(myCcieVo);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> weekDurationIndexPageTwo(HttpSession session, int page, int rows, Integer yea) {
        // 获取当前学生信息
        Student student = getStudent(session);
        // 返回结果集
        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
        List<Map<String, Object>> result = new ArrayList<>(16);
        if (student.getFirstStudyTime() != null) {
            //获取当前日期
            Date currentDate = SimpleDatesUtil.getCurrentDate(new Date());
            //总周数
            int timeGapDays = SimpleDatesUtil.getTimeGapDays(student.getFirstStudyTime());
            //开始查找的位置
            int start = rows * (page - 1) + 1;
            //结束的位置
            int end = rows * page;
            for (int i = start; i < end; i++) {
                if (i <= timeGapDays + 1) {
                    Map<String, Object> map = new LinkedHashMap<>(16);
                    Date beforWeekDate = SimpleDatesUtil.getBeforeWeekDate(currentDate, i);
                    Date onMonday = SimpleDatesUtil.getOnMonday(beforWeekDate);
                    Date onSunday = SimpleDatesUtil.getOnSunday(beforWeekDate);
                    map.put("statsDate", s.format(onMonday));// 每周开始日期
                    map.put("endDate", s.format(onSunday));// 每周结束日期
                    int week = timeGapDays - i + 1;
                    map.put("week", "第" + week + "周"); // 第几周
                    //map.put("weekSort", (i+1));// 周,该字段用于排序
                    map.put("state", false); // 不是本周
                    // 查询循环周的总有效时长(m)
                    Integer duration = simpleDurationMapper.totalTime(SimpleDateUtil.formatYYYYMMDD(onMonday), SimpleDateUtil.formatYYYYMMDD(onSunday), student.getId());
                    String timeStrBySecond = TimeUtil.getTimeStrBySecond(duration);
                    int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 12600) * 100));
                    if (plan <= 100) {
                        map.put("pluSign", false);// 不显示+号
                        map.put("duration", plan);// 百分比, 用于展示进度条
                        if (duration == null || duration == 0) {
                            map.put("learnDate", "0小时");// 学习时长
                        } else {
                            map.put("learnDate", BigDecimalUtil.div(duration, 3600, 1) + "小时");// 学习时长
                        }
                    } else {
                        map.put("pluSign", true);// 显示+号
                        map.put("duration", 100);// 百分比, 用于展示进度条
                        map.put("learnDate", "3.5小时");// 学习时长
                    }
                    map.put("durationStr", timeStrBySecond);// 总有效时长格式: 00小时00分00秒

                    // 本周
                    if (i == 1) {
                        map.put("week", "本周"); // 第几周
                        map.put("state", true);
                    } else {
                        // 去掉没有数据的周
                        if (plan == 0) {
                            // brak--;
                            continue;
                        }
                    }
                    result.add(map);
                }
            }
            // 封装结果,返回
            Map totalMap = new HashMap(16);
            totalMap.put("page", page);
            if (result.size() < rows) {
                totalMap.put("total", 1);
            } else {
                totalMap.put("total", result.size() % rows == 0 ? result.size() / rows : result.size() / rows + 1);
            }
            totalMap.put("data", result);
            return ServerResponse.createBySuccess(totalMap);
        } else {
            return null;
        }

    }


    /**
     * 分页逻辑, 每次都让走981行分页判断里边, 不属于本页的跳过并总数据数-1
     *
     * @param session
     * @param page
     * @param rows
     * @param yea
     * @return
     */
    @Override
    public ServerResponse<Object> weekDurationIndexPage(HttpSession session, int page, int rows, Integer yea) {

        int protogenesis_page = page;

        // 获取当前学生信息
        Long studentId = studentIdBySession(session);

        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");

        // 返回结果集
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 学生在该系统学习过的年份 select date_format(learn_time, '%Y') from learn where student_id = 3155 and learn_time IS NOT NULL GROUP BY  date_format(learn_time, '%Y')
        // 年份是从大到小
        List<Integer> years = new ArrayList();
        // 全部
        if (yea == null || yea == 0) {
            years = learnMapper.selectLearn_times(studentId);
        } else {
            // 指定年
            years.add(yea);
        }

        // 获取当前时间所在年的周数
        int ye = WeekUtil.getWeekOfYear(new Date()) - 1;

        // 获取当前年份
        int we = SimpleDateUtil.DateYYYY();

        // 循环里边进行分页操作
        int auto = 0; // 0 定义自增变量
        int brak = 1; // 1 定义自增变量
        int return_ = 0; // 0保存输入当前页的数据, 1不保存

        // 记录有多少条数据, 计算总页数
        int total = 0;

        // 遍历学生学习过的年份-年份从大到小
        for (Integer year : years) {

            // 根据遍历的年份获取有多少周
            int yearMax = WeekUtil.getMaxWeekNumOfYear(year) - 1; // 如:52

            // 遍历一年中的每周
            int maxWeek = we == year ? ye : yearMax;
            for (int i = maxWeek; i >= 0; i--) {
                total++; // 用于计算总页数

                // 分页
                if (auto >= (page - 1) * rows && brak <= rows) {

                    // 用于封装一周的数据
                    Map<String, Object> map = new LinkedHashMap<String, Object>();

                    // 3.周的起始日期
                    Date weekStart = WeekUtil.getFirstDayOfWeek(year, i);
                    // 4.周的结尾日期
                    Date weekEnd = WeekUtil.getLastDayOfWeek(year, i);
                    int week = i + 1;
                    map.put("statsDate", s.format(weekStart));// 每周开始日期
                    map.put("endDate", s.format(weekEnd));// 每周结束日期
                    map.put("week", "第" + week + "周"); // 第几周
                    //map.put("weekSort", (i+1));// 周,该字段用于排序
                    map.put("state", false); // 不是本周
                    // 查询循环周的总有效时长(m)
                    Integer duration = simpleDurationMapper.totalTime(SimpleDateUtil.formatYYYYMMDD(weekStart), SimpleDateUtil.formatYYYYMMDD(weekEnd), studentId);
                    String timeStrBySecond = TimeUtil.getTimeStrBySecond(duration);
                    int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 12600) * 100));
                    if (plan <= 100) {
                        map.put("pluSign", false);// 不显示+号
                        map.put("duration", plan);// 百分比, 用于展示进度条
                        if (duration == null || duration == 0) {
                            map.put("learnDate", "0小时");// 学习时长
                        } else {
                            map.put("learnDate", BigDecimalUtil.div(duration, 3600, 1) + "小时");// 学习时长
                        }
                    } else {
                        map.put("pluSign", true);// 显示+号
                        map.put("duration", 100);// 百分比, 用于展示进度条
                        map.put("learnDate", "3.5小时");// 学习时长
                    }
                    map.put("durationStr", timeStrBySecond);// 总有效时长格式: 00小时00分00秒

                    // 本周
                    if (year == we && i == maxWeek) {
                        map.put("week", "本周"); // 第几周
                    } else {
                        // 去掉没有数据的周
                        if (plan == 0) {
                            // brak--;
                            total--;
                            continue;
                        }
                    }

                    brak++;

                    if (return_ == 0) {
                        result.add(map);
                    }

                    if (brak > rows) {
                        brak--;
                        return_ = 1;
                    }
                }
                auto++;
            }

        }

        // 封装结果,返回
        Map totalMap = new HashMap();
        totalMap.put("page", protogenesis_page);
        totalMap.put("total", total % rows == 0 ? total / rows : total / rows + 1);
        totalMap.put("data", result);

        return ServerResponse.createBySuccess(totalMap);
    }

    @Override
    public ServerResponse<Object> weekQuantityPageTwo(HttpSession session, int page, int rows, Integer yea) {
        // 获取当前学生信息
        Student student = super.getStudent(session);
        // 返回结果集
        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
        List<Map<String, Object>> result = new ArrayList<>(16);
        if (student.getFirstStudyTime() != null) {
            //获取当前日期
            Date currentDate = SimpleDatesUtil.getCurrentDate(new Date());
            //总周数
            int timeGapDays = SimpleDatesUtil.getTimeGapDays(student.getFirstStudyTime());
            //开始查找的位置
            int start = rows * (page - 1) + 1;
            //结束的位置
            int end = rows * page;

            // 每周数据量封装容器
            Map<String, Object> map;

            // 之前的周数据
            Date beforeWeekDate;
            // 每周周一的日期
            Date onMonday;
            // 每周周日的日期
            Date onSunday;
            for (int i = start; i < end; i++) {
                if (i <= timeGapDays + 1) {
                    map = new LinkedHashMap<>();
                    beforeWeekDate = SimpleDatesUtil.getBeforeWeekDate(currentDate, i);
                    onMonday = SimpleDatesUtil.getOnMonday(beforeWeekDate);

                    onSunday = SimpleDatesUtil.getOnSunday(beforeWeekDate);
                    // 每周开始日期
                    map.put("statsDate", s.format(onMonday));
                    // 每周结束日期
                    map.put("endDate", s.format(onSunday));
                    int week = timeGapDays - i + 1;
                    // 第几周
                    map.put("week", "第" + week + "周");
                    // 不是本周
                    map.put("state", false);
                    // 查询循环周的总有效时长(m)
                    Map<String, Object> countMap = learnMapper.mapWeekCountQuantity(SimpleDateUtil.formatYYYYMMDD(onMonday), SimpleDateUtil.formatYYYYMMDD(onSunday), student.getId());
                    Long duration = (Long) countMap.get("a") + (Long) countMap.get("b") + (Long) countMap.get("c") + (Long) countMap.get("d") +
                            (Long) countMap.get("e") + (Long) countMap.get("f") + (Long) countMap.get("g") + (Long) countMap.get("h") + (Long) countMap.get("i") + (Long) countMap.get("word")
                            + (Long) countMap.get("sentence") + (Long) countMap.get("text");

                    int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 200) * 100));
                    if (plan <= 100) {
                        // 进度条不显示+号
                        map.put("plusSign", false);
                        // 本分比进度条
                        map.put("duration", plan);
                    } else {
                        // 进度条显示+号
                        map.put("plusSign", true);
                        // 本分比进度条
                        map.put("duration", 100);
                    }

                    // 周总学习量
                    map.put("count", duration);
                    map.put("a", countMap.get("a"));
                    map.put("b", countMap.get("b"));
                    map.put("c", countMap.get("c"));
                    map.put("d", countMap.get("d"));
                    map.put("e", countMap.get("e"));
                    map.put("f", countMap.get("f"));
                    map.put("g", countMap.get("g"));
                    map.put("h", countMap.get("h"));
                    map.put("i", countMap.get("i"));
                    map.put("word", countMap.get("word"));
                    map.put("sentence", countMap.get("sentence"));
                    map.put("text", countMap.get("text"));

                    // 本周
                    if (i == 1) {
                        // 第几周
                        map.put("week", "本周");
                        map.put("state", true);
                    } else {
                        // 去掉没有数据的周
                        if (plan == 0) {
                            // brak--;
                            continue;
                        }
                    }
                    result.add(map);
                }
            }
            // 封装结果,返回
            Map<String, Object> totalMap = new HashMap<>(16);
            totalMap.put("page", page);
            if (result.size() < rows) {
                totalMap.put("total", 1);
            } else {
                totalMap.put("total", timeGapDays % rows == 0 ? timeGapDays / rows : timeGapDays / rows + 1);
            }
            totalMap.put("data", result);
            return ServerResponse.createBySuccess(totalMap);
        } else {
            return null;
        }
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getMedalInClass(HttpSession session) {
        Student student = getStudent(session);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Date today = new Date();
        List<Map<String, String>> medalMap = simpleAwardMapper.selectLatestMedalInClass(student, today);
        if (medalMap.size() == 0) {
            // 如果当前没有查询出已领取的记录，查找昨天的领取记录
            Date yesterday = new DateTime().plusDays(-1).toDate();
            medalMap = simpleAwardMapper.selectLatestMedalInClass(student, yesterday);
        }
        if (medalMap.size() > 0) {
            medalMap.forEach(map -> {
                        if (map != null && map.containsKey("nickName") && map.containsKey("medalName")) {
                            Map<String, Object> resMap = new HashMap<>();
                            resMap.put("nickName", map.get("nickName"));
                            resMap.put("textTheme", "同学获取了");
                            if (map.get("medalName") != null && map.get("medalName").contains("#")) {
                                String[] medalNames = map.get("medalName").split("#");
                                if (medalNames.length > 1) {
                                    resMap.put("medalName", Objects.equals(student.getSex(), 1) ? medalNames[1] : medalNames[0]);
                                } else {
                                    resMap.put("medalName", map.get("medalName"));
                                }
                            } else {
                                resMap.put("medalName", map.get("medalName"));
                            }
                            resMap.put("textEnding", "勋章");
                            resultList.add(resMap);
                        }
                    }
            );
        }
        return ServerResponse.createBySuccess(resultList);
    }

    /**
     * 进度排行榜
     *
     * @param session  存放着学生信息
     * @param haveUnit 已学单元 1=正序 2=倒叙
     * @param haveTest 已做测试 1=正序 2=倒叙
     * @param haveTime 学习时长 1=正序 2=倒叙
     * @return
     */
    @Override
    public ServerResponse<Object> durationSeniority(HttpSession session, Integer model, Integer haveUnit, Integer haveTest, Integer haveTime, Integer page, Integer rows) {
        // 获取当前学生信息
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 姓名
        String studentName = student.getStudentName();
        // 区
        String area = student.getArea();
        // 学校
        String school_name = student.getSchoolName();
        // 年级
        String grade = student.getGrade();
        // 班级
        String squad = student.getSquad();
        // 版本
        String version = student.getVersion();

        // 学段
        String study_paragraph = null;
        if ("七年级".equals(grade) || "八年级".equals(grade) || "九年级".equals(grade)) {
            study_paragraph = "初中";
        } else {
            study_paragraph = "高中";
        }

        // 响应数据
        Map<String, Object> result = new HashMap<>();
        List<SeniorityVo> list = null;

        // 默认本班排行
        if (model == 1 || model == null) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = simpleStudentUnitMapper.planSeniority(area, school_name, grade, squad, study_paragraph, haveUnit, version);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = simpleTestRecordMapper.planSeniority(area, school_name, grade, squad, study_paragraph, haveTest, version);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = simpleDurationMapper.planSeniority(area, school_name, grade, squad, study_paragraph, haveTime, version);
            }

            // 班级总参与人数
            result.put("atNumber", list.size());

            // 本校排行
        } else if (model == 2) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = simpleStudentUnitMapper.planSenioritySchool(area, school_name, study_paragraph, haveUnit, version);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = simpleTestRecordMapper.planSenioritySchool(area, school_name, study_paragraph, haveTest, version);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = simpleDurationMapper.planSenioritySchool(area, school_name, study_paragraph, haveTime, version);
            }

            // 学校总参与人数
            result.put("atNumber", simpleStudentMapper.schoolHeadcount(area, school_name, version));

            // 全国排行
        } else {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = simpleStudentUnitMapper.planSeniorityNationwide(study_paragraph, haveUnit, version);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = simpleTestRecordMapper.planSeniorityNationwide(study_paragraph, haveTest, version);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = simpleDurationMapper.planSeniorityNationwide(study_paragraph, haveTime, version);
            }

            // 初中/高中总参与人数
            result.put("atNumber", simpleStudentMapper.schoolHeadcountNationwide(study_paragraph, version));

        }

        int onset = (page - 1 * rows);
        int auto = 0;

        // 用于判断当前学生是否在排行里边有排名
        result.put("atRanking", "0");

        // 封装页面需要的数据, 所有学生数据
        for (SeniorityVo vo : list) {

            Long stuId = vo.getStudent_id();

            // 学生排行
            // 查询单元排行
            vo.setCountUnit(simpleStudentUnitMapper.onePlanSeniority(stuId));
            // 查询已做测试
            vo.setCountTest(simpleTestRecordMapper.onePlanSeniority(stuId));
            // 查询学习时长
            vo.setLearnDate(simpleDurationMapper.onePlanSeniority(stuId));

            // 当前学生排行
            auto++;
            if (stuId.equals(studentId)) {
                result.put("atRanking", auto);
                result.put("atCountUnit", vo.getCountUnit());
                result.put("atCountTest", vo.getCountTest());
                result.put("atLearnDate", vo.getLearnDate());
            }

        }

        result.put("page", page);
        result.put("total", list.size() % rows == 0 ? list.size() / rows : list.size() / rows + 1);

        // 对list分页
        page = (page - 1) * rows;
        rows = page + rows;
        List<SeniorityVo> resultList = list.subList(page > list.size() ? list.size() : page, list.size() < rows ? list.size() : rows);
        result.put("pageDate", resultList);

        // 当前学生排行等数据,
        if (result.get("atRanking").equals("0")) {
            result.put("atRanking", "未上榜");
            result.put("atCountUnit", simpleStudentUnitMapper.onePlanSeniority(studentId));
            result.put("atCountTest", simpleTestRecordMapper.onePlanSeniority(studentId));
            result.put("atLearnDate", simpleDurationMapper.onePlanSeniority(studentId));
        }

        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> courseStatisticsCountTrue(HttpSession session, Integer unitId, Integer model) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        Map map = new HashMap();

        Integer unitAllStudyModel = learnMapper.countUnitAllStudyModel(studentId, unitId, model);
        Integer count = null;
        if (model == 0) {
            count = unitMapper.countWordPicByUnitid(unitId.toString());
        } else if (model < 4 && model > 0) {
            count = unitMapper.countWordByUnitid(unitId.toString());
        } else {
            count = unitMapper.countSentenceByUnitid(unitId.toString());
        }

        // 根据单元id 查询课程单元名
        String name = unitMapper.getCourseNameByunitId(unitId);

        map.put("toLearn", unitAllStudyModel); // ./
        map.put("countUnit", count); // /.
        map.put("name", name);

        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取下拉年份
     *
     * @param studentId
     * @return
     */
    @Override
    public ServerResponse<Object> getYear(long studentId) {

        List year = new ArrayList<>();

        // 获取注册年份
        int startYear = simpleStudentMapper.getYear(studentId);
        // 当前年份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        int endYear = Integer.parseInt(sdf.format(date));
        year.add(endYear);

        while (true) {
            if (endYear <= startYear) {
                break;
            }
            year.add(--endYear);
        }

        return ServerResponse.createBySuccess(year);
    }

    @Override
    public ServerResponse<Object> payCardIndex(long studentId) {
        Map student = simpleStudentMapper.getStudentAccountTime(studentId);
        return ServerResponse.createBySuccess(student);
    }

    @Override
    public ServerResponse<Object> getPayCard(long studentId, int page, int rows) {
        Map resut = new HashMap();
        PageHelper.startPage(page, rows);
        List<Map> map = simplePayLogMapper.selectPayLogDataByStudentId(studentId);
        for (Map ma : map) {
            ma.put("card_dateStr", ma.get("card_date") + "天");
        }
        resut.put("data", map);
        PageInfo pageInfo = new PageInfo(map);
        resut.put("page", page);
        resut.put("total", pageInfo.getPages());

        return ServerResponse.createBySuccess(resut);
    }

    @Override
    public ServerResponse updateCcie(HttpSession session) {
        Long studentId = getStudentId(session);
        ccieMapper.updateReadFlag(studentId, 1);
        return ServerResponse.createBySuccess();
    }

    /**
     * 使用充值卡
     *
     * @param studentId
     * @param card
     * @return
     */
    @Override
    public ServerResponse<Object> postPayCard(long studentId, String card) throws ParseException {
        Student student = simpleStudentMapper.selectByPrimaryKey(studentId);
        // 学生账号
        String account = student.getAccount();
        // 查询学生账号到期时间
        Date parse = student.getAccountTime();

        // 1.修改充值卡状态
        int up = simplePayCardMapper.updateCreateUserByCardNo(card, account, new Date());
        if (up != 1) {
            Long pc = simplePayCardMapper.getIdByCardNo(card);
            if (pc == null) {
                return ServerResponse.createByErrorMessage("卡号不存在");
            } else {
                return ServerResponse.createByErrorMessage("该卡号已被使用");
            }
        }

        // 查询充值卡时间
        int cardNo = simplePayCardMapper.getValidityTimeByCard(card);

        // 充值后账号到期日期
        String format = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        // 到期时间小于当前日期
        if (parse.getTime() < System.currentTimeMillis()) {
            // 从当前日期添加90天
            c.add(Calendar.DAY_OF_MONTH, cardNo);
            format = sf.format(c.getTime());
            //System.out.println("从当前日期增加90天后日期 ： " + format);
        } else {
            // 在之前基础上加90天
            c.setTime(parse);   // 日期
            c.add(Calendar.DATE, cardNo); //日期加100天
            format = sf.format(c.getTime());
            //System.out.println("在之前基础上加100天" + format);
        }

        if (up == 1) {
            // 2.把充值卡时间修改到学生表
            int s = simpleStudentMapper.updateAccountTimeByStudentId(studentId, format);
            // 3.记录充值记录表
            if (s == 1) {
                // 记录充值信息
                PayLog pa = new PayLog();
                Date date = new Date();
                pa.setStudentId(studentId);
                pa.setCardDate(cardNo);
                pa.setCardNo(card);
                pa.setFoundDate(date);
                pa.setRecharge(date);
                simplePayLogMapper.insert(pa);
                return ServerResponse.createBySuccess("充值成功");
            }
        }

        return ServerResponse.createByErrorMessage("充值失败");
    }

    @Override
    public void updateFakeData() {
        List<Student> students = simpleStudentMapper.selectListByTeacherId(270);
        int size = students.size();
        Student student;
        for (int i = 0; i < size; i++) {
            student = students.get(i);
            if (i == 0) {
                // 学生应该有的证书个数
                updateCcieCount(student);
            } else {
                Student student1 = simpleStudentMapper.selectById(students.get(i - 1).getId());
                // 上个学生金币总数
                double totalGold = BigDecimalUtil.add(student1.getSystemGold(), student1.getOfflineGold());

                if (student.getSystemGold() != BigDecimalUtil.sub(totalGold, 500)) {
                    student.setSystemGold(BigDecimalUtil.sub(totalGold, 500));
                    simpleStudentMapper.updateById(student);
                }
                updateCcieCount(student);
            }
        }
    }

    @Override
    public ServerResponse needViewCount(HttpSession session) {
        Student student = super.getStudent(session);

        // 留言反馈未阅读信息数量
        int feedBackCount = unReadFeedBackCount(student.getId());
        // 可抽奖次数
        int lotteryCount = lotteryCount(student);

        Map<String, Integer> map = new HashMap<>(16);

        map.put("count",  feedBackCount + lotteryCount);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 可抽奖次数
     *
     * @param student
     * @return
     */
    private int lotteryCount(Student student) {
        Integer energy = student.getEnergy();
        if (energy != null) {
           return energy / 5;
        }
        return 0;
    }

    private void updateCcieCount(Student student) {
        // 学生应该有的证书个数
        int shouldHaveCount = (int) ((BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold())) * 0.17);
        // 学生当前的证书个数
        int count = ccieMapper.countByStudentId(student.getId());
        if (count < shouldHaveCount) {
            log.error("nickname=[{}]", student.getNickname());
            // 插入新证书补足数量
            ccieMapper.insertWithNeedCount(student.getId(), shouldHaveCount - count);
        }
    }
}

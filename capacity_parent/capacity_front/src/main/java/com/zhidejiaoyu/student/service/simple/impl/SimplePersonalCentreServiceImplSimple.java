package com.zhidejiaoyu.student.service.simple.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.Vo.SeniorityVo;
import com.zhidejiaoyu.common.Vo.simple.ccieVo.CcieVo;
import com.zhidejiaoyu.common.Vo.simple.ccieVo.MyCcieVo;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.dto.rank.RankDto;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDateUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleDatesUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleTimeUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.SimpleWeekUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.service.simple.SimplePersonalCentreServiceSimple;
import com.zhidejiaoyu.student.utils.simple.SimpleCcieUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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

    @Autowired
    private RankOpt rankOpt;

    @Override
    public ServerResponse<Object> personalIndex(HttpSession session) {
        Map<String, Object> map = new HashMap<>(16);
        // 获取当前学生信息
        Student student = getStudent(session);
        Long id = student.getId();
        map.put("name", student.getStudentName());
        map.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
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
        int ye = SimpleWeekUtil.getWeekOfYear(new Date()) - 1;

        // 获取当前年份
        int we = SimpleDateUtil.DateYYYY();

        // 遍历学生学习过的年份
        for (Integer year : years) {

            // 根据遍历的年份获取有多少周
            int yearMax = SimpleWeekUtil.getMaxWeekNumOfYear(year) - 1; // 如:52

            // 遍历一年中的每周
            for (int i = 0; i <= yearMax; i++) {
                // 用于封装一周的数据
                Map<String, Object> map = new LinkedHashMap<String, Object>();

                // 3.周的起始日期
                Date weekStart = SimpleWeekUtil.getFirstDayOfWeek(year, i);
                // 4.周的结尾日期
                Date weekEnd = SimpleWeekUtil.getLastDayOfWeek(year, i);
                map.put("statsDate", s.format(weekStart));// 每周开始日期
                map.put("endDate", s.format(weekEnd));// 每周结束日期
                map.put("week", "第" + (i + 1) + "周"); // 第几周
                //map.put("weekSort", (i+1));// 周,该字段用于排序
                map.put("state", false); // 不是本周
                // 查询循环周的总有效时长(m)
                Integer duration = simpleDurationMapper.totalTime(SimpleDateUtil.formatYYYYMMDD(weekStart), SimpleDateUtil.formatYYYYMMDD(weekEnd), studentId);
                String timeStrBySecond = SimpleTimeUtil.getTimeStrBySecond(duration);
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
        int ye = SimpleWeekUtil.getWeekOfYear(new Date()) - 1;

        // 获取当前时间所在周
        int we = SimpleDateUtil.DateYYYY();

        for (Integer year : years) {

            // 1.根据年获取有多少周
            int yearMax = SimpleWeekUtil.getMaxWeekNumOfYear(year) - 1;

            for (int i = 0; i <= yearMax; i++) {
                // 封装返回结果集
                Map<String, Object> map = new LinkedHashMap<String, Object>();

                // 3.周的起始日期
                Date weekStart = SimpleWeekUtil.getFirstDayOfWeek(year, i);
                // 4.周的结尾日期
                Date weekEnd = SimpleWeekUtil.getLastDayOfWeek(year, i);

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


    @Override
    public ServerResponse<Object> rankingSeniority(HttpSession session, RankDto rankDto) {
        Student student = super.getStudent(session);

        switch (rankDto.getType()) {
            case 1:
                // 金币排行
                return ServerResponse.createBySuccess(this.getGoldRank(student, rankDto));
            case 2:
                // 勋章排行
                return ServerResponse.createBySuccess(this.getMedalRank(student, rankDto));
            case 3:
                // 证书排行
                return ServerResponse.createBySuccess(this.getCcieRank(student, rankDto));
            case 4:
                // 膜拜排行
                return ServerResponse.createBySuccess(this.getWorshipRank(student, rankDto));
            default:
                return ServerResponse.createBySuccess(new HashMap<>(16));
        }
    }

    /**
     * 获取膜拜排行
     *
     * @param student
     * @param rankDto
     * @return
     */
    private Map<String, Object> getWorshipRank(Student student, RankDto rankDto) {
        // 缓存中相对应的 key 值
        String key;
        if (rankDto.getModel() == 1) {
            key = RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId();
        } else if (rankDto.getModel() == 2) {
            key = RankKeysConst.SCHOOL_WORSHIP_RANK + TeacherInfoUtil.getSchoolAdminId(student);
        } else {
            key = RankKeysConst.COUNTRY_WORSHIP_RANK;
        }

        return packageResultMap(student, rankDto, key);
    }

    /**
     * 获取证书排行
     *
     * @param student
     * @param rankDto
     * @return
     */
    private Map<String, Object> getCcieRank(Student student, RankDto rankDto) {
        // 缓存中相对应的 key 值
        String key;
        if (rankDto.getModel() == 1) {
            key = RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId();
        } else if (rankDto.getModel() == 2) {
            key = RankKeysConst.SCHOOL_CCIE_RANK + TeacherInfoUtil.getSchoolAdminId(student);
        } else {
            key = RankKeysConst.COUNTRY_CCIE_RANK;
        }

        return packageResultMap(student, rankDto, key);
    }

    /**
     * 获取勋章排行
     *
     * @param student
     * @param rankDto
     * @return
     */
    private Map<String, Object> getMedalRank(Student student, RankDto rankDto) {
        // 缓存中相对应的 key 值
        String key;
        if (rankDto.getModel() == 1) {
            key = RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId();
        } else if (rankDto.getModel() == 2) {
            key = RankKeysConst.SCHOOL_MEDAL_RANK + TeacherInfoUtil.getSchoolAdminId(student);
        } else {
            key = RankKeysConst.COUNTRY_MEDAL_RANK;
        }

        worshipMapper.updateState(student.getId());

        return packageResultMap(student, rankDto, key);
    }

    /**
     * 获取金币排行
     *
     * @param student
     * @param rankDto
     * @return
     */
    private Map<String, Object> getGoldRank(Student student, RankDto rankDto) {
        // 缓存中相对应的 key 值
        String key;
        if (rankDto.getModel() == 1) {
            key = RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId();
        } else if (rankDto.getModel() == 2) {
            key = RankKeysConst.SCHOOL_GOLD_RANK + TeacherInfoUtil.getSchoolAdminId(student);
        } else {
            key = RankKeysConst.COUNTRY_GOLD_RANK;
        }

        return packageResultMap(student, rankDto, key);
    }

    /**
     * 封装响应结果
     *
     * @param student
     * @param rankDto
     * @param key   缓存中相对应的 key 值
     * @return
     */
    private Map<String, Object> packageResultMap(Student student, RankDto rankDto, String key) {
        // 参与排行的人数
        long number;
        // 排行总页数
        long totalPages;
        // 当前学生的个人数据
        Map<String, Object> myData = packageMyData(student, rankDto, key);
        Integer rows = rankDto.getRows();
        if (rankDto.getModel() == 3) {
            // 全国排行
            myData.put("number", "99999+");
            totalPages = 100 / rows + 1;
        } else {
            // 班级排行、全校排行
            number = rankOpt.getMemberSize(key);
            totalPages = getTotalPages(rows, number);
            myData.put("number", number);
        }

        Map<String, Object> resultMap = new HashMap<>(16);
        resultMap.put("mySchool", student.getSchoolName());
        resultMap.put("myClass", simpleStudentMapper.selectClassNameByStudentId(student.getId()));
        resultMap.put("total", totalPages);
        resultMap.put("page", rankDto.getPage());
        resultMap.put("phDate", packagePhData(rankDto, key, rows));
        resultMap.put("myDate", myData);

        return resultMap;
    }

    /**
     * 排行榜列表数据
     *
     * @param rankDto
     * @param key
     * @param rows
     */
    private List<Map<String, Object>> packagePhData(RankDto rankDto, String key, Integer rows) {
        List<Map<String, Object>> phData = new ArrayList<>();

        Integer page = rankDto.getPage();
        long startIndex = (long) page == 1 ? 0 : (page - 1) * rows;
        List<Long> studentIds = rankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, startIndex + rows);
        if (studentIds.size() > 0) {
            studentIds.forEach(id -> {
                Map<String, Object> dataMap = new HashMap<>(16);
                Student student1 = simpleStudentMapper.selectById(id);
                if (student1 == null) {
                    rankOpt.deleteMember(key, id);
                    log.warn("未查询到 id 为[{}]的学生！", id);
                    return;
                }
                dataMap.put("zs", rankOpt.getScore(RankKeysConst.COUNTRY_CCIE_RANK, id) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_CCIE_RANK, id));
                dataMap.put("xz", rankOpt.getScore(RankKeysConst.COUNTRY_MEDAL_RANK, id) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_MEDAL_RANK, id));
                dataMap.put("mb", rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, id) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, id));
                dataMap.put("area", student1.getArea());
                dataMap.put("city", student1.getCity());
                dataMap.put("province", student1.getProvince());
                dataMap.put("headUrl", AliyunInfoConst.host + student1.getHeadUrl());
                dataMap.put("studentName", student1.getNickname());
                dataMap.put("id", id);
                dataMap.put("gold", Math.round(BigDecimalUtil.add(student1.getOfflineGold(), student1.getSystemGold())));
                dataMap.put("childName", getLevel((int) BigDecimalUtil.add(student1.getOfflineGold(), student1.getSystemGold()), redisOpt.getAllLevel()));
                phData.add(dataMap);
            });
        }
        return phData;
    }

    private Map<String, Object> packageMyData(Student student, RankDto rankDto, String key) {
        Map<String, Object> studentMap = new HashMap<>(16);
        double totalGold = BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        // 我的排名
        Object myRanking = getMyRanking(student, key);
        studentMap.put("stuId", student.getId());
        studentMap.put("myGold", Math.round(totalGold));
        studentMap.put("myChildName", getLevel((int) Math.round(totalGold), redisOpt.getAllLevel()));
        studentMap.put("myMb", rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId()) == -1 ? 0 : rankOpt.getScore(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId()));
        studentMap.put("myRanking", myRanking);

        // 如果是学生点击排行按钮进入排行，其初始页为0，后台强制其跳转到当前学生所在页
        if (rankDto.getPage() == 0) {
            if (myRanking instanceof String) {
                rankDto.setPage(1);
            } else if (myRanking instanceof Long) {
                long ranking = Long.parseLong(myRanking.toString());
                rankDto.setPage((int) (ranking / rankDto.getRows() + (ranking % rankDto.getRows() == 0 ? 0 : 1)));
            } else {
                rankDto.setPage(1);
            }
        }

        return studentMap;
    }

    /**
     * 金币排行中获取我的名次
     *
     * @param student
     */
    private Object getMyRanking(Student student, String key) {
        long rank = rankOpt.getRank(key, student.getId());
        return rank > 99 || rank == -1 ? "未上榜" : rank + 1;
    }

    private long getTotalPages(Integer rows, long number) {
        long totalPages;
        if (number > 100) {
            totalPages = 100 / rows + 1;
        } else {
            totalPages = number % rows > 0 ? number / rows + 1 : number / rows;
        }
        return totalPages;
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
        }
        return level;
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
            String ccieName = SimpleCcieUtil.getCcieName(Integer.valueOf(map.get("testType").toString()), Integer.valueOf(map.get("model").toString()));
            String modelAndTestType = SimpleCcieUtil.getModelAndTestType(Integer.valueOf(map.get("model").toString()), map.get("testName"));
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
                ccieVo.setModelAndTestType(SimpleCcieUtil.getModelAndTestType(Integer.valueOf(map.get("model").toString()), map.get("testName")));
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
                    String timeStrBySecond = SimpleTimeUtil.getTimeStrBySecond(duration);
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
            medalMap.parallelStream().filter(map -> map != null && map.containsKey("nickName") && map.containsKey("medalName")).forEach(map -> {
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
        String study_paragraph;
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
        Integer count;
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
    public ServerResponse<Object> postPayCard(long studentId, String card) {
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
        String format;
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

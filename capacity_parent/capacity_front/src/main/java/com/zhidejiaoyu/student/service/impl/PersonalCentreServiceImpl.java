package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.SeniorityVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.AwardUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.TimeUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.WeekUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.PersonalCentreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 消息中心
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@Service
@Transactional
public class PersonalCentreServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements PersonalCentreService {

    @Value("${domain}")
    private String domain;

    /**
     * 消息中心mapper接口
     */
    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private WorshipMapper worshipMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private PayCardMapper payCardMapper;

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private SyntheticRewardsListMapper syntheticRewardsListMapper;

    @Autowired
    private StudentSkinMapper studentSkinMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ServerResponse<Object> personalIndex(HttpSession session) {
        Map<String, Object> map = new HashMap<>(16);
        // 获取当前学生信息
        Student student = getStudent(session);
        Long id = student.getId();
        map.put("name", student.getStudentName());
        map.put("headUrl", student.getHeadUrl());

        // 判断有哪些模块有未处理的信息
        redPoint(map, id);

        // 我的总金币
        Double myGoldD = studentMapper.myGold(id);
        BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
        int myGold = Integer.parseInt(mybd.toString());
        // 根据金币获取等级名
        String levelName = learnMapper.getLevelNameByGold(myGold);
        map.put("levelName", levelName);

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
    }

    private void myFeedBack(Map<String, Object> map, Long studentId) {
        MessageBoardExample messageBoardExample = new MessageBoardExample();
        MessageBoardExample.Criteria criteria = messageBoardExample.createCriteria();
        criteria.andReadFlagEqualTo(3).andStudentIdEqualTo(studentId);
        int i = messageBoardMapper.countByExample(messageBoardExample);
        if (i == 0) {
            map.put("messageRead", false);
        } else {
            map.put("messageRead", true);
        }
    }

    private void myCcie(Map<String, Object> map, Long studentId) {
        CcieExample ccieExample = new CcieExample();
        CcieExample.Criteria criteria = ccieExample.createCriteria();
        criteria.andStudentIdEqualTo(studentId).andReadFlagEqualTo(0);
        int i = ccieMapper.countByExample(ccieExample);
        if (i == 0) {
            map.put("ccieRead", false);
        } else {
            map.put("ccieRead", true);
        }
    }

    private void unReadNews(Map<String, Object> map, Long studentId) {
        NewsExample example = new NewsExample();
        example.createCriteria().andStudentidEqualTo(studentId).andReadEqualTo(2);
        int i = newsMapper.countByExample(example);
        if (i == 0) {
            // 不在未读消息
            map.put("read", false);
        } else {
            // 存在未读消息
            map.put("read", true);
        }
    }

    /**
     * 消息通知
     */
    @Override
    public ServerResponse<Object> newsCentre(HttpSession session) {
        // 获取当前学生信息
        Long id = StudentIdBySession(session);
        List<News> list = newsMapper.selectByStudentId(id);
        return ServerResponse.createBySuccess(list);
    }


    /**
     * 1.删除
     * 2.根据通知id标记为已读
     * 3.全部标记为已读
     */
    @Override
    public ServerResponse<String> newsupdate(HttpSession session, Integer state, Integer[] id) {
        // 获取当前学生信息
        Long studentId = StudentIdBySession(session);

        // 1.删除(根据消息通知id)
        if (state == 1) {
            for (Integer intId : id) {
                newsMapper.deleteByPrimaryKey(Long.valueOf(intId));
            }
            return ServerResponse.createBySuccessMessage("删除完成");
        } else if (state == 2) {
            // 2.根据通知id标记为已读(根据消息通知id)
            News ne = new News();
            for (Integer intId : id) {
                ne.setId(Long.valueOf(intId));
                ne.setRead(1);
                newsMapper.updateByPrimaryKeySelective(ne);
            }
            return ServerResponse.createBySuccessMessage("已标记为已读");
        } else if (state == 3) {
            // 3.全部标记为已读(根据学生id)
            newsMapper.updateByRead(studentId);
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
        Long studentId = StudentIdBySession(session);

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
        int we = DateUtil.DateYYYY();

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
                Integer duration = durationMapper.totalTime(DateUtil.formatYYYYMMDD(weekStart), DateUtil.formatYYYYMMDD(weekEnd), studentId);
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
    private long StudentIdBySession(HttpSession session) {
        // 获取当前学生信息
        return getStudentId(session);
    }

    /**
     * 每周总学习量
     * 每周六个模块个个的学习量
     */
    @Override
    public ServerResponse<Object> weekQuantity(HttpSession session) {
        // 获取当前学生id
        Long studentId = StudentIdBySession(session);

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
        int we = DateUtil.DateYYYY();

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
                Map<String, Object> countMap = learnMapper.mapWeekCountQuantity(DateUtil.formatYYYYMMDD(weekStart), DateUtil.formatYYYYMMDD(weekEnd), studentId);
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
    public ServerResponse<Object> CourseStatistics(HttpSession session, int page, int rows) {
        // 获取当前学生id
        Long studentId = StudentIdBySession(session);

        // 返回数据集包含分页
        Map resultMapAll = new HashMap();

        // 返回数据集
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // Map = id, course_name, version, label, learn_count
        // 获取学生学习过得课程id,课程名,版本,标签,课程学习遍数
        PageHelper.startPage(page, rows);
        List<Map<String, Object>> courses = courseMapper.courseLearnInfo(studentId);

        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(courses);
        resultMapAll.put("page", page);
        resultMapAll.put("total", pageInfo.getPages());
        // 循环学过的所有课程
        for (Map map : courses) {
            // 封装一个课程的数据
            Map<String, Object> resultMap = new HashMap<String, Object>();

            // 当前课程id
            Long course_id = (Long) map.get("id");

            resultMap.put("id", course_id);// 课程id
            resultMap.put("courseName", map.get("course_name"));// 带年级的课程名
            resultMap.put("versionLabel", map.get("version") + "-" + map.get("label"));// 不带年级的课程名
            resultMap.put("learnCount", map.get("learn_count"));// 课程学习的第几遍

            // 获取课程单词总数
            Integer countVocabulary = vocabularyMapper.courseCountVocabulary(course_id);
            // 获取课程例句总数
            Integer countSentence = sentenceMapper.courseCountSentence(course_id);
            // 获取单词图鉴总数
            Integer picCount = vocabularyMapper.picByCourseId(course_id);
            resultMap.put("countVocabulary", countVocabulary); // 课程单词总数
            resultMap.put("countSentence", countSentence); // 课程例句总数
            resultMap.put("picCount", picCount); // 单词图鉴总数

            // 获取课程下所有单元id
            List<Map<String, Object>> allUnit = unitMapper.allUnit(course_id.intValue());
            // 当前课程单词模块所学单元id
            Integer wordMax = studentUnitMapper.maxUnitIdByWordByCourseIdByStudentIdBy(course_id, studentId);
            // 当前课程例句模块所学单元id
            Integer sentenceMax = studentUnitMapper.maxUnitIdBySentenceByCourseIdByStudentIdBy(course_id, studentId);


            // 根据课程获取六个模块的学习量
            for (int i = 0; i < 7; i++) {
                Map<String, Object> m = new HashMap<String, Object>();
                String countStr = learnMapper.countCourseStudyModel(studentId, course_id, i);
                Integer count = 0;
                if (StringUtils.isNotBlank(countStr)) {
                    count = Integer.parseInt(countStr);
                }
                m.put("studyModel", count); // 六个模块个个的学习量

                if (i == 0) {
                    // 计算单词模块百分比值
                    int planVocabulary = ((int) (BigDecimalUtil.div(count, picCount) * 100));
                    m.put("duration", planVocabulary);// 单词图鉴模块百分比值
                } else if (i < 4 && i > 0) {
                    // 计算单词模块百分比值
                    int planVocabulary = ((int) (BigDecimalUtil.div(count, countVocabulary) * 100));
                    m.put("duration", planVocabulary);// 单词模块百分比值
                } else {
                    // 计算例句模块百分比值
                    int planSentence = ((int) (BigDecimalUtil.div(count, countSentence) * 100));
                    m.put("duration", planSentence);// 例句模块百分比值
                }

                // 用于封装所有单元的状态
                List<Map> li = new ArrayList<>();

                int a = 0;
                for (int aa = 0; aa < allUnit.size(); aa++) {
                    Map mm = new HashMap();

                    // 每个课程正在学的单元id
                    Integer learnUnitId = 0;
                    if (i < 4) {
                        learnUnitId = wordMax;
                    } else {
                        learnUnitId = sentenceMax;
                    }

                    Map<String, Object> mapUnit = allUnit.get(aa);
                    Integer id = ((Long) mapUnit.get("id")).intValue(); // 单元id
                    Object unitName = mapUnit.get("unit_name"); // 单元名
                    mm.put("unitId", id);
                    mm.put("unitName", unitName);

                    Integer integer = testRecordMapper.selectUnitTestMaxPointByStudyModel(studentId, id.longValue(), i);
                    // 学过的单元
                    if (a == 0) {
                        if (integer != null && integer >= 80) {
                            // 状态1 单元测试通过
                            mm.put("colour", 1);
                        } else {
                            // 状态2 单元测试未通过
                            mm.put("colour", 2);
                        }
                    }

                    // 未学单元
                    if (a == 3) {
                        // 状态5 空白
                        mm.put("colour", 5);
                    }

                    // 正在学的单元
                    if (id.equals(learnUnitId)) {
                        // 单元下单词图鉴总单词数量
                        // 单元总单词数量
                        // 例句总例句数量
                        Integer countAll = vocabularyMapper.countByModel(id, i);

                        // 单元单词图鉴已学数量
                        // 单元已学数量
                        // 单元例句已学数量
                        Integer myCount = learnMapper.countHaveToLearnByModelAll(studentId, id, i);
                        if (integer == null) {
                            mm.put("colour", 3); // 还未做单元测试3
                        }
                        if (!countAll.equals(myCount) && myCount < countAll) {
                            mm.put("colour", 4); // 正在学状态4
                        }

                        a = 3;
                    }

                    if (!mm.containsKey("colour")) {
                        mm.put("colour", 3); // 还未做单元测试3
                    }
                    li.add(mm);
                }
                // 课程所有单元状态
                m.put("unitList", li);
                // key: 0=单词图鉴, 1=慧记忆, 2=慧听写, 3=慧默写, 4=例句听力, 5=例句翻译, 6=例句默写
                resultMap.put(i + "", m);
            }
            result.add(resultMap);
        }

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
        Long studentId = StudentIdBySession(session);

        // 获取单元id
        Map<String, Object> mapDate = unitMapper.getUnitIdByCourseIdAndUnitNumber(courseId, unitNumber);
        Long unitId = (Long) mapDate.get("id");

        // 课程名方式1
        result.put("courseNameOne", mapDate.get("course_name"));
        // 课程名方式2
        result.put("courseNameTwo", mapDate.get("version") + "-" + mapDate.get("label"));

        // 查询单元下边有多少已学单词
        Integer learnWord = learnMapper.countByWord(studentId, unitId, model);
        // 已学单词/例句
        result.put("yet", learnWord);

        if (model < 4) {
            // 查询单元下边有多少单词
            Long countByWord = unitVocabularyMapper.selectWordCountByUnitId(unitId);
            // 单元总单词/例句量
            result.put("count", countByWord);
        } else {
            // 查询单元下边有多少例句
            int countBySentence = unitSentenceMapper.countByUnitId(unitId);
            // 单元总单词/例句量
            result.put("count", countBySentence);
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

        final String KEY = "capacity_student_rank";
        final String FIELD = "condition:" + student.getId() + ":" + page + ":" + rows + ":" + golds + ":" + badges + ":" + certificates + ":" + worships + ":" + model + ":" + queryType;
        try {
            Object object = redisTemplate.opsForHash().get(KEY, FIELD);
            if (object != null) {
                result = (Map<String, Object>) object;
                return ServerResponse.createBySuccess(result);
            }
        } catch (Exception e) {
            log.error("排行榜类型转换错误，学生[{}]-[{}]排行榜类型转换错误，error=[{}]", student.getId(), student.getStudentName(), e.getMessage());
        }

        // 教师id
        Long teacherId = student.getTeacherId();
        // 班级id
        Long classId = student.getClassId();

        // 获取 `每个学生的信息`
        List<Map<String, Object>> students = studentMapper.selectSeniority(model, teacherId, classId);

        // 获取等级规则
        List<Map<String, Object>> levels = levelMapper.selectAll();

        // 用于封装我的排名,我的金币,我的等级,我被膜拜
        Map<String, Object> myMap = new HashMap<>(16);

        // 我的排名(全部)
        Map<Long, Map<String, Object>> classLevel = null;
        if (queryType == null) {
            if ("3".equals(model)) {
                // 全国排名
                classLevel = studentMapper.selectLevelByStuId(student, 3);
            } else if ("2".equals(model)) {
                // 学校排名
                classLevel = studentMapper.selectLevelByStuId(student, 2);
            } else {
                // 班级排名
                classLevel = studentMapper.selectLevelByStuId(student, 1);
            }
            if (classLevel == null || classLevel.get(student.getId()) == null) {
                return ServerResponse.createBySuccess("无排行数据");
            }
            String myRankingDouble = (classLevel.get(student.getId())).get("rank") + "";
            if (myRankingDouble.contains(".")) {
                // 我的排名
                myMap.put("myRanking", myRankingDouble.substring(0, myRankingDouble.indexOf(".")));
            } else {
                // 我的排名
                myMap.put("myRanking", myRankingDouble);
            }
        }

        // 我的排行 今日，本周，本月
        if (queryType != null) {
            // 查出来的顺序金币是从大到小
            List<Integer> queryTypeList = new ArrayList<>();

            // 我的排名(今日)
            if (queryType == 1) {
                queryTypeList = runLogMapper.getAllQueryType(DateUtil.formatYYYYMMDD(new Date()), model, student);
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
            Double myGoldD = studentMapper.myGold(student.getId());
            BigDecimal mybd = new BigDecimal(myGoldD).setScale(0, BigDecimal.ROUND_HALF_UP);
            myGold = Integer.parseInt(mybd.toString());
        } else {
            // 今日，本周，本月排行
            // 我的排名(今日)
            Map<Long, Map<String, Object>> m = new HashMap();
            if (queryType == 1) {
                m = runLogMapper.getGoldByStudentId(DateUtil.formatYYYYMMDD(new Date()), model, student);
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
        int myMb = studentMapper.myMb(student.getId());
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
            myrecord = 0;
            myauto = 0;
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
        Map<Long, Map<String, Long>> runLogCount = runLogMapper.getMapKeyStudentrunLog();

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
                record = 0;
                auto = 0;
            } else {
                stu.put("childName", levels.get(0).get("child_name"));
            }
        }

        // 排序
        if ("1".equals(golds)) {
            // 按照金币
            //Collections.sort(students, new MapGoldAsc());
            Collections.sort(students, new MapGoldDesc());
        } else if ("2".equals(golds)) {
            Collections.sort(students, new MapGoldDesc());

        } else if ("1".equals(badges)) {
            // 按照勋章
            //Collections.sort(students, new MapXzAsc());
            Collections.sort(students, new MapXzDesc());
        } else if ("2".equals(badges)) {
            Collections.sort(students, new MapXzDesc());

        } else if ("1".equals(certificates)) {
            // 按照证书
            //Collections.sort(students, new MapZsAsc());
            Collections.sort(students, new MapZsDesc());
        } else if ("2".equals(certificates)) {
            Collections.sort(students, new MapZsDesc());

        } else if ("1".equals(worships)) {
            // 按照膜拜
            //Collections.sort(students, new MapMbAsc());
            Collections.sort(students, new MapMbDesc());
        } else if ("2".equals(worships)) {
            Collections.sort(students, new MapMbDesc());
        } else {
            // 默认本班金币倒叙
            Collections.sort(students, new MapGoldDesc());
        }

        // list最大限制到100
        List<Map<String, Object>> lista = new ArrayList<>(students.subList(0, students.size() > 100 ? 100 : students.size()));

        // 总参与人数
        myMap.put("number", lista.size());
        // 把我的信息封装到返回结果集中
        result.put("myDate", myMap);

        // 我的排名,根据选项实时更换排行
        int aa = 0;
        Long id = student.getId();
        for (Map m : lista) {
            aa++;
            if (m.get("id").equals(id)) {
                myMap.put("myRanking", aa);
            }
        }

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

        redisTemplate.opsForHash().put(KEY, FIELD, result);
        redisTemplate.expire(KEY, 30, TimeUnit.MINUTES);
        return ServerResponse.createBySuccess(result);
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
    public ServerResponse<Object> showCcie(HttpSession session, Integer model, Integer type) {
        // 获取当前学生信息
        Student student = getStudent(session);
        Long studentId = student.getId();

        List<Map<String, Object>> listCcie = ccieMapper.selectAllCcieByStudentIdAndDate(studentId, model, type);
        if (listCcie.size() == 0) {
            return ServerResponse.createByErrorMessage("暂无证书！");
        }
        // 拼接证书内容
        StringBuilder sb = new StringBuilder();
        String[] time;
        // 封装课程名
        for (Map<String, Object> map : listCcie) {
            String ccieNo = map.get("ccie_no") + "";
            if (ccieNo.startsWith("K")) {
                map.put("ccieName", "课程证书");
            } else if (ccieNo.startsWith("N")) {
                map.put("ccieName", "牛人证书");
            }

            time = map.get("time").toString().split("-");
            sb.setLength(0);
            if (type == 2) {
                sb.append("恭喜上神于").append(time[0]).append("年").append(time[1]).append("月").append(time[2])
                        .append("日，修完“").append(map.get("courseName")).append("”课程，特此颁发证书，以资鼓励。");
            } else {
                sb.append("于").append(time[0]).append("年").append(time[1]).append("月").append(time[2])
                        .append("日，在“智慧英语平台”完成").append(map.get("testName"))
                        .append("。").append(map.get("encourageWord")).append("，特发此证，以资鼓励。");
            }

            map.put("content", sb.toString());
            map.put("ccie_no", "证书编号：" + map.get("ccie_no"));

            map.remove("encourageWord");
            map.remove("time");
            map.remove("testName");
        }

        return ServerResponse.createBySuccess(listCcie);
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
        Long studentId = StudentIdBySession(session);

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
        int we = DateUtil.DateYYYY();

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
                    map.put("statsDate", s.format(weekStart));// 每周开始日期
                    map.put("endDate", s.format(weekEnd));// 每周结束日期
                    map.put("week", "第" + (i + 1) + "周"); // 第几周
                    //map.put("weekSort", (i+1));// 周,该字段用于排序
                    map.put("state", false); // 不是本周
                    // 查询循环周的总有效时长(m)
                    Integer duration = durationMapper.totalTime(DateUtil.formatYYYYMMDD(weekStart), DateUtil.formatYYYYMMDD(weekEnd), studentId);
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
    public ServerResponse<Object> weekQuantityPage(HttpSession session, int page, int rows, Integer yea) {
        int protogenesis_page = page;

        // 获取当前学生id
        Long studentId = StudentIdBySession(session);

        // 设置响应需要的日期格式
        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");

        // 设置返回封装结果集
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 学生在该系统学习过的年份
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

        // 获取当前时间年份
        int we = DateUtil.DateYYYY();

        // 循环里边进行分页操作
        int auto = 0; // page
        int brak = 1; // rows
        int return_ = 0; // 0保存输入当前页的数据, 1不保存

        int total = 0; // 记录总数据数

        for (Integer year : years) {

            // 1.根据年获取有多少周
            int yearMax = WeekUtil.getMaxWeekNumOfYear(year) - 1;

            int maxWeek = year == we ? ye : yearMax;
            // 循环周从大到小
            for (int i = maxWeek; i >= 0; i--) {
                total++;

                // 分页
                if (auto >= (page - 1) * rows && brak <= rows) {

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
                    // 每周六个模块个个的学习量
                    Map<String, Object> countMap = learnMapper.mapWeekCountQuantity(DateUtil.formatYYYYMMDD(weekStart), DateUtil.formatYYYYMMDD(weekEnd), studentId);
                    Long duration = (Long) countMap.get("count");
                    int plan = ((int) (BigDecimalUtil.div((duration == null ? 0 : duration), 200) * 100));
                    if (plan <= 100) {
                        map.put("plusSign", false); // 进度条不显示+号
                        map.put("duration", plan); // 本分比进度条
                    } else {
                        map.put("plusSign", true); // 进度条显示+号
                        map.put("duration", 100); // 本分比进度条
                    }

                    // 跳过没有数据的周
//                    if(duration == 0){
//                        total--;
//                        continue;
//                    }
                    map.put("count", duration); // 周总学习量
                    map.put("a", countMap.get("a")); // 周慧记忆学习量
                    map.put("b", countMap.get("b")); // 周慧听写学习量
                    map.put("c", countMap.get("c")); // 周慧默写学习量
                    map.put("d", countMap.get("d")); // 周例句听力学习量
                    map.put("e", countMap.get("e")); // 周例句翻译学习量
                    map.put("f", countMap.get("f")); // 周例句默写学习量

                    // 本周
                    if (year == we && i == maxWeek) {
                        // 本周
                        map.put("week", "本周");
                        map.put("state", true); // 本周, 有继续学习按钮
                    } else {
                        // 去掉数据为空的周
                        if (plan == 0) {
                            //brak++;
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

        // 封装数据,返回
        Map totalMap = new HashMap();
        totalMap.put("page", protogenesis_page);
        totalMap.put("total", total % rows == 0 ? total / rows : total / rows + 1);
        totalMap.put("data", result);

        return ServerResponse.createBySuccess(totalMap);
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
        // 年级
        String grade = student.getGrade();
        // 版本
        String version = student.getVersion();

        // 教师id - 学校
        Long teacherId = student.getTeacherId();
        // 班级id - 班级
        Long classId = student.getClassId();

        // 学段
        String study_paragraph;
        if ("七年级".equals(grade) || "八年级".equals(grade) || "九年级".equals(grade)) {
            study_paragraph = "初中";
        } else if ("小学".equals(grade)) {
            study_paragraph = "小学";
        } else {
            study_paragraph = "高中";
        }

        // 响应数据
        Map<String, Object> result = new HashMap<>(16);
        List<SeniorityVo> list = null;

        // 默认本班排行
        if (model == 1) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSeniority(grade, study_paragraph, haveUnit, version, classId);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSeniority(grade, study_paragraph, haveTest, version, classId);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSeniority(grade, study_paragraph, haveTime, version, classId);
            }

            // 班级总参与人数
            result.put("atNumber", list == null ? 0 : list.size());

            // 本校排行
        } else if (model == 2) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSenioritySchool(study_paragraph, haveUnit, version, teacherId);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSenioritySchool(study_paragraph, haveTest, version, teacherId);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSenioritySchool(study_paragraph, haveTime, version, teacherId);
            }

            // 学校总参与人数
            result.put("atNumber", studentMapper.schoolHeadcount(teacherId, version));

            // 全国排行
        } else {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSeniorityNationwide(study_paragraph, haveUnit, version);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSeniorityNationwide(study_paragraph, haveTest, version);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSeniorityNationwide(study_paragraph, haveTime, version);
            }

            // 初中/高中总参与人数
            result.put("atNumber", studentMapper.schoolHeadcountNationwide(study_paragraph, version));

        }

        int auto = 0;

        // 用于判断当前学生是否在排行里边有排名
        result.put("atRanking", "0");

        // 封装页面需要的数据, 所有学生数据
        for (SeniorityVo vo : list) {

            Long stuId = vo.getStudent_id();

            // 学生排行
            // 查询单元排行
            vo.setCountUnit(studentUnitMapper.onePlanSeniority(stuId));
            // 查询已做测试
            vo.setCountTest(testRecordMapper.onePlanSeniority(stuId));
            // 查询学习时长
            vo.setLearnDate(durationMapper.onePlanSeniority(stuId));

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
        List<SeniorityVo> resultList = new ArrayList<>(list.subList(page > list.size() ? list.size() : page, list.size() < rows ? list.size() : rows));
        result.put("pageDate", resultList);

        // 当前学生排行等数据,
        if (result.get("atRanking").equals("0")) {
            result.put("atRanking", "未上榜");
            result.put("atCountUnit", studentUnitMapper.onePlanSeniority(studentId));
            result.put("atCountTest", testRecordMapper.onePlanSeniority(studentId));
            result.put("atLearnDate", durationMapper.onePlanSeniority(studentId));
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

        map.put("toLearn", unitAllStudyModel);
        map.put("countUnit", count);
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
        int startYear = studentMapper.getYear(studentId);
        // 当前年份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        int endYear = Integer.parseInt(sdf.format(date));
        year.add("全部");
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
        Map student = studentMapper.getStudentAccountTime(studentId);
        return ServerResponse.createBySuccess(student);
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
        // 充值卡时间
        int cardNo = 100;

        Student student = studentMapper.selectByPrimaryKey(studentId);
        // 学生账号
        String account = student.getAccount();
        // 查询学生账号到期时间
        Date parse = student.getAccountTime();

        // 1.修改充值卡状态
        int up = payCardMapper.updateCreateUserByCardNo(card, account, new Date());
        if (up != 1) {
            Long pc = payCardMapper.getIdByCardNo(card);
            if (pc == null) {
                return ServerResponse.createByErrorMessage("卡号不存在");
            } else {
                return ServerResponse.createByErrorMessage("该卡号已被使用");
            }
        }

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
            int s = studentMapper.updateAccountTimeByStudentId(studentId, format);
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
                payLogMapper.insert(pa);
                return ServerResponse.createBySuccess("充值成功");
            }
        }

        return ServerResponse.createByErrorMessage("充值失败");
    }

    @Override
    public ServerResponse<Object> getPayCard(long studentId, int page, int rows) {
        Map resut = new HashMap();
        PageHelper.startPage(page, rows);
        List<Map> map = payLogMapper.selectPayLogDataByStudentId(studentId);
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

    @Override
    public ServerResponse<Object> getMedalInClass(HttpSession session) {
        Student student = getStudent(session);

        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("studentId", student.getId());
        paramMap.put("session", session);
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));

        String url = domain + "/api/personal/getLatestMedalInClass?session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(entity.getBody() == null ? null : entity.getBody().get("data"));
    }

    @Override
    public Object getLucky(Integer studentId, HttpSession session) {
        Map<String, Object> useMap = new HashMap<>();
        if (studentId == null) {
            Student student = getStudent(session);
            studentId = student.getId().intValue();
            useMap.put("sex", student.getSex() == 1 ? "男" : "女");
        } else {
            Student student = studentMapper.selectById(studentId);
            useMap.put("sex", student.getSex() == 1 ? "男" : "女");
        }
        Map<String, Object> resultMap = new HashMap<>();
        //查询手套印记
        List<SyntheticRewardsList> gloveOrFlower = syntheticRewardsListMapper.getGloveOrFlower(studentId);
        SyntheticRewardsList useGloveOrFlower = syntheticRewardsListMapper.getUseGloveOrFlower(studentId);
        List<Map<String, Object>> gloveOrFlowerList = new ArrayList<>();

        for (SyntheticRewardsList synthetic : gloveOrFlower) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", synthetic.getImgUrl());
            if (useGloveOrFlower != null) {
                map.put("state", true);
                map.put("time", 48 + "小时00分00秒");
            } else {
                SyntheticRewardsList isUse = syntheticRewardsListMapper.getIsUse(studentId, synthetic.getName());
                if (isUse != null) {
                    Integer count = syntheticRewardsListMapper.selCountByStudentIdAndName(synthetic);
                    map.put("state", false);
                    map.put("time", 48 * count + "小时00分00秒");
                } else {
                    map.put("state", true);
                    map.put("time", 48 + "小时00分00秒");
                }
            }
            map.put("syntheticInteger", AwardUtil.getMaps(synthetic.getName()));
            map.put("type", "gloveOrFlower");
            map.put("name", synthetic.getName());
            map.put("message", "得到的金币加成" + AwardUtil.getNumber(Integer.parseInt(AwardUtil.getMaps(synthetic.getName()).toString())) + "%");
            map.put("createTime", synthetic.getCreateTime());
            gloveOrFlowerList.add(map);
        }
        if (useGloveOrFlower != null) {
            Map<String, Object> useNowMap = new HashMap<>();
            useNowMap.put("url", useGloveOrFlower.getImgUrl());
            useNowMap.put("endTime", useGloveOrFlower.getUseEndTime());
            useMap.put("gloveOrFlower", useNowMap);
        }
        resultMap.put("gloveOrFlower", gloveOrFlowerList);
        List<Map<String, Object>> skinList = new ArrayList<>();
        List<StudentSkin> studentSkins = studentSkinMapper.selSkinByStudentIdIsHave(studentId.longValue());
        for (StudentSkin studentSkin : studentSkins) {
            if (studentSkin.getState() == 1) {
                useMap.put("skin", studentSkin.getImgUrl());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("url", studentSkin.getImgUrl());
            if (studentSkin.getState() == 1) {
                map.put("state", true);
            } else {
                map.put("state", false);
            }
            map.put("type", "skin");
            map.put("skinIngter", AwardUtil.getMaps(studentSkin.getSkinName()));
            map.put("id", studentSkin.getId());
            map.put("name", studentSkin.getSkinName());
            map.put("message", "个性装扮");
            map.put("createTime", studentSkin.getCreateTime());
            skinList.add(map);
        }
        resultMap.put("skin", skinList);
        resultMap.put("use", useMap);
        return ServerResponse.createBySuccess(resultMap);
    }

}

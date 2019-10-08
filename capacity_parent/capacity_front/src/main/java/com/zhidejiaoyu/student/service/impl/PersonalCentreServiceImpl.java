package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

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
    private SentenceMapper sentenceMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

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
            Long courseId = (Long) map.get("id");

            resultMap.put("id", courseId);// 课程id
            resultMap.put("courseName", map.get("course_name"));// 带年级的课程名
            resultMap.put("versionLabel", map.get("version") + "-" + map.get("label"));// 不带年级的课程名
            resultMap.put("learnCount", map.get("learn_count"));// 课程学习的第几遍

            // 获取课程单词总数
            Integer countVocabulary = vocabularyMapper.courseCountVocabulary(courseId);
            // 获取课程例句总数
            Integer countSentence = sentenceMapper.courseCountSentence(courseId);
            // 获取单词图鉴总数
            Integer picCount = vocabularyMapper.picByCourseId(courseId);
            resultMap.put("countVocabulary", countVocabulary); // 课程单词总数
            resultMap.put("countSentence", countSentence); // 课程例句总数
            resultMap.put("picCount", picCount); // 单词图鉴总数

            // 获取课程下所有单元id
            List<Map<String, Object>> allUnit = unitMapper.allUnit(courseId.intValue());
            // 当前课程单词模块所学单元id
            Integer wordMax = studentUnitMapper.maxUnitIdByWordByCourseIdByStudentIdBy(courseId, studentId);
            // 当前课程例句模块所学单元id
            Integer sentenceMax = studentUnitMapper.maxUnitIdBySentenceByCourseIdByStudentIdBy(courseId, studentId);


            // 根据课程获取六个模块的学习量
            for (int i = 0; i < 7; i++) {
                Map<String, Object> m = new HashMap<String, Object>();
                String countStr = learnMapper.countCourseStudyModel(studentId, courseId, i);
                Integer count = 0;
                if (StringUtils.isNotBlank(countStr)) {
                    count = Integer.parseInt(countStr);
                }
                m.put("studyModel", count); // 六个模块个个的学习量

                if (i == 0) {
                    // 计算单词模块百分比值
                    int planVocabulary = ((int) (BigDecimalUtil.div(count, picCount) * 100));
                    m.put("duration", planVocabulary);// 单词图鉴模块百分比值
                } else if (i < 4) {
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

        int protogenesisPage = page;

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
        totalMap.put("page", protogenesisPage);
        totalMap.put("total", total % rows == 0 ? total / rows : total / rows + 1);
        totalMap.put("data", result);

        return ServerResponse.createBySuccess(totalMap);
    }

    @Override
    public ServerResponse<Object> weekQuantityPage(HttpSession session, int page, int rows, Integer yea) {
        int protogenesisPage = page;

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
        totalMap.put("page", protogenesisPage);
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
        String studyParagraph;
        if ("七年级".equals(grade) || "八年级".equals(grade) || "九年级".equals(grade)) {
            studyParagraph = "初中";
        } else if ("小学".equals(grade)) {
            studyParagraph = "小学";
        } else {
            studyParagraph = "高中";
        }

        // 响应数据
        Map<String, Object> result = new HashMap<>(16);
        List<SeniorityVo> list = null;

        // 默认本班排行
        if (model == 1) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSeniority(grade, studyParagraph, haveUnit, version, classId);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSeniority(grade, studyParagraph, haveTest, version, classId);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSeniority(grade, studyParagraph, haveTime, version, classId);
            }

            // 班级总参与人数
            result.put("atNumber", list == null ? 0 : list.size());

            // 本校排行
        } else if (model == 2) {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSenioritySchool(studyParagraph, haveUnit, version, teacherId);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSenioritySchool(studyParagraph, haveTest, version, teacherId);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSenioritySchool(studyParagraph, haveTime, version, teacherId);
            }

            // 学校总参与人数
            result.put("atNumber", studentMapper.schoolHeadcount(teacherId, version));

            // 全国排行
        } else {
            // 已学单元
            if (haveUnit != null && haveUnit > 0) {
                list = studentUnitMapper.planSeniorityNationwide(studyParagraph, haveUnit, version);
                // 已做测试
            } else if (haveTest != null && haveTest > 0) {
                list = testRecordMapper.planSeniorityNationwide(studyParagraph, haveTest, version);
                // 学习时长
            } else if (haveTime != null && haveTime > 0) {
                list = durationMapper.planSeniorityNationwide(studyParagraph, haveTime, version);
            }

            // 初中/高中总参与人数
            result.put("atNumber", studentMapper.schoolHeadcountNationwide(studyParagraph, version));

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
            map.put("url", GetOssFile.getPublicObjectUrl(synthetic.getImgUrl()));
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
            useNowMap.put("url", GetOssFile.getPublicObjectUrl(useGloveOrFlower.getImgUrl()));
            useNowMap.put("endTime", useGloveOrFlower.getUseEndTime());
            useMap.put("gloveOrFlower", useNowMap);
        }
        resultMap.put("gloveOrFlower", gloveOrFlowerList);
        List<Map<String, Object>> skinList = new ArrayList<>();
        List<StudentSkin> studentSkins = studentSkinMapper.selSkinByStudentIdIsHave(studentId.longValue());
        for (StudentSkin studentSkin : studentSkins) {
            if (studentSkin.getState() == 1) {
                Map<String, Object> skinMap = new HashMap<>();
                if (studentSkin.getEndTime() != null) {
                    skinMap.put("endTime", studentSkin.getEndTime());
                    skinMap.put("time", (studentSkin.getEndTime().getTime() - System.currentTimeMillis()) / 1000);
                }
                skinMap.put("url", GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()));
                useMap.put("skin", skinMap);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("url", GetOssFile.getPublicObjectUrl(studentSkin.getImgUrl()));
            if (studentSkin.getState() == 1) {
                map.put("state", true);
            } else {
                map.put("state", false);
            }
            map.put("type", "skin");
            map.put("skinIngter", AwardUtil.getMaps(studentSkin.getSkinName()));
            map.put("id", studentSkin.getId());
            if (studentSkin.getEndTime() == null) {
                map.put("endTime", "30天");
                map.put("time", "30天");
            } else {
                map.put("endTime", studentSkin.getEndTime());
                map.put("time", (studentSkin.getEndTime().getTime() - System.currentTimeMillis()) / 1000);
            }
            map.put("name", studentSkin.getSkinName());
            map.put("message", "个性装扮");
            map.put("createTime", studentSkin.getCreateTime());
            skinList.add(map);
        }
        resultMap.put("skin", skinList);
        resultMap.put("use", useMap);
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<Object> needViewCount(HttpSession session) {
        Student student = super.getStudent(session);

        // 留言反馈未阅读信息数量
        int feedBackCount = unReadFeedBackCount(student.getId());
        // 可抽奖次数
        int lotteryCount = lotteryCount(student);

        Map<String, Integer> map = new HashMap<>(16);

        map.put("count", feedBackCount + lotteryCount);
        return ServerResponse.createBySuccess(map);
    }

    private int unReadFeedBackCount(Long studentId) {
        MessageBoardExample messageBoardExample = new MessageBoardExample();
        MessageBoardExample.Criteria criteria = messageBoardExample.createCriteria();
        criteria.andStudentIdEqualTo(studentId).andReadFlagEqualTo(3).andRoleEqualTo(1);
        return messageBoardMapper.countByExample(messageBoardExample);
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

}

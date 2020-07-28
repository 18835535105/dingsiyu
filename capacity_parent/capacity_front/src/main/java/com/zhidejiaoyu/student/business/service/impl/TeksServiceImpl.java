package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.constant.PetMP3Constant;
import com.zhidejiaoyu.common.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.CalculateTimeUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;
import com.zhidejiaoyu.common.utils.goldUtil.TestGoldUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.learn.common.SaveTeksData;
import com.zhidejiaoyu.student.business.service.TeksService;
import com.zhidejiaoyu.student.business.test.service.TestService;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TeksServiceImpl extends BaseServiceImpl<TeksMapper, Teks> implements TeksService {

    /**
     * 50分
     */
    private static final int FIVE = 50;
    /**
     * 60分
     */
    private static final int SIX = 60;
    /**
     * 70分
     */
    private static final int SEVENTY = 70;
    /**
     * 80分
     */
    private static final int PASS = 80;
    /**
     * 90分
     */
    private static final int NINETY_POINT = 90;
    /**
     * 100分
     */
    private static final int FULL_MARK = 100;


    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》", "\"", "-", "..."};

    private static final List<String> NAMELIST = new ArrayList<String>() {{
        add("Zhang");
        add("Wu");
        add("Yifan");
        add("Wang");
        add("Han");
        add("Amy");
        add("Anne");
        add("Mingming");
        add("Jones");
        add("mr");
        add("Mr");
        add("Miss");
    }};
    /**
     * 以字母或数字结尾
     */
    final String END_MATCH = ".*[a-zA-Z0-9$# @]$";

    /**
     * 以字母或数字开头
     */
    final String START_MATCH = "^[a-zA-Z0-9$# @].*";

    /**
     * 以字母或数字结尾
     */
    final String END_MATCH2 = ".*[a-zA-Z0-9$# '@-]$";

    @Autowired
    private TeksMapper teksMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;
    @Autowired
    private CommonMethod commonMethod;
    @Autowired
    private TeksNewMapper teksNewMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private VoiceMapper voiceMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private PetSayUtil petSayUtil;

    @Autowired
    private TestGoldUtil testGoldUtil;

    @Autowired
    private TeksCourseMapper teksCourseMapper;

    @Autowired
    private TeksUnitMapper teksUnitMapper;
    @Resource
    private SaveTeksData saveTeksData;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private TestService testService;
    @Resource
    private CourseFeignClient courseFeignClient;


    @Override
    public ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId) {
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        if (teks.size() > 0) {
            List<Teks> resultTeks = new ArrayList<>();
            int i = 0;
            for (Teks teks1 : teks) {
                teks1.setPronunciation(baiduSpeak.getSentencePath(teks1.getSentence().replace("#", " ").replace("$", "")));
                i++;
                teks1.setSentence(teks1.getSentence().replace("#", " ").replace("$", ""));
                resultTeks.add(teks1);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> selSpeakTeksByUnitId(Long unitId, HttpSession session) {
        Student student = getStudent(session);
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, 1, 3);
        List<TeksNew> teks = teksNewMapper.selTeksByUnitIdAndGroup(unitId, learnNews.getGroup());
        Map<String, Object> getMap = new HashMap<>(16);
        getMap.put("studentId", student.getId());
        getMap.put("unitId", unitId);
        Integer integer = voiceMapper.selMaxCountByUnitIdAndStudentId(getMap);
        if (teks.size() > 0) {
            Map<String, Object> map = new HashMap<>(16);
            List<TeksNew> resultTeks = new ArrayList<>();
            for (TeksNew teks1 : teks) {
                teks1.setPronunciation(baiduSpeak.getSentencePath(teks1.getSentence()));
                teks1.setSentence(replace(teks1.getSentence()));
                resultTeks.add(teks1);
            }
            map.put("list", resultTeks);
            if (integer == null) {
                map.put("count", 0);
            } else {
                map.put("count", integer);
            }
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> selHistoryByCountAndUnitId(Integer count, Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> maps = new HashMap<>();
        maps.put("unitId", unitId);
        maps.put("studentId", student.getId());
        maps.put("count", count);
        List<Map<String, Object>> map = teksMapper.selHistoryByCountAndUnitId(maps);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> resultMap = new ArrayList<>();
        for (Map<String, Object> getMap : map) {
            getMap.put("url", GetOssFile.getPublicObjectUrl(String.valueOf(getMap.get("url"))));
            resultMap.add(getMap);
        }
        result.put("list", resultMap);
        result.put("count", count);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> selRankingList(Integer unitId, HttpSession session) {
        Student student = getStudent(session);

        Map<String, Object> getMap = new HashMap<>(16);
        getMap.put("schoolName", student.getSchoolName());
        getMap.put("unitId", unitId);
        getMap.put("nowTime", new DateTime().toString(DateUtil.YYYYMMDDHHMMSS));
        // 28天前日期
        getMap.put("beforeTime", new DateTime().minusDays(28).toString(DateUtil.YYYYMMDDHHMMSS));

        Map<String, Object> result = new HashMap<>(16);
        //全国排名
        List<Map<String, Object>> countryMap = voiceMapper.selectTextRank(getMap, AliyunInfoConst.host);
        result.put("nationalRanking", countryMap);
        //全校排名
        List<Map<String, Object>> schoolMap = voiceMapper.selectTextRankSchool(getMap, AliyunInfoConst.host);
        result.put("shcoolRanking", schoolMap);
        return ServerResponse.createBySuccess(result);
    }


    @Override
    public ServerResponse<Object> selChooseTeks(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        //判断是否取出数据
        if (teks.size() > 0) {
            //返回的集合
            List<Map<String, Object>> resultList = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            //遍历数据
            for (Teks teks1 : teks) {
                //将遍历的数据放入到
                Map<String, Object> map = new HashMap<>();
                map.put("chinese", teks1.getParaphrase());
                map.put("pronunciation", baiduSpeak.getSentencePath(teks1.getSentence()));
                map.put("id", teks1.getId());
                String[] sentenceList = teks1.getSentence().trim().split(" ");
                this.getList(sentenceList, map);
                resultList.add(map);
                resultMap.put("list", resultList);
                resultMap.put("number", teks.size());
                Map<String, Object> selMap = new HashMap<>();
                selMap.put("unitId", unitId);
                selMap.put("studentId", student.getId());
                selMap.put("model", "课文默写测试");
                selMap.put("genre", null);
                Integer integer = testRecordMapper.selectMaxPointByUnitStudentModel(selMap);
                if (integer == null) {
                    resultMap.put("maxScore", 0);
                } else {
                    resultMap.put("maxScore", integer);
                }
            }

            return ServerResponse.createBySuccess(resultMap);
        }
        return ServerResponse.createByError();
    }

    @Override
    public void getList(String[] split, Map<String, Object> map) {
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        // 存储标点
        List<String> pointList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            s = StringUtils.trim(replace(s));
            if (Pattern.matches(END_MATCH, s) && Pattern.matches(START_MATCH, s)) {
                rightList.add(s);
                pointList.add(null);
                orderList.add(s);
            } else {
                char[] chars = s.toCharArray();
                sb.setLength(0);
                int length = chars.length;
                for (int i = 0; i < length; i++) {
                    char aChar = chars[i];
                    // 当前下标的数据
                    String s1 = new String(new char[]{aChar});
                    // 是字母或者数字，拼接字符串
                    if (Pattern.matches(END_MATCH, s1)) {
                        sb.append(s1);
                    } else {
                        //判断是否为第一位
                        if (i == 0) {
                            if (sb.length() > 0) {
                                rightList.add(replace(sb.toString()));
                                orderList.add(replace(sb.toString()));
                                sb.setLength(0);
                            }
                            // 如果符号前面是字母需要在符号列表中加 null
                            rightList.add(s1);
                            pointList.add(s1);
                        } else {
                            //如果中间出现符号则掠过
                            if (i < (length - 1)) {
                                char longChar = chars[i + 1];
                                String s2 = new String(new char[]{longChar});
                                if (Pattern.matches(END_MATCH, s2)) {
                                    sb.append(s1);
                                } else {
                                    if (sb.length() > 0) {
                                        rightList.add(replace(sb.toString()));
                                        orderList.add(replace(sb.toString()));
                                        sb.setLength(0);
                                    }
                                    // 如果符号前面是字母需要在符号列表中加 null
                                    if (Pattern.matches(END_MATCH, new String(new char[]{chars[i - 1]}))) {
                                        pointList.add(null);
                                    }
                                    rightList.add(s1);
                                    pointList.add(s1);
                                }
                            } else {
                                if (sb.length() > 0) {
                                    rightList.add(replace(sb.toString()));
                                    orderList.add(replace(sb.toString()));
                                    sb.setLength(0);
                                }
                                // 如果符号前面是字母需要在符号列表中加 null
                                if (Pattern.matches(END_MATCH, new String(new char[]{chars[i - 1]}))) {
                                    pointList.add(null);
                                }
                                rightList.add(s1);
                                pointList.add(s1);
                            }
                        }

                    }

                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(replace(sb.toString()));
                        orderList.add(replace(sb.toString()));
                        pointList.add(null);
                        sb.setLength(0);
                    }
                }
            }
        }
        Collections.shuffle(orderList);
        map.put("sentence", rightList);
        map.put("vocabularyArray", orderList);
        map.put("blankSentenceArray", pointList);
    }

    public static String replace(String str) {
        return StringUtils.trim(str.replace("#", " ").replace("$", ""));
    }

    @Override
    @SuppressWarnings("all")
    public ServerResponse<Map<String, Object>> getCourseAndUnit(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        Map<String, Object> studyMap = null;
        List<Map<String, Object>> resultMap;
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        Map<String, Object> returnMap = new HashMap<>();
        // 学生所有课程id及课程名
        /* List<Map<String, Object>> courses = courseMapper.selectTextCourseIdAndCourseNameByStudentId(studentId);*/
        List<Map<String, Object>> courses = studentStudyPlanMapper.selTeksByStudentId(studentId, 3);
        if (courses == null || courses.size() == 0) {
            return ServerResponse.createByError(400, "当前学生没有课程，请让老师添加");
        }
        // 学生课程下所有例句的单元id及单元名
        if (courses.size() > 0) {
            List<Long> courseIds = new ArrayList<>(courses.size());
            courses.forEach(map -> courseIds.add((Long) map.get("id")));
            Map<String, Object> learnUnit = learnMapper.selTeksLaterCourse(student.getId());

            for (Map<String, Object> courseMap : courses) {
                courseUnitVo = new CourseUnitVo();
                resultMap = new ArrayList<>();
                Long id = learnMapper.selLaterLearnTeks(student.getId(), (Long) courseMap.get("id"));
                if (id != null) {
                    courseUnitVo.setLearnUnit(id.toString());
                }
                courseUnitVo.setCourseId((Long) courseMap.get("id"));
                courseUnitVo.setCourseName(courseMap.get("courseName").toString());
                courseUnitVo.setVersion(courseMap.get("version").toString());
                courseUnitVo.setGrad(courseMap.get("grade").toString() + "-" + courseMap.get("label").toString());
                courseUnitVo.setUnitVos(resultMap);
                courseUnitVos.add(courseUnitVo);
            }
            if (learnUnit != null) {
                List<StudentStudyPlan> plans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, (Long) learnUnit.get("course_id"), 3);
                boolean flag = false;
                Long unitId = (Long) learnUnit.get("unit_id");
                if (plans.size() != 0) {
                    for (StudentStudyPlan plan : plans) {
                        if (unitId >= plan.getStartUnitId() && unitId <= plan.getEndUnitId()) {
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    studyMap = new HashMap<>();
                    Long studyUnitId = Long.parseLong(((Long) learnUnit.get("unit_id")).toString());
                    studyMap.put("unitId", studyUnitId);
                    studyMap.put("version", learnUnit.get("version"));
                    TeksUnit teksUnit = teksUnitMapper.selectById(studyUnitId);
                    studyMap.put("unitName", teksUnit.getUnitName());
                    studyMap.put("grade", learnUnit.get("grade").toString() + "-" + learnUnit.get("label").toString());
                    studyMap.put("courseId", learnUnit.get("courseId"));
                } else {
                    if (plans.size() != 0) {
                        studyMap = new HashMap<>();
                        Long studyUnitId = plans.get(0).getStartUnitId();
                        studyMap.put("unitId", studyUnitId);
                        TeksUnit teksUnit = teksUnitMapper.selectById(studyUnitId);
                        studyMap.put("unitName", teksUnit.getUnitName());
                        TeksCourse teksCourse = teksCourseMapper.selectById(plans.get(0).getCourseId());
                        studyMap.put("version", teksCourse.getVersion());
                        studyMap.put("grade", teksCourse.getGrade() + "-" + teksCourse.getLabel());
                        studyMap.put("courseId", teksCourse.getId());
                    } else {
                        List<StudentStudyPlan> allPlans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, (Long) courses.get(0).get("id"), 3);
                        if (allPlans.size() != 0) {
                            studyMap = new HashMap<>();
                            studyMap.put("unitId", allPlans.get(0).getStartUnitId());
                            TeksCourse teksCourse = teksCourseMapper.selectById(allPlans.get(0).getCourseId());
                            studyMap.put("version", teksCourse.getVersion());
                            TeksUnit teksUnit = teksUnitMapper.selectById(allPlans.get(0).getStartUnitId());
                            studyMap.put("unitName", teksUnit.getUnitName());
                            studyMap.put("version", teksCourse.getVersion());
                            studyMap.put("courseId", teksCourse.getId());
                            studyMap.put("grade", teksCourse.getGrade() + "-" + teksCourse.getLabel());
                        }
                    }
                }
            } else {
                List<StudentStudyPlan> plans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, (Long) courses.get(0).get("id"), 3);
                if (plans.size() != 0) {
                    studyMap = new HashMap<>();
                    studyMap.put("unitId", plans.get(0).getStartUnitId());
                    TeksCourse teksCourse = teksCourseMapper.selectById(plans.get(0).getCourseId());
                    studyMap.put("version", teksCourse.getVersion());
                    TeksUnit teksUnit = teksUnitMapper.selectById(plans.get(0).getStartUnitId());
                    studyMap.put("unitName", teksUnit.getUnitName());
                    studyMap.put("version", teksCourse.getVersion());
                    studyMap.put("courseId", teksCourse.getId());
                    studyMap.put("grade", teksCourse.getGrade() + "-" + teksCourse.getLabel());
                }
            }
            List<Map<String, Object>> testList = teksMapper.getStudentAllCourse(studentId, courseIds);
            returnMap.put("present", studyMap);
            returnMap.put("versionList", testList);
            returnMap.put("list", courseUnitVos);
        }


        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<Object> getUnitStatus(Long courseId, HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<Long> courseIds = new ArrayList<>();
        courseIds.add(courseId);
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        List<Map<String, Object>> textUnits = new ArrayList<>();
        List<Map<String, Object>> resultMap;
        this.getStudyUnit(courseIds, textUnits, studentId);
        // 已经进行过单元闯关的单元
        List<Map<String, Object>> courses = studentStudyPlanMapper.selByStudentIdAndCourseIdAndType(studentId, 3, courseId);

        for (Map<String, Object> courseMap : courses) {
            courseUnitVo = new CourseUnitVo();
            resultMap = new ArrayList<>();
            Long id = learnMapper.selLaterLearnTeks(student.getId(), (Long) courseMap.get("id"));
            if (id != null) {
                courseUnitVo.setLearnUnit(id.toString());
            }
            // 存放单元信息
            Map<String, Object> unitInfoMap;
            for (Map<String, Object> unitMap : textUnits) {
                unitInfoMap = new HashMap<>(16);
                if (Objects.equals(courseMap.get("id"), unitMap.get("courseId"))) {
                    if (id == null && courseUnitVo.getLearnUnit() == null) {
                        courseUnitVo.setLearnUnit(unitMap.get("id").toString());
                    }
                    unitInfoMap.put("unitId", unitMap.get("id"));
                    unitInfoMap.put("unitName", unitMap.get("unitName"));
                    if (unitMap.get("number") == null) {
                        unitInfoMap.put("number", 0);
                    } else {
                        unitInfoMap.put("number", unitMap.get("number"));
                    }
                    unitInfoMap.put("number", unitMap.get("number"));
                    if (unitMap.get("number") != null && (Long) unitMap.get("number") > 0) {
                        // 当前单元已进行过单元闯关，标记为已学习
                        unitInfoMap.put("state", 4);
                    } else {
                        // 当前单元还未学习
                        unitInfoMap.put("state", 1);
                    }
                    addCourseStatus(Long.parseLong(unitMap.get("id").toString()), studentId, unitInfoMap);
                    resultMap.add(unitInfoMap);
                }
            }
            courseUnitVo.setUnitVos(resultMap);
            courseUnitVos.add(courseUnitVo);
        }
        return ServerResponse.createBySuccess(courseUnitVos);
    }

    /**
     * 查询自定课程信息
     *
     * @param unitId
     * @param studentId
     * @param unitInfoMap
     */
    private void addCourseStatus(Long unitId, Long studentId, Map<String, Object> unitInfoMap) {
        List<Teks> id1 = teksMapper.selTeksByUnitId(unitId.intValue());
        Integer teksAudition = learnMapper.selLearnTeks(studentId, "课文试听", unitId);
        unitInfoMap.put("teksAudition", teksAudition != null && teksAudition > 0);
        List<Map<String, Object>> id2 = voiceMapper.selVoiceTeksByStudentAndUnit(unitId, studentId);
        if (id2 != null) {
            unitInfoMap.put("teksGoodVoice", id1.size() <= id2.size());
        } else {
            unitInfoMap.put("teksGoodVoice", false);
        }
        Integer teksTest = learnMapper.selLearnTeks(studentId, "课文默写测试", unitId);
        unitInfoMap.put("teksTest", teksTest != null && teksTest > 0);

        Integer testRecord = learnMapper.selLearnTeks(studentId, "课文测试", unitId);
        unitInfoMap.put("teksEntryTest", testRecord != null && testRecord > 0);
    }

    @Override
    public ServerResponse<Map<String, Object>> getIsInto(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        Map<String, Object> unitInfoMap = new HashMap<>();
        unitInfoMap.put("teksAudition", true);
        unitInfoMap.put("teksGoodVoice", true);
        Integer teksAudition = learnMapper.selLearnTeks(studentId, "课文试听", unitId);
        Integer teksTest = learnMapper.selLearnTeks(studentId, "课文默写测试", unitId);
        if (teksAudition != null && teksAudition != 0) {
            unitInfoMap.put("teksTest", true);
            if (teksTest != null && teksTest != 0) {
                TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitIdAndGenre(studentId, unitId, "课文默写测试");
                if (testRecord != null) {
                    unitInfoMap.put("testRecord", testRecord.getPoint() >= 70);
                } else {
                    unitInfoMap.put("testRecord", false);
                }

            } else {
                unitInfoMap.put("testRecord", false);
            }
        } else {
            unitInfoMap.put("teksTest", false);
            unitInfoMap.put("testRecord", false);
        }
        Map<String, Object> unitData = new HashMap<>();
        addCourseStatus(unitId, studentId, unitData);
        unitInfoMap.put("unitInfoMap", unitData);
        return ServerResponse.createBySuccess(unitInfoMap);
    }


    @Override
    public ServerResponse<Object> selWriteTeks(Integer unitId) {
        //获取课文句子
        List<Teks> listTeks = teksMapper.selTeksByUnitId(unitId);
        //返回的数据集合
        List<Object> resultTeks = new ArrayList<>();
        if (listTeks.size() > 0) {
            for (Teks teks : listTeks) {
                //getListTeks(resultTeks, teks);
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return ServerResponse.createByError();
    }

    @Override
    public void getListTeks(List<Object> resultTeks, TeksNew teks) {
        //保存返回的数据
        Map<String, Object> map = new HashMap<>();
        map.put("chinese", teks.getParaphrase());
        map.put("pronunciation", baiduSpeak.getSentencePath(teks.getSentence()));
        map.put("sentence", replace(teks.getSentence()));
        map.put("id", teks.getId());
        List<String> sentenceList = getLeterRightList(teks.getSentence());
        int[] integers;
        List<String> blanceSentence = new ArrayList<>();
        List<String> vocabulary = new ArrayList<>();
        if (sentenceList.size() > 2) {
            integers = wirterBlank(sentenceList);
            if (integers.length == 1) {
                //当空格数为一时调用
                for (int i = 0; i < sentenceList.size(); i++) {
                    if (i == (integers[0])) {
                        addList(sentenceList.get(i), blanceSentence, vocabulary);
                    } else {
                        vocabulary.add(sentenceList.get(i).replace("#", " ").replace("$", ""));
                        blanceSentence.add(sentenceList.get(i).replace("#", " ").replace("$", ""));
                    }
                }
                map.put("blanceSentence", blanceSentence);
                map.put("vocabulary", vocabulary);
            } else if (integers.length == 2) {
                //当空格数为二时调用
                for (int i = 0; i < sentenceList.size(); i++) {
                    if (i == (integers[0]) || i == (integers[1])) {
                        addList(sentenceList.get(i), blanceSentence, vocabulary);
                    } else {
                        String replace = replace(sentenceList.get(i));
                        vocabulary.add(replace);
                        blanceSentence.add(replace);
                    }
                }
                map.put("blanceSentence", blanceSentence);
                map.put("vocabulary", vocabulary);
            } else if (integers.length == 3) {
                //当空格数为三时调用
                for (int i = 0; i < sentenceList.size(); i++) {
                    if (i == (integers[0] - 1) || i == (integers[1] - 1) || i == (integers[2] - 1)) {
                        addList(sentenceList.get(i), blanceSentence, vocabulary);
                    } else {
                        vocabulary.add(sentenceList.get(i).replace("#", " ").replace("$", ""));
                        blanceSentence.add(sentenceList.get(i).replace("#", " ").replace("$", ""));
                    }
                }
                map.put("blanceSentence", blanceSentence);
                map.put("vocabulary", vocabulary);
            }

            resultTeks.add(map);

        } else {
            for (String sentence : sentenceList) {
                vocabulary.add(sentence);
                blanceSentence.add(sentence);
            }
            map.put("blanceSentence", blanceSentence);
            map.put("vocabulary", vocabulary);
            resultTeks.add(map);
        }
    }

    private List<String> getLeterRightList(String sentence) {
        // 正确顺序
        List<String> rightList = new ArrayList<>();
        String[] split = sentence.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (Pattern.matches(END_MATCH, s) && Pattern.matches(START_MATCH, s)) {
                rightList.add(s);
            } else {
                char[] chars = s.toCharArray();
                sb.setLength(0);
                int length = chars.length;
                for (int i = 0; i < length; i++) {
                    char aChar = chars[i];
                    // 当前下标的数据
                    String s1 = new String(new char[]{aChar});
                    // 是字母或者数字，拼接字符串
                    if (Pattern.matches(END_MATCH, s1)) {
                        sb.append(s1);
                    } else {
                        if (i == 0) {
                            rightList.add(s1);
                        } else {
                            if (i < (length - 1)) {
                                char longChar = chars[i + 1];
                                String s2 = new String(new char[]{longChar});
                                if (Pattern.matches(END_MATCH2, s2)) {
                                    sb.append(s1);
                                } else {
                                    if (sb.length() > 0) {
                                        rightList.add(sb.toString());
                                        sb.setLength(0);
                                    }
                                    rightList.add(s1);
                                }
                            } else {
                                if (sb.length() > 0) {
                                    rightList.add(sb.toString());
                                    sb.setLength(0);
                                }
                                rightList.add(s1);
                            }

                        }
                    }

                    // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                    if (sb.length() > 0 && i == length - 1) {
                        rightList.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        }
        return rightList;
    }

    @Override
    @TestChangeAnnotation(isUnitTest = false)
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> addData(TestRecord testRecord, HttpSession session, Long flowId) {
        //学生对象
        Student student = super.getStudent(session);
        saveTeksData.insertLearnExtend(flowId, testRecord.getUnitId(), student, "课文训练", 2, 3);
        Integer group = testService.getGroup(testRecord.getUnitId(), student.getId(), 2, 3);
        final String model = "课文默写测试";
        //测试开始时间
        //测试结束时间
        Date endTime = new Date();
        saveTestRecordTime(testRecord, session, endTime);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        //添加学习模块
        testRecord.setStudyModel(model);
        //获取课程id
        Long aLong = teksUnitMapper.selectCourseIdByUnitId(testRecord.getUnitId());
        //添加金币
        WordUnitTestDTO wordUnitTestDTO = new WordUnitTestDTO();
        wordUnitTestDTO.setClassify(7);
        Integer point = testRecord.getPoint();
        wordUnitTestDTO.setPoint(point);
        wordUnitTestDTO.setCourseId(aLong);
        wordUnitTestDTO.setUnitId(new Long[]{testRecord.getUnitId()});

        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(), testRecord.getUnitId(), model, model);

        int goldCount = this.getGold(testRecord, student, testRecordOld, 7);
        testRecord.setGenre(model);
        testRecord.setAwardGold(goldCount);
        testRecord.setStudentId(student.getId());
        testRecord.setCourseId(aLong);
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());

        getLevel(session);
        double gold = StudentGoldAdditionUtil.getGoldAddition(student, goldCount + 0.0);
        if (student.getBonusExpires() != null) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
        }
        // 封装响应数据
        Map<String, Object> map = packageResultMap(student, wordUnitTestDTO, point, goldCount, testRecord, model, group);
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldCount));
        studentMapper.updateById(student);
        int insert = testRecordMapper.insert(testRecord);

        if (insert > 0) {
            Learn learn = new Learn();
            learn.setType(1);
            learn.setStudyModel(testRecord.getGenre());
            learn.setStudentId(testRecord.getStudentId());
            learn.setCourseId(testRecord.getCourseId());
            learn.setUnitId(testRecord.getUnitId());
            learn.setUpdateTime(new Date());
            learn.setLearnTime(new Date());
            Long aLong1 = learnMapper.selTeksLearn(learn);
            if (aLong1 != null) {
                learn.setId(aLong1);
                learnMapper.updTeksLearn(learn);
            } else {
                learnMapper.insert(learn);
            }
        }

        return ServerResponse.createBySuccess(map);
    }

    private int getGold(TestRecord testRecord, Student student, TestRecord testRecordOld, Integer classify) {

        int goldCount = 0;
        if (testRecordOld == null) {
            goldCount = getGoldCount(classify, student, testRecord.getPoint(), testRecord.getStudyModel());
        } else {
            // 查询当前单元测试历史最高分数
            int betterPoint = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), testRecord.getUnitId(), 7);

            // 非首次测试成绩本次测试成绩大于历史最高分，超过历史最高分次数 +1并且金币奖励翻倍
            if (betterPoint < testRecord.getPoint()) {
                int betterCount = testRecordOld.getBetterCount() + 1;
                testRecord.setBetterCount(betterCount);
                goldCount = getGoldCount(classify, student, testRecord.getPoint(), testRecord.getStudyModel());
            }
        }
        return testGoldUtil.addGold(student, goldCount);
    }

    private int getGoldCount(Integer classify, Student student, int point, String model) {
        int goldCount;
        if (point < SIX) {
            goldCount = 0;
        } else if (point < SEVENTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SIXTY_TO_SEVENTY;
        } else if (point < PASS) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SEVENTY_TO_EIGHTY;
        } else if (point < NINETY_POINT) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_NINETY;
        } else if (point < FULL_MARK) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_NINETY_TO_FULL;
        } else {
            goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
        }
        this.saveLog(student, goldCount, classify, model);
        return goldCount;
    }

    /**
     * 保存金币变化日志信息
     *
     * @param student
     * @param goldCount 奖励金币数
     * @param classify
     * @param model     测试模块
     */
    private void saveLog(Student student, int goldCount, Integer classify, String model) {
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldCount));
        studentMapper.updateByPrimaryKeySelective(student);
        String msg;
        String reason;
        if (classify != null) {
            reason = GenreConstant.UNIT_TEST;
            msg = "id为：" + student.getId() + "的学生在[" + commonMethod.getTestType(classify)
                    + "]模块下的单元闯关测试中首次闯关成功，获得#" + goldCount + "#枚金币";
        } else {
            reason = model;
            msg = "id为：" + student.getId() + "的学生在[" + model + "]模块下，获得#" + goldCount + "#枚金币";
        }
        if (goldCount > 0) {
            try {
                GoldLogUtil.saveStudyGoldLog(student.getId(), reason, goldCount);
            } catch (RuntimeException e) {
                log.error("保存学生[{} - {} - {}]日志出错！msg=[{}]", student.getId(), student.getAccount(), student.getStudentName(), msg, e);
            }
        }
    }

    private Map<String, Object> packageResultMap(Student student, WordUnitTestDTO wordUnitTestDTO, Integer point,
                                                 Integer goldCount, TestRecord testRecord, String model, Integer group) {
        Map<String, Object> map = new HashMap<>(16);
        int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                testRecord.getStudyModel(), model, group);
        int energy = super.getEnergy(student, wordUnitTestDTO.getPoint(), number);
        map.put("energy", energy);
        map.put("gold", goldCount);
        if (point < PASS) {
            map.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            map.put("text", "很遗憾，闯关失败，再接再厉。");
            map.put("backMsg", new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
            testRecord.setPass(2);
        } else if (point < NINETY_POINT) {
            map.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            map.put("text", "闯关成功，独孤求败！");
            map.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        } else {
            map.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            map.put("text", "恭喜你刷新了纪录！");
            map.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        }
        map.put("point", point);
        map.put("imgUrl", PetUrlUtil.getTestPetUrl(student, point, "单元闯关测试", PASS));
        return map;
    }

    @Override
    public ServerResponse<Object> selHistoryPronunciation(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> maps = new HashMap<>(16);
        maps.put("unitId", unitId);
        maps.put("studentId", student.getId());
        List<Map<String, Object>> map = teksMapper.selHistoryPronunciation(maps);
        Map<String, Map<String, Object>> isMap = new HashMap<>();
        map.forEach(sMap -> {
            String wordId = sMap.get("wordId").toString();
            Map<String, Object> map1 = isMap.get(wordId);
            if (map1 == null) {
                isMap.put(wordId, sMap);
            } else {
                int sVoiceId = Integer.parseInt(sMap.get("voiceId").toString());
                int gVoiceId = Integer.parseInt(map1.get("voiceId").toString());
                if (gVoiceId < sVoiceId) {
                    isMap.put(wordId, sMap);
                }
            }
        });
        Set<String> strings = isMap.keySet();
        List<Map<String, Object>> getMaps = new ArrayList<>();
        strings.forEach(string -> {
            getMaps.add(isMap.get(string));
        });
        List<Map<String, Object>> resultMap = new ArrayList<>();
        String url;
        for (Map<String, Object> getMap : getMaps) {
            url = getMap.get("url") == null ? "" : getMap.get("url").toString();
            getMap.put("url", url);
            String sentence = getMap.get("sentence").toString();
            getMap.put("sentence", replace(sentence));
            resultMap.add(getMap);
        }
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<Object> isHistoryPronunciation(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> maps = new HashMap<>();
        maps.put("unitId", unitId);
        maps.put("studentId", student.getId());
        Integer map = teksMapper.isHistoryPronunciation(maps);
        if (map != null && map > 0) {
            return ServerResponse.createBySuccess(true);
        } else {
            return ServerResponse.createBySuccess(false);
        }
    }


    //判断空格出现位置
    public int[] wirterBlank(List<String> strList) {
        Random random = new Random();
        List<Integer> shuZhuString = new ArrayList<>();
        for (int i = 0; i < strList.size(); i++) {
            boolean flag = true;
            String str = strList.get(i);
            for (String s : NAMELIST) {

                if (str.contains(s)) {
                    flag = false;
                    break;
                }
            }
            if (!Pattern.matches(END_MATCH, strList.get(i)) && !Pattern.matches(START_MATCH, strList.get(i))) {
                flag = false;
            }

            if (str.contains("$") || str.contains("#")) {
                flag = false;
            }
            if (flag) {
                shuZhuString.add(i);
            }
        }

        int choose;
        if (shuZhuString.size() > 3) {
            choose = 2;
        } else {
            choose = 1;
        }
        int[] integers = new int[choose];
        if (shuZhuString.size() == 0) {
            return integers;
        }
        int s = 0;
        if (choose == 1) {
            int i = random.nextInt(shuZhuString.size());
            integers[0] = shuZhuString.get(i);
        }
        if (choose == 2) {
            int index = 0;
            while (integers[choose - 1] == 0) {
                int i = random.nextInt(shuZhuString.size());
                if (s == 1) {
                    if (i != index) {
                        integers[s] = shuZhuString.get(i);
                    }
                } else {
                    integers[s] = shuZhuString.get(i);
                    index = i;
                }
                if (s != 1) {
                    s++;
                }
            }
        }
        return integers;
    }


    /**
     * 添加数据
     */
    public void addList(String str, List<String> blanceSentence, List<String> vocabulary) {
        vocabulary.add(str.replace("#", " ").replace("$", ""));
        blanceSentence.add(null);
    }

    /**
     * 获取课文测试
     */
    @Override
    public ServerResponse<Object> getTeksTest(HttpSession session, Integer unitId) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        //获取单元中的课文语句
        List<TeksNew> teks = teksNewMapper.getTwentyTeks();
        return getTeks(teks);
    }

    @Override
    public ServerResponse<Object> getTeks(List<TeksNew> teks) {
        //第一步去除课文中只有一句话的句子
        List<TeksNew> useTeks = new ArrayList<>();
        for (TeksNew teks1 : teks) {
            String sentence = teks1.getSentence();
            String[] s = sentence.split(" ");
            boolean isTrue = true;
            if (s.length <= 1) {
                isTrue = false;
            }
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < s.length; i++) {
                if (!s[i].contains("#") && !s[i].contains("$")) {
                    list.add(i);
                }
            }
            if (list.size() <= 0) {
                isTrue = false;
            }
            if (isTrue) {
                useTeks.add(teks1);
            }
        }
        //附加项选择
        List<TeksNew> addTeks = null;
        //根据清楚后的语句小于4句时添加
        if (useTeks.size() < 4) {
            addTeks = courseFeignClient.getTwentyTeks();
        }
        return getReturnTestTeks(useTeks, addTeks);
    }

    @Override
    public ServerResponse<Map<String, Object>> saveTeksAudition(HttpSession session, Integer unitId, Integer
            courseId) {
        Student student = getStudent(session);
        Learn learn = new Learn();
        learn.setType(1);
        learn.setStudentId(student.getId());
        learn.setUnitId(unitId.longValue());
        learn.setCourseId(courseId.longValue());
        learn.setStudyModel("课文试听");
        learn.setLearnTime(new Date());
        learn.setUpdateTime(new Date());
        Long learnId = learnMapper.selTeksLearn(learn);
        if (learnId != null) {
            learn.setId(learnId);
            Integer integer = learnMapper.updTeksLearn(learn);
            if (integer < 1) {
                return ServerResponse.createByError(300, "添加课文试听学习记录未成功");
            }
        } else {
            int insert = learnMapper.insert(learn);
            if (insert < 1) {
                return ServerResponse.createByError(300, "添加课文试听学习记录未成功");
            }
        }
        return ServerResponse.createBySuccess(200, "添加课文试听学习记录成功");
    }

    @Override
    public ServerResponse<List<Map<String, Object>>> getTeksLaterLearnTime(HttpSession session) {
        //获取学生id
        Student student = getStudent(session);
        //获取学习时间
        List<Map<String, Object>> list = learnMapper.selectTeksLaterLearnTimeByStudentId(student.getId());
        List<Map<String, Object>> resultList = new ArrayList<>();
        //获取学生学习单元时间
        for (Map<String, Object> map : list) {
            Date learnTime = (Date) map.get("update_time");
            String time = learnTime.toString();
            if (StringUtils.isNotEmpty(time)) {
                map.put("update_time", CalculateTimeUtil.calculateTime(time));
            } else {
                map.put("update_time", "");
            }
            // sort用于时间排序
            time = time.replaceAll("[\\pP\\pS\\pZ]", "");
            map.put("sort", time + "");
            resultList.add(map);
        }
        return ServerResponse.createBySuccess(resultList);
    }


    @Override
    public ServerResponse<Object> getReturnTestTeks(List<TeksNew> teks, List<TeksNew> addTeks) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> choseFour = new ArrayList<>();
        List<Map<String, Object>> hearingList = new ArrayList<>();
        List<TeksNew> optionList = new ArrayList<>(teks);
        //去除附加选项中与原课文语句相同的语句
        if (addTeks != null && addTeks.size() > 0) {
            for (TeksNew teks1 : addTeks) {
                boolean isAdd = true;

                for (TeksNew teks2 : optionList) {
                    if (teks2.getSentence().equals(teks1.getSentence())) {
                        isAdd = false;
                        break;
                    }
                }
                if (isAdd) {
                    optionList.add(teks1);
                }
            }
        }
        //判断课文语句数量
        if (teks.size() < 4) {
            //语句小于4全部为听力
            hearing(teks, returnList, optionList);
        } else {
            //打乱顺序
            Collections.shuffle(teks);
            //当语句等于4个 为四选四题目
            if (teks.size() == 4) {
                //取前4个句子为四选四句子
                fourChooseFour(teks, returnList);
            } else {
                int fourLength = teks.size() / 5;
                int i = teks.size() % 5;
                if (i != 4) {
                    //获取四选四习题
                    List<TeksNew> fourOption = teks.subList(0, fourLength * 4);
                    fourChooseFour(fourOption, choseFour);
                    //获取听力习题
                    List<TeksNew> hearOption = teks.subList(fourLength * 4, teks.size());
                    hearing(hearOption, hearingList, optionList);
                } else {
                    //获取四选四习题
                    List<TeksNew> fourOption = teks.subList(0, (fourLength + 1) * 4);
                    fourChooseFour(fourOption, choseFour);
                    //获取听力习题
                    List<TeksNew> hearOption = teks.subList((fourLength + 1) * 4, teks.size());
                    hearing(hearOption, hearingList, optionList);
                }
                //排列习题的顺序
                arrayList(choseFour, hearingList, returnList);
            }
        }
        return ServerResponse.createBySuccess(returnList);
    }

    //排列习题循序
    private void arrayList
    (List<Map<String, Object>> choseFour, List<Map<String, Object>> hearingList, List<Map<String, Object>> returnList) {
        if (choseFour.size() >= hearingList.size()) {
            for (int i = 0; i < choseFour.size(); i++) {
                returnList.add(choseFour.get(i));
                if (hearingList.size() - 1 >= i) {
                    returnList.add(hearingList.get(i));
                }
            }
        } else {
            for (int i = 0; i < hearingList.size(); i++) {
                if (choseFour.size() - 1 >= i) {
                    returnList.add(choseFour.get(i));
                }
                returnList.add(hearingList.get(i));
            }
        }
    }

    //四选四题目获取
    private void fourChooseFour(List<TeksNew> optionList, List<Map<String, Object>> choseFour) {
        for (int i = 0; i < optionList.size(); i += 4) {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("type", "fourChoseFour");
            List<TeksNew> teks = optionList.subList(i, i + 4);
            List<Object> objects = new ArrayList<>();
            List<Map<String, Object>> answers = new ArrayList<>();
            List<String> returnAnswers = new ArrayList<>();
            for (int s = 0; s < teks.size(); s++) {
                answersFour(teks.get(s), objects, answers, returnAnswers);
            }
            Collections.shuffle(answers);
            returnMap.put("option", objects);
            returnMap.put("subject", answers);
            returnMap.put("answer", returnAnswers);
            choseFour.add(returnMap);
        }
    }

    //四选四题目挖空并储存选项和答案
    private void answersFour(TeksNew teks, List<Object> senOption, List<Map<String, Object>> answers, List<String> returnAnswers) {
        Integer teksId = teks.getId();
        List<String> regitList = getLeterRightList(teks.getSentence());
        List<String> arrList = new ArrayList<>();
        int location = 0;
        //句子在teksId为103935时挖空位置固定为0
        if (!teksId.equals(103935)) {
            location = changeInteger(regitList);
        }
        for (int i = 0; i < regitList.size(); i++) {
            Map<String, Object> returnMap = new HashMap<>();
            if (i == location) {
                String option = regitList.get(i);
                arrList.add(null);
                returnMap.put("name", 5);
                returnMap.put("value", option);
                returnAnswers.add(option);
                answers.add(returnMap);
            } else {
                String option = regitList.get(i);
                arrList.add(replace(option));
            }
        }
        senOption.add(arrList);
    }

    //获取挖空的位置
    private int changeInteger(List<String> sentenceList) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < sentenceList.size(); i++) {
            if (!sentenceList.get(i).contains("#") && !sentenceList.get(i).contains("$")) {
                if (Pattern.matches(END_MATCH, sentenceList.get(i)) && Pattern.matches(START_MATCH, sentenceList.get(i))) {
                    list.add(i);
                }
            }
        }
        int integer = (int) Math.ceil(Math.random() * (list.size() - 1));
        return list.get(integer);
    }

    //获取听力题
    private void hearing(List<TeksNew> hearingList, List<Map<String, Object>> returnList, List<TeksNew> optionList) {
        for (TeksNew teksNew : hearingList) {
            Collections.shuffle(optionList);
            List<TeksNew> list = new ArrayList<>();
            for (TeksNew teks : optionList) {
                if (!teks.getSentence().trim().equals(teksNew.getSentence().trim())) {
                    list.add(teks);
                }
            }
            list.remove(teksNew);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("id", teksNew.getId());
            returnMap.put("chinese", teksNew.getParaphrase());
            returnMap.put("pronunciation", baiduSpeak.getSentencePath(teksNew.getSentence()));
            returnMap.put("english", replace(teksNew.getSentence()));
            returnMap.put("type", "HanElectBritish");
            HanElectBritish(teksNew, new ArrayList<>(list.subList(0, 3)), returnMap);
            returnList.add(returnMap);
        }
    }

    private void HanElectBritish(TeksNew teks, List<TeksNew> optionList, Map<String, Object> returnMap) {
        List<String> option = new ArrayList<>();
        String sentence = replace(teks.getSentence());
        option.add(sentence);
        for (int i = 0; i < 3; i++) {
            option.add(replace(optionList.get(i).getSentence()));
        }
        Collections.shuffle(option);
        for (int i = 0; i < option.size(); i++) {
            if (option.get(i).equals(sentence)) {
                returnMap.put("answer", i);
            }
        }
        returnMap.put("option", option);

    }

    private void getStudyUnit(List<Long> courseIds, List<Map<String, Object>> returnCourse, Long studentId) {
        for (int i = 0; i < courseIds.size(); i++) {
            List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selByStudentIdAndCourseId(studentId, courseIds.get(i), 3);
            if (studentStudyPlans.size() > 1) {
                for (StudentStudyPlan studentStudyPlan : studentStudyPlans) {
                    List<Map<String, Object>> maps = teksUnitMapper.selectByStudentIdAndCourseIdAndStartUnitIdAndEndUnitId(courseIds.get(i),
                            studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId(), studentId);
                    for (int j = 0; j < maps.size(); j++) {
                        boolean contains = returnCourse.contains(maps.get(j));
                        if (contains) {
                            maps.remove(maps.get(i));
                        }
                    }
                    returnCourse.addAll(maps);
                }
            } else if (studentStudyPlans.size() == 1) {
                List<Map<String, Object>> maps = teksUnitMapper.selectByStudentIdAndCourseIdAndStartUnitIdAndEndUnitId(courseIds.get(i),
                        studentStudyPlans.get(0).getStartUnitId(), studentStudyPlans.get(0).getEndUnitId(), studentId);
                returnCourse.addAll(maps);
            }
        }
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项  为空时无干扰项
     * @return
     */
    public List<String> getOrderEnglishList(String sentence, String exampleDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " ");
                    if (".".equals(sentence.substring(sentence.length() - 1))) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                    }
                } else {
                    sentence = sentence.replace(s, "");
                }
            }
        }
        // 将例句按照空格拆分
        String[] words = sentence.split(" ");

        List<String> list = new ArrayList<>();
        // 去除固定搭配中的#
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("#")) {
                words[i] = words[i].replace("#", " ");
            }
            if (words[i].contains("*")) {
                words[i] = words[i].replace("*", " ");
            }
            if (words[i].contains("$")) {
                words[i] = words[i].replace("$", " ");
            }
            // 防止形如 My name is $Jenny$#Green. 的句子中 Jenny Green 中间含有两个空格问题
            if (words[i].contains("  ")) {
                words[i] = words[i].replace("  ", " ");
            }
            list.add(words[i].trim());
        }

        if (StringUtils.isNotEmpty(exampleDisturb)) {
            list.add(exampleDisturb.replace("#", " "));
        }

        Collections.shuffle(list);
        return list;
    }

}

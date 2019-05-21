package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.CalculateTimeUtil;
import com.zhidejiaoyu.common.utils.goldUtil.TestGoldUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.common.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.service.TeksService;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;


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
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    private static final List<String> NAMELIST = new ArrayList<String>() {{
        add("Zhang");
        add("Wu");
        add("Yifan");
        add("Wang");
        add("Han");
        add("Amy");
    }};


    @Autowired
    private TeksMapper teksMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;
    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private UnitMapper unitMapper;

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

    @Value("${ftp.prefix}")
    private String prefix;

    @Autowired
    private TeksCourseMapper teksCourseMapper;

    @Autowired
    private TeksUnitMapper teksUnitMapper;

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
    public ServerResponse<Object> selSpeakTeksByUnitId(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> map = new HashMap<>();
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        Map<String, Object> getMap = new HashMap<>();
        getMap.put("studentId", student.getId());
        getMap.put("unitId", unitId);
        Integer integer = voiceMapper.selMaxCountByUnitIdAndStudentId(getMap);
        if (teks.size() > 0) {
            List<Teks> resultTeks = new ArrayList<>();
            int i = 0;
            for (Teks teks1 : teks) {
                teks1.setPronunciation(baiduSpeak.getSentencePath(teks1.getSentence()).replace("#", " ").replace("$", ""));
                teks1.setSentence(teks1.getSentence().replace("#"," ").replace("$",""));
                i++;
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
            getMap.put("url", prefix + getMap.get("url"));
            resultMap.add(getMap);
        }
        result.put("list", resultMap);
        result.put("count", count);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> selRankingList(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> getMap = new HashMap<>();
        getMap.put("schoolName", student.getSchoolName());
        getMap.put("unitId", unitId);
        Map<String, Object> result = new HashMap<>();
        //全国排名
        List<Map<String, Object>> maps = voiceMapper.selectTeksRank(getMap);
        result.put("nationalRanking", maps);
        //全校排名
        List<Map<String, Object>> mapss = voiceMapper.selectTeksRankSchool(getMap);
        result.put("shcoolRanking", mapss);
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
                map.put("pronunciation", baiduSpeak.getSentencePath(teks1.getSentence().replace("#", " ").replace("$", "")));
                map.put("id", teks1.getId());
                String[] sentenceList = teks1.getSentence().trim().split(" ");
                List blankSentenceArray = new ArrayList();
                List sentence = new ArrayList();
                //获取填空位置
                for (int i = 0; i < sentenceList.length; i++) {
                    sentenceList[i] = sentenceList[i].trim();
                    if (sentenceList[i].endsWith(",") || sentenceList[i].endsWith(".") || sentenceList[i].endsWith("?") || sentenceList[i].endsWith("!")) {
                        blankSentenceArray.add(null);
                        if (sentenceList[i].endsWith("...")) {
                            blankSentenceArray.add(sentenceList[i].substring(sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                            sentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                            sentence.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                        } else {
                            blankSentenceArray.add(sentenceList[i].substring(sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                            sentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                            sentence.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                        }

                    } else {
                        blankSentenceArray.add(null);
                        sentence.add(sentenceList[i].replace("#", " ").replace("$", ""));
                    }
                    //返回的填空单词 以及句子填空位置
                    if (teks1.getSentence().indexOf("...") != -1) {
                        String substring = teks1.getSentence().replace("...", "");
                        map.put("vocabularyArray", getOrderEnglishList(substring, null));
                    } else {
                        map.put("vocabularyArray", getOrderEnglishList(teks1.getSentence(), null));
                    }
                    map.put("blankSentenceArray", blankSentenceArray);
                    map.put("sentence", sentence);
                }
                resultList.add(map);
                resultMap.put("list", resultList);
                resultMap.put("number", teks.size());
                Map<String, Object> selMap = new HashMap<>();
                selMap.put("unitId", unitId);
                selMap.put("studentId", student.getId());
                selMap.put("model", "课文默写测试");
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
    @SuppressWarnings("all")
    public ServerResponse<Map<String, Object>> getCourseAndUnit(HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        List<CourseUnitVo> courseUnitVos = new ArrayList<>();
        CourseUnitVo courseUnitVo;
        Map<String, Object> studyMap = null;
        List<Map<String, Object>> resultMap;
        Map<String, Object> returnMap = new HashMap<>();
        // 学生所有课程id及课程名
        /* List<Map<String, Object>> courses = courseMapper.selectTextCourseIdAndCourseNameByStudentId(studentId);*/
        List<Map<String, Object>> courses = studentStudyPlanMapper.selByStudentId(studentId, 3);
        if (courses == null || courses.size() == 0) {
            return ServerResponse.createByError(400,"当前学生没有课程，请让老师添加");
        }
        // 学生课程下所有例句的单元id及单元名
        if (courses.size() > 0) {
            List<Long> courseIds = new ArrayList<>(courses.size());
            courses.forEach(map -> courseIds.add((Long) map.get("id")));
            // 获取课程下所有课文的单元信息
            List<Map<String, Object>> textUnits = new ArrayList<>();
            this.getStudyUnit(courseIds, textUnits, studentId);
            Map<String, Object> learnUnit = learnMapper.selTeksLaterCourse(student.getId());
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
                    studyMap.put("unitId", learnUnit.get("unit_id"));
                    studyMap.put("version", learnUnit.get("version"));
                    studyMap.put("grade", learnUnit.get("grade").toString() + learnUnit.get("label").toString());
                }
            }
            // 已经进行过单元闯关的单元
            Map<Long, Map<Long, Long>> testMap = null;
            if (textUnits.size() > 0) {
                List<Long> unitIds = new ArrayList<>(textUnits.size());
                textUnits.forEach(map -> unitIds.add((Long) map.get("id")));
                testMap = testRecordMapper.selectHasUnitTest(studentId, unitIds);
            }
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
                courseUnitVo.setGrad(courseMap.get("grade").toString() + courseMap.get("label").toString());
                // 存放单元信息
                Map<String, Object> unitInfoMap;
                for (Map<String, Object> unitMap : textUnits) {
                    unitInfoMap = new HashMap<>(16);
                    if (studyMap == null) {
                        studyMap = new HashMap<>();
                        studyMap.put("unitId", unitMap.get("id"));
                        studyMap.put("version", unitMap.get("version"));
                        studyMap.put("grade", unitMap.get("grade").toString() + unitMap.get("label").toString());
                    }
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
                        List<Teks> id1 = teksMapper.selTeksByUnitId(((Long) unitMap.get("id")).intValue());
                        Integer teksAudition = learnMapper.selLearnTeks(studentId, "课文试听", (Long) unitMap.get("id"));
                        if (teksAudition != null && teksAudition > 0) {
                            unitInfoMap.put("teksAudition", true);
                        } else {
                            unitInfoMap.put("teksAudition", false);
                        }
                        List<Map<String, Object>> id2 = voiceMapper.selVoiceTeksByStudentAndUnit((Long) unitMap.get("id"), studentId);
                        if (id2 != null) {
                            if (id1.size() <= id2.size()) {
                                unitInfoMap.put("teksGoodVoice", true);
                            } else {
                                unitInfoMap.put("teksGoodVoice", false);
                            }
                        } else {
                            unitInfoMap.put("teksGoodVoice", false);
                        }
                        Integer teksTest = learnMapper.selLearnTeks(studentId, "课文默写测试", (Long) unitMap.get("id"));
                        if (teksTest != null && teksTest > 0) {
                            unitInfoMap.put("teksTest", true);
                        } else {
                            unitInfoMap.put("teksTest", false);
                        }

                        Integer testRecord = learnMapper.selLearnTeks(studentId, "课文测试", (Long) unitMap.get("id"));
                        if (testRecord != null && testRecord > 0) {
                            unitInfoMap.put("teksEntryTest", true);
                        } else {
                            unitInfoMap.put("teksEntryTest", false);
                        }
                        resultMap.add(unitInfoMap);

                    }
                }
                courseUnitVo.setUnitVos(resultMap);
                courseUnitVos.add(courseUnitVo);
            }
            List<Map<String, Object>> testList = teksMapper.getStudentAllCourse(studentId, courseIds);
            returnMap.put("present", studyMap);
            returnMap.put("versionList", testList);
            returnMap.put("list", courseUnitVos);
        }


        return ServerResponse.createBySuccess(returnMap);
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
                if(testRecord!=null){
                    if (testRecord.getPoint() >= 70) {
                        unitInfoMap.put("testRecord", true);
                    } else {
                        unitInfoMap.put("testRecord", false);
                    }
                }else{
                    unitInfoMap.put("testRecord", false);
                }

            } else {
                unitInfoMap.put("testRecord", false);
            }
        } else {
            unitInfoMap.put("teksTest", false);
            unitInfoMap.put("testRecord", false);
        }

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
                //保存返回的数据
                Map<String, Object> map = new HashMap<>();
                map.put("chinese", teks.getParaphrase());
                map.put("pronunciation", baiduSpeak.getSentencePath(teks.getSentence()).replace("#", " ").replace("$", ""));
                map.put("sentence", teks.getSentence().replace("#", " ").replace("$", ""));
                map.put("id", teks.getId());
                String[] sentenceList = teks.getSentence().split(" ");
                for (int i = 0; i < sentenceList.length; i++) {
                    sentenceList[i] = sentenceList[i].trim();
                }
                //获取空格出现的位置
               /* if(sentenceList.length>6){
                    integers=wirterBlank(sentenceList.length,3);
                }else*/
                int[] integers;
                List<String> blanceSentence = new ArrayList<>();
                List<String> vocabulary = new ArrayList<>();
                if (sentenceList.length != 1) {
                    integers = wirterBlank(sentenceList);
                    if (integers.length == 1) {
                        //当空格数为一时调用
                        for (int i = 0; i < sentenceList.length; i++) {
                            if (i == (integers[0])) {
                                addList(sentenceList[i], blanceSentence, vocabulary);
                            } else {
                                if (sentenceList[i].endsWith(",") || sentenceList[i].endsWith(".") || sentenceList[i].endsWith("?") || sentenceList[i].endsWith("!")) {
                                    if (sentenceList[i].endsWith("...")) {
                                        vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                        vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                        blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                        blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                    } else {
                                        vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                        blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                    }
                                } else {
                                    vocabulary.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                    blanceSentence.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                }
                            }
                        }
                        map.put("blanceSentence", blanceSentence);
                        map.put("vocabulary", vocabulary);
                    } else if (integers.length == 2) {
                        //当空格数为二时调用
                        for (int i = 0; i < sentenceList.length; i++) {
                            if (i == (integers[0]) || i == (integers[1])) {
                                addList(sentenceList[i], blanceSentence, vocabulary);
                            } else {
                                if (sentenceList[i].endsWith(",") || sentenceList[i].endsWith(".") || sentenceList[i].endsWith("?") || sentenceList[i].endsWith("!")) {
                                    if (sentenceList[i].endsWith("...")) {
                                        if (sentenceList[i].length() == 3) {
                                            vocabulary.add(sentenceList[i]);
                                            blanceSentence.add(sentenceList[i]);
                                        } else {
                                            vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                            vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                            blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                            blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                        }
                                    } else {
                                        vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                        blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                    }
                                } else {
                                    vocabulary.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                    blanceSentence.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                }
                            }
                        }
                        map.put("blanceSentence", blanceSentence);
                        map.put("vocabulary", vocabulary);
                    } else if (integers.length == 3) {
                        //当空格数为三时调用
                        for (int i = 0; i < sentenceList.length; i++) {
                            if (i == (integers[0] - 1) || i == (integers[1] - 1) || i == (integers[2] - 1)) {
                                addList(sentenceList[i], blanceSentence, vocabulary);
                            } else {
                                if (sentenceList[i].endsWith(",") || sentenceList[i].endsWith(".") || sentenceList[i].endsWith("?") || sentenceList[i].endsWith("!")) {
                                    if (sentenceList[i].endsWith("...")) {
                                        vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                        vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                        blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 3).replace("#", " ").replace("$", ""));
                                        blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 3));
                                    } else {
                                        vocabulary.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        vocabulary.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                        blanceSentence.add(sentenceList[i].substring(0, sentenceList[i].length() - 1).replace("#", " ").replace("$", ""));
                                        blanceSentence.add(sentenceList[i].substring(sentenceList[i].length() - 1));
                                    }
                                } else {
                                    vocabulary.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                    blanceSentence.add(sentenceList[i].replace("#", " ").replace("$", ""));
                                }
                            }
                        }
                        map.put("blanceSentence", blanceSentence);
                        map.put("vocabulary", vocabulary);
                    }

                    resultTeks.add(map);

                } else {
                    if (sentenceList[0].endsWith(",") || sentenceList[0].endsWith(".") || sentenceList[0].endsWith("?") || sentenceList[0].endsWith("!")) {
                        if (sentenceList[0].endsWith("...")) {
                            vocabulary.add(sentenceList[0].substring(0, sentenceList[0].length() - 3));
                            vocabulary.add(sentenceList[0].substring(sentenceList[0].length() - 3));
                            blanceSentence.add(sentenceList[0].substring(0, sentenceList[0].length() - 3));
                            blanceSentence.add(sentenceList[0].substring(sentenceList[0].length() - 3));
                        } else {
                            vocabulary.add(sentenceList[0].substring(0, sentenceList[0].length() - 1));
                            vocabulary.add(sentenceList[0].substring(sentenceList[0].length() - 1));
                            blanceSentence.add(sentenceList[0].substring(0, sentenceList[0].length() - 1));
                            blanceSentence.add(sentenceList[0].substring(sentenceList[0].length() - 1));
                        }
                    } else {
                        vocabulary.add(sentenceList[0]);
                        blanceSentence.add(sentenceList[0]);
                    }
                    map.put("blanceSentence", blanceSentence);
                    map.put("vocabulary", vocabulary);
                }
            }
            return ServerResponse.createBySuccess(resultTeks);
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<Object> addData(TestRecord testRecord, HttpSession session) {
        //学生对象
        Student student = super.getStudent(session);
        final String model = "课文默写测试";
        //测试开始时间
        Date startTime = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        //测试结束时间
        Date endTime = new Date();
        //添加学习模块
        testRecord.setStudyModel(model);
        //获取课程id
        Long aLong = teksUnitMapper.selectCourseIdByUnitId(testRecord.getUnitId());
        //添加金币
        WordUnitTestDTO wordUnitTestDTO = new WordUnitTestDTO();
        wordUnitTestDTO.setClassify(7);
        Integer point = testRecord.getPoint();
        wordUnitTestDTO.setPoint(point);

        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(), testRecord.getUnitId(), model, model);

        int goldCount = this.getGold(testRecord, student, testRecordOld, 7);
        testRecord.setGenre(model);
        testRecord.setAwardGold(goldCount);
        testRecord.setStudentId(student.getId());
        testRecord.setCourseId(aLong);
        testRecord.setTestStartTime(startTime);
        testRecord.setTestEndTime(endTime);
        getLevel(session);
        if(student.getBonusExpires()!=null){
            if(student.getBonusExpires().getTime()>System.currentTimeMillis()){
                Double doubleGOld=goldCount*0.2;
                student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), doubleGOld));
                goldCount=goldCount+doubleGOld.intValue();
            }
        }
        // 封装响应数据
        Map<String, Object> map = packageResultMap(student, wordUnitTestDTO, point, goldCount, testRecord);
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldCount));
        studentMapper.updateByPrimaryKeySelective(student);
        Integer insert = testRecordMapper.insert(testRecord);

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
        if (classify != null) {
            msg = "id为：" + student.getId() + "的学生在" + commonMethod.getTestType(classify)
                    + " 模块下的单元闯关测试中首次闯关成功，获得#" + goldCount + "#枚金币";
        } else {
            msg = "id为：" + student.getId() + "的学生在" + model + " 模块下，获得#" + goldCount + "#枚金币";
        }
        RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
        runLog.setCourseId(student.getCourseId());
        runLog.setUnitId(student.getUnitId());
        runLogMapper.insert(runLog);
    }

    private Map<String, Object> packageResultMap(Student student, WordUnitTestDTO wordUnitTestDTO, Integer point, Integer goldCount, TestRecord testRecord) {
        Map<String, Object> map = new HashMap<>(16);
        int energy = super.getEnergy(student, wordUnitTestDTO.getPoint());
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
        map.put("imgUrl", student.getPartUrl());
        return map;
    }

    @Override
    public ServerResponse<Object> selHistoryPronunciation(Integer unitId, HttpSession session) {
        Student student = getStudent(session);
        Map<String, Object> maps = new HashMap<>();
        maps.put("unitId", unitId);
        maps.put("studentId", student.getId());
        List<Map<String, Object>> map = teksMapper.selHistoryPronunciation(maps);
        List<Map<String, Object>> resultMap = new ArrayList<>();
        for (Map<String, Object> getMap : map) {
            getMap.put("url", prefix + getMap.get("url"));
            String sentence = getMap.get("sentence").toString();
            getMap.put("sentence",sentence.replace("$","").replace("#"," "));
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
    public int[] wirterBlank(String[] strList) {
        Random random = new Random();
        List<Integer> shuZhuString = new ArrayList<>();
        for (int i = 0; i < strList.length; i++) {
            boolean falg = true;
            for (int j = 0; j < NAMELIST.size(); j++) {
                if (strList[i].endsWith(",") || strList[i].endsWith("?") || strList[i].endsWith(".") || strList[i].endsWith("!")) {
                    if (strList[i].endsWith("...")) {
                        if(strList[i].length()==3){
                            falg = false;
                        }else{
                            String str = strList[i].replace("...", "");
                            if (str.equals(NAMELIST.get(j))) {
                                falg = false;
                            }
                        }
                    } else {
                        String str = strList[i].substring(0, strList[i].length() - 1);
                        if (str.equals(NAMELIST.get(j))) {
                            falg = false;
                        }
                    }
                } else {
                    if (strList[i].equals(NAMELIST.get(j))) {
                        falg = false;
                    }
                }
                if (strList[i].indexOf("#") != -1 || strList[i].indexOf("$") != -1) {
                    falg = false;
                }
            }
            if (falg) {
                shuZhuString.add(i);
            }
        }


        int choose = 0;
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
                if (choose == 2 && s == 1) {
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
        /*if(choose==3){
            while(integers[choose-1]==0){
                int i=((int)(Math.random()*number))+1;
                if(s==0){
                    integers[0]=i;
                }
                if(s==1){
                    if(integers[0]!=i){
                        integers[1]=i;
                        s++;
                    }
                }
                if(s==2){
                    if(integers[0]!=i&&integers[1]!=i){
                        integers[2]=i;
                        s++;
                    }
                }

            }
        }*/
        return integers;
    }


    //添加数据
    public void addList(String str, List<String> blanceSentence, List<String> vocabulary) {
        if (str.endsWith(",") || str.endsWith(".") || str.endsWith("?") || str.endsWith("!")) {
            vocabulary.add(str.substring(0, str.length() - 1));
            vocabulary.add(str.substring(str.length() - 1));
            blanceSentence.add(null);
            blanceSentence.add(str.substring(str.length() - 1));
        } else {
            vocabulary.add(str);
            blanceSentence.add(null);
        }
    }

    /**
     * 获取课文测试
     */
    @Override
    public ServerResponse<Object> getTeksTest(HttpSession session, Integer unitId) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        //获取单元中的课文语句
        List<Teks> teks = teksMapper.selTeksByUnitId(unitId);
        //第一步去除课文中只有一句话的句子
        List<Teks> useTeks = new ArrayList<>();
        for (Teks teks1 : teks) {
            String sentence = teks1.getSentence();
            String[] s = sentence.split(" ");
            if (s.length > 1) {
                useTeks.add(teks1);
            }
        }
        //附加项选择
        List<Teks> addTeks = null;
        //根据清楚后的语句小于4句时添加
        if (useTeks.size() < 4) {
            addTeks = teksMapper.getTwentyTeks();
        }
        return getReturnTestTeks(useTeks, addTeks);
    }

    @Override
    public ServerResponse<Map<String, Object>> saveTeksAudition(HttpSession session, Integer unitId, Integer courseId) {
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
            Integer insert = learnMapper.insert(learn);
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
                map.put("update_time", CalculateTimeUtil.CalculateTime(time));
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


    private ServerResponse<Object> getReturnTestTeks(List<Teks> teks, List<Teks> addTeks) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> choseFour = new ArrayList<>();
        List<Map<String, Object>> hearingList = new ArrayList<>();
        List<Teks> optionList = new ArrayList<>(teks);
        //去除附加选项中与原课文语句相同的语句
        if (addTeks != null && addTeks.size() > 0) {
            for (Teks teks1 : addTeks) {
                boolean isAdd = true;

                for (Teks teks2 : optionList) {
                    if (teks2.getSentence().equals(teks1.getSentence())) {
                        isAdd = false;
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
                    List<Teks> fourOption = teks.subList(0, fourLength * 4);
                    fourChooseFour(fourOption, choseFour);
                    //获取听力习题
                    List<Teks> hearOption = teks.subList(fourLength * 4, teks.size());
                    hearing(hearOption, hearingList, optionList);
                } else {
                    //获取四选四习题
                    List<Teks> fourOption = teks.subList(0, (fourLength + 1) * 4);
                    fourChooseFour(fourOption, choseFour);
                    //获取听力习题
                    List<Teks> hearOption = teks.subList((fourLength + 1) * 4, teks.size());
                    hearing(hearOption, hearingList, optionList);
                }
                //排列习题的顺序
                arrayList(choseFour, hearingList, returnList);
            }
        }
        return ServerResponse.createBySuccess(returnList);
    }

    //排列习题循序
    private void arrayList(List<Map<String, Object>> choseFour, List<Map<String, Object>> hearingList, List<Map<String, Object>> returnList) {
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
    private void fourChooseFour(List<Teks> optionList, List<Map<String, Object>> choseFour) {
        for (int i = 0; i < optionList.size(); i += 4) {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("type", "fourChoseFour");
            List<Teks> teks = optionList.subList(i, i + 4);
            List<Object> objects = new ArrayList<>();
            List<Map<String, Object>> answers = new ArrayList<>();
            List<String> returnAnswers = new ArrayList<>();
            for (int s = 0; s < teks.size(); s++) {
                answersFour(teks.get(s), objects, answers, s, returnAnswers);
            }
            Collections.shuffle(answers);
            returnMap.put("option", objects);
            returnMap.put("subject", answers);
            returnMap.put("answer", returnAnswers);
            choseFour.add(returnMap);
        }
    }

    //四选四题目挖空并储存选项和答案
    private void answersFour(Teks teks, List<Object> senOption, List<Map<String, Object>> answers, Integer index, List<String> returnAnswers) {
        String sentence = teks.getSentence();
        String[] s = sentence.split(" ");
        List<String> arrList = new ArrayList<>();
        Integer location = changeInteger(s);
        for (int i = 0; i < s.length; i++) {
            Map<String, Object> returnMap = new HashMap<>();
            if (i == location) {
                String option = s[i];
                arrList.add(null);
                if (option.endsWith(",") || option.endsWith(".") || option.endsWith("!") || option.endsWith("?")) {
                    if (option.endsWith("...")) {
                        returnMap.put("name", 5);
                        returnMap.put("value", option.replace("...", ""));
                        arrList.add("...");
                        returnAnswers.add(option.replace("...", ""));
                    } else {
                        returnMap.put("name", 5);
                        returnMap.put("value", option.substring(0, option.length() - 1));
                        arrList.add(option.substring(option.length() - 1));
                        returnAnswers.add(option.substring(0, option.length() - 1));
                    }

                } else {
                    returnMap.put("name", 5);
                    returnMap.put("value", option);
                    returnAnswers.add(option);
                }
                answers.add(returnMap);
            } else {
                String option = s[i];
                if (option.endsWith(",") || option.endsWith(".") || option.endsWith("!") || option.endsWith("?")) {
                    if (option.endsWith("...")) {
                        arrList.add(option.replace("...", "").replace("#", " ").replace("$", ""));
                        arrList.add("...");
                    } else {
                        arrList.add(option.substring(0, option.length() - 1).replace("#", " ").replace("$", ""));
                        arrList.add(option.substring(option.length() - 1));
                    }
                } else {
                    arrList.add(option.replace("#", " ").replace("$", ""));
                }
            }
        }
        senOption.add(arrList);
    }

    //获取挖空的位置
    private int changeInteger(String[] str) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if (str[i].indexOf("#") == -1 && str[i].indexOf("$") == -1) {
                list.add(i);
            }
        }
        int integer = (int) Math.ceil(Math.random() * (list.size() - 1));
        return list.get(integer);
    }

    //获取听力题
    private void hearing(List<Teks> hearingList, List<Map<String, Object>> returnList, List<Teks> optionList) {
        for (int i = 0; i < hearingList.size(); i++) {
            Collections.shuffle(optionList);
            List<Teks> list = new ArrayList<>();
            list.addAll(optionList);
            list.remove(hearingList.get(i));
            /*Integer ss =1; //(int) Math.ceil((Math.random() * 2));*/
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("id", hearingList.get(i).getId());
            returnMap.put("chinese", hearingList.get(i).getParaphrase());
            returnMap.put("pronunciation", baiduSpeak.getSentencePath(hearingList.get(i).getSentence().replace("#", " ").replace("$", "")));
            returnMap.put("english", hearingList.get(i).getSentence().replace("#", " ").replace("$", ""));
            //英选汉 原有两个选项现改为一个
              /*  if (ss == 1) {
                    returnMap.put("type", "BritishElectHan");
                    BritishElectHan(teks.get(i), new ArrayList<Teks>(list.subList(0, 3)), returnMap);
                    //汉选英
                } else {*/
            returnMap.put("type", "HanElectBritish");
            HanElectBritish(hearingList.get(i), new ArrayList<Teks>(list.subList(0, 3)), returnMap);
            /*}*/
            returnList.add(returnMap);
        }
    }

    private void HanElectBritish(Teks teks, List<Teks> optionList, Map<String, Object> returnMap) {
        List<String> option = new ArrayList<>();
        option.add(teks.getSentence().replace("#", " ").replace("$", ""));
        for (int i = 0; i < 3; i++) {
            option.add(optionList.get(i).getSentence().replace("#", " ").replace("$", ""));
        }
        Collections.shuffle(option);
        for (int i = 0; i < option.size(); i++) {
            if (option.get(i).equals(teks.getSentence().replace("#", " ").replace("$", ""))) {
                returnMap.put("answer", i);
            }
        }
        returnMap.put("option", option);

    }

    private void BritishElectHan(Teks teks, List<Teks> optionList, Map<String, Object> returnMap) {
        optionList.add(teks);
        Collections.shuffle(optionList);
        List<String> english = new ArrayList<>();
        List<String> option = new ArrayList<>();
        for (int i = 0; i < optionList.size(); i++) {
            if (optionList.get(i).getSentence().equals(teks.getSentence())) {
                returnMap.put("answer", i);
            }
            english.add(optionList.get(i).getSentence());
            option.add(baiduSpeak.getSentencePath(optionList.get(i).getSentence()));
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
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
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
            if(words[i].contains("$")){
                words[i] = words[i].replace("$", " ");
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

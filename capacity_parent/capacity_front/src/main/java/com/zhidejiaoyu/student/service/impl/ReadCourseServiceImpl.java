package com.zhidejiaoyu.student.service.impl;

import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.constant.read.ReadContentConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhidejiaoyu.student.service.impl.ReadWordServiceImpl.getAllWords;

@Service
public class ReadCourseServiceImpl extends BaseServiceImpl<ReadCourseMapper, ReadCourse> implements ReadCourseService {

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private ReadCourseMapper readCourseMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private ReadTypeMapper readTypeMapper;

    @Autowired
    private ReadContentMapper readContentMapper;

    @Autowired
    private ReadChooseMapper readChooseMapper;

    @Autowired
    private ReadJudgeMapper readJudgeMapper;

    @Autowired
    private ReadQuestionAnsweringMapper readQuestionAnsweringMapper;

    @Autowired
    private ReadChooseBlanksMapper readChooseBlanksMapper;

    @Autowired
    private ReadBlanksMapper readBlanksMapper;

    @Autowired
    private ReadArderMapper readArderMapper;

    @Autowired
    private ReadWordMapper readWordMapper;

    @Autowired
    private ReadPictureMapper readPictureMapper;

    @Autowired
    private ReadWiseCounselMapper readWiseCounselMapper;

    /**
     * 获取全部单元信息
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getAllCourse(HttpSession session, String gradeString) {
        //修改前数据
       /* Long studentId = getStudentId(session);
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId);
        if (studentStudyPlans != null && studentStudyPlans.size() > 0) {
            //去掉重复添加的阅读数据
            List<ReadCourse> readCourses = this.getReadCourseList(studentStudyPlans);
            //获取当前学习单元
            CapacityStudentUnit unit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("list", readCourses);
            Map<String, Object> present = new HashMap<>();
            //判断正在学习单元拥有
            if (unit != null) {
                for (ReadCourse course : readCourses) {
                    if (unit.getUnitId().equals(course.getId())) {
                        present.put("courseId", unit.getCourseId());
                        present.put("courseName", unit.getCourseName());
                    }
                }
            }
            //在未拥有当前学习的课程时调用
            if (present.size() <= 0) {
                present = new HashMap<>();
                ReadCourse readCourse = readCourses.get(0);
                present.put("courseId", readCourse.getId());
                present.put("courseName", readCourse.getCourseName());
                //更改正在学习的信息
                updStudyPlan(readCourse, unit, studentId);
            }
            returnMap.put("present", present);
            return ServerResponse.createBySuccess(returnMap);
        }*/
        //修改后数据
        Long studentId = getStudentId(session);
        Integer gradeInteger = getGradeInteger(gradeString);
        //获取学习计划
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId, gradeInteger);
        if (studentStudyPlans != null && studentStudyPlans.size() > 0) {
            CapacityStudentUnit unit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
            //存放全部版本信息
            List<Map<String, Object>> courseList = new ArrayList<>();
            //获取去重后的数据  及存放正在学习的版本
            List<Map<String, Object>> list = this.getReadCourseList(studentStudyPlans, unit);
            //存放返回信息
            Map<String, Object> returnMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                //获取年级
                String grade = map.get("grade").toString();
                //获取所有版本list集合
                List<Long> unitList = (List<Long>) map.get("unitMap");
                //获取月份信息
                List<Map<String, Object>> maps = readCourseMapper.selSort(grade, unitList);
                //判断正在学习的课程是否拥有，如果未拥有放入当前版本信息
                Map<String, Object> versionMap = new HashMap<>();
                versionMap.put("grade", grade);
                versionMap.put("isStudy", unit.getUnitId());
                List<Map<String, Object>> sList = new ArrayList<>();
                for (Map<String, Object> sMap : maps) {
                    long courseId = Long.parseLong(sMap.get("courseId").toString());
                    //获取课程下阅读数量
                    Integer typeCount = readTypeMapper.selCountByCourseId(courseId);
                    //获取课程下所有考试结果
                    Integer recprdCourseId = testRecordMapper.selectReadCountByCourseId(courseId);
                    if (typeCount <= recprdCourseId) {
                        sMap.put("text", "已完成");
                    } else {
                        sMap.put("text", "未完成");
                    }
                    sList.add(sMap);
                }
                versionMap.put("unitList", sList);
                courseList.add(versionMap);
            }
            returnMap.put("course", courseList);
            return ServerResponse.createBySuccess(returnMap);
        }
        return ServerResponse.createByError(500, "未分配课程");
    }

    /**
     * 修改正在学习的单元信息
     *
     * @param session
     * @param grade
     * @return
     */
    @Override
    public ServerResponse<Object> updStudyPlan(HttpSession session, Long unitId, String grade) {
        Long studentId = getStudentId(session);
        Integer gradeInteger = getGradeInteger(grade);
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selByCourseIdAndUnitIdAndType(gradeInteger, unitId, 6, studentId);
        CapacityStudentUnit unit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
        updStudyPlan(studentStudyPlan, unit, studentId, unitId);
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取当前单元阅读课程信息
     *
     * @param session
     * @param unitId
     * @param grade
     * @return
     */
    @Override
    public ServerResponse<Object> getStudyCourse(HttpSession session, Long unitId, String grade) {
        Student student = getStudent(session);
        //获取年级月份下的全部的课程
        if (unitId == null || grade == null) {
            return ServerResponse.createByError();
        }
        List<Long> courseIds = readCourseMapper.selBySortAndGrade(unitId, grade);
        //获取月份下的所有课程
        List<ReadType> readTypes = readTypeMapper.selByCourseList(courseIds);
        //List<ReadType> readTypes = readTypeMapper.selByCourseId(courseId);
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (ReadType readType : readTypes) {
            Map<String, Object> returnMap = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            map.put("typeId", readType.getId());
            map.put("typesOfEssays", readType.getTypesOfEssays());
            map.put("difficulty", readType.getDifficulty());
            map.put("wordQuantity", readType.getWordQuantity());
            String learnTime = readType.getLearnTime().replace("s", "");
            Integer second = Integer.parseInt(learnTime);
            Integer minute = second / 60;
            Integer residueSecond = second % 60;
            StringBuilder strB = new StringBuilder();
            if (minute != null && minute != 0) {
                strB.append(minute + "分");
            }
            if (residueSecond != null && residueSecond != 0) {
                strB.append(residueSecond + "秒");
            }
            map.put("readName", readType.getReadName());
            map.put("lookLearnTime", strB.toString());
            map.put("calculationLearnTime", second);
            map.put("questions", readType.getReadCount());
            TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitIdAndGenreAndStudyModel(student.getId(), readType.getId(), "阅读测试", "阅读测试");
            if (testRecord != null) {
                map.put("rightCount", testRecord.getRightCount());
                map.put("isClose", true);
            } else {
                map.put("rightCount", 0);
                map.put("isClose", false);
            }
            returnMap.put("title", readType.getReadName());
            returnMap.put("data", map);
            returnList.add(returnMap);
        }
        return ServerResponse.createBySuccess(returnList);
    }

    /**
     * 获取阅读课文文章
     *
     * @param typeId
     * @param courseId
     * @return
     */
    @Override
    public ServerResponse<Object> getContent(Long typeId, Long courseId) {
        Map<String, Object> map = new HashMap<>();

        if (typeId != null) {
            //查看趣味阅读
            this.getInterestingReadingData(typeId, map);
        } else {
            //查看队长讲英语课程
            this.getSpeakEnglishData(courseId, map);
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> getVersion(HttpSession session) {
        Long studentId = getStudentId(session);
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId, null);
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
        List<Map<String, Object>> returnList = new ArrayList<>();
        if (studentStudyPlans.size() == 0) {
            return ServerResponse.createByError();
        }
        boolean isTrue = true;
        int index = 0;
        for (StudentStudyPlan plan : studentStudyPlans) {
            index++;
            Map<String, Object> map = new HashMap<>();
            map.put("version", getStringGrade(plan.getCourseId()));
            if (capacityStudentUnit != null && capacityStudentUnit.getCourseId().equals(plan.getCourseId())) {
                map.put("isStudy", true);
                isTrue = false;
            } else {
                map.put("isStudy", false);
            }
            if (isTrue && studentStudyPlans.size() == index) {
                map.put("isStudy", true);
            }
            returnList.add(map);
        }

        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    public ServerResponse capacityMatching(HttpSession session, Long courseId, Long readTypeId) {
        List<ReadContent> readContents = readContentMapper.selectByReadTypeId(readTypeId);
        Map<String, Object> returnMap = new HashMap<>(16);
        if (readContents.size() == 0) {
            returnMap.put("rate", "100%");
            return ServerResponse.createBySuccess(returnMap);
        }

        StringBuilder sb = new StringBuilder();

        // 将文章中需要挖空的数据替换为空字符串
        readContents.forEach(readContent -> sb.append(readContent.getSentence().replace(ReadContentConstant.BLANK + ".", "").replace(ReadContentConstant.BLANK, "")).append(" "));
        // 整篇文章所有句子
        String text = sb.toString().trim();
        List<String> allWords = getAllWords(text);

        Student student = super.getStudent(session);
        // 当前文章的生词
        List<String> needMarkRedWords = readWordMapper.selectNeedMarkRedWords(student.getId(), courseId, allWords, readTypeId);
        // 当前文章所有单词数
        String[] words = text.split(" ");

        int totalWords = words.length;
        int newWords = needMarkRedWords.size();

        if (totalWords < newWords) {
            returnMap.put("rate", "0%");
            return ServerResponse.createBySuccess(returnMap);
        }

        double div = BigDecimalUtil.div(newWords, totalWords, 2);
        long rate = Math.round((1 - div) * 100);
        returnMap.put("rate", rate + "%");
        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<Object> getEnglishParadise(Long courseId, Integer type) {
        Map<String, Object> map = new HashMap<>();
        List<ReadArder> readArders = readArderMapper.selByCourseId(courseId, type + 1);
        getEnglishData(readArders, map);
        if (type == 2 || type == 3) {
            List<ReadPicture> readPictures = readPictureMapper.selByCourseIdAndType(courseId, type);
            List<String> partList = new ArrayList<>();
            for (ReadPicture readPicture : readPictures) {
                partList.add(AliyunInfoConst.host + readPicture.getPartUrl());
            }
            map.put("partList", partList);
        }
        ReadCourse readCourse = readCourseMapper.selectById(courseId);
        map.put("courseName", readCourse.getGrade() + "-" + readCourse.getMonth());
        return ServerResponse.createBySuccess(map);
    }

    private void getWordList(ReadContent readContent, List<Map<String, Object>> readList) {
        Map<String, Object> wordMap = new HashMap<>();
        String[] replace = readContent.getSentence().replace("#&#", "").split(" ");
        List<String> wordList = Arrays.asList(replace);
        wordMap.put("wordList", wordList);
        wordMap.put("translate", readContent.getTranslate());
        readList.add(wordMap);
    }

    /**
     * 获取趣味阅读返回格式
     *
     * @param typeId
     * @param map
     */
    private void getInterestingReadingData(Long typeId, Map<String, Object> map) {
        ReadType readType = readTypeMapper.selectById(typeId);
        List<ReadContent> readContents = readContentMapper.selectByTypeId(typeId);
        List<List<Map<String, Object>>> returnList = new ArrayList<>();
        List<Map<String, Object>> readList = new ArrayList<>();
        int i = 0;
        for (ReadContent readContent : readContents) {

            if (readList.size() == 0) {
                readList = new ArrayList<>();
                getWordList(readContent, readList);
                i++;
            } else {
                if (readContent.getSentence().indexOf("#&#") != -1) {
                    returnList.add(readList);
                    readList = new ArrayList<>();
                }
                getWordList(readContent, readList);
                i++;
            }
            if (i == readContents.size()) {
                returnList.add(readList);
            }
        }
        if (readType.getTestType() == 5) {
            this.getBlanks(typeId, map, returnList);
        }
        if (readType.getTestType() == 3) {
            this.getChooseSentences(typeId, map, readType.getReadCount());
        }
        /* if (readType.getTestType() != 5) {*/
        map.put("sentenceList", returnList);
        /* }*/

        map.put("type", readType.getTestType());
        Integer learnTime = Integer.parseInt(readType.getLearnTime().replace("s", ""));
        map.put("learnTime", learnTime);
        ReadCourse readCourse = readCourseMapper.selectById(readType.getCourseId());
        map.put("courseName", readCourse.getGrade() + "-" + readCourse.getMonth());
        map.put("courseId", readCourse.getId());

        Integer minute = learnTime / 60;
        Integer residueSecond = learnTime % 60;
        StringBuilder strB = new StringBuilder();
        if (minute != null && minute != 0) {
            strB.append(minute + "分");
        }
        if (residueSecond != null && residueSecond != 0) {
            strB.append(residueSecond + "秒");
        }
        map.put("lookTime", strB.toString());
        if (readType.getTestType() == 1) {
            this.getChoiceQuestions(typeId, null, map);
        }
        if (readType.getTestType() == 2) {
            this.getJudgmentQuestions(typeId, null, map);
        }
        if (readType.getTestType() == 4) {
            this.getAnswersToQuestions(typeId, map);
        }

    }

    private void getSpeakEnglishData(Long courseId, Map<String, Object> map) {

        //获取队长讲英语课文
        List<ReadArder> readArders = readArderMapper.selByCourseId(courseId, 1);
        //获取队长讲英语锦囊妙计
        ReadWiseCounsel readWiseCounsel = readWiseCounselMapper.getByCourseId(courseId);
        map.put("wiseCounsel", readWiseCounsel.getContent());
        ReadCourse readCourse = readCourseMapper.selectById(courseId);
        map.put("courseName", readCourse.getGrade() + "-" + readCourse.getMonth());
        map.put("title", readArders.get(0).getTitle());
        getEnglishData(readArders, map);
        //获取考试类型
        Integer type = readArders.get(0).getType();
        map.put("type", type);
        if (type == 1) {
            this.getChoiceQuestions(null, courseId, map);
        }
        if (type == 2) {
            this.getJudgmentQuestions(null, courseId, map);
        }

    }

    private void getReturnEnglisthMap(List<ReadArder> readArders, Map<String, Object> map) {
        StringBuilder englishBuilder = new StringBuilder();
        StringBuilder chineseBuilder = new StringBuilder();
        List<Map<String, Object>> list = new ArrayList<>();
        int i = 0;
        for (ReadArder readArder : readArders) {
            if (i == 0) {
                englishBuilder.append(readArder.getSentence().replace("#&#", ""));
                chineseBuilder.append(readArder.getTranslate().replace("#&#", ""));
                i++;
            } else {
                if (readArder.getSentence().indexOf("#&#") != -1) {
                    Map<String, Object> sMap = new HashMap<>();
                    sMap.put("sentence", englishBuilder.toString());
                    sMap.put("translate", chineseBuilder.toString());
                    list.add(sMap);
                    englishBuilder = new StringBuilder();
                    chineseBuilder = new StringBuilder();
                }
                englishBuilder.append(readArder.getSentence().replace("#&#", ""));
                chineseBuilder.append(readArder.getTranslate().replace("#&#", ""));
                i++;
            }
            if (i == readArders.size()) {
                Map<String, Object> sMap = new HashMap<>();
                sMap.put("sentence", englishBuilder.toString());
                sMap.put("translate", chineseBuilder.toString());
                list.add(sMap);
            }
        }
        map.put("sentenceList", list);
    }

    private void getEnglishData(List<ReadArder> readArders, Map<String, Object> map) {
        List<List<ReadArder>> returnList = new ArrayList<>();
        List<ReadArder> readList = new ArrayList<>();
        int i = 0;
        for (ReadArder readArder : readArders) {
            if (readList.size() == 0) {
                readList = new ArrayList<>();
                String replace = readArder.getSentence().replace("#&#", "");
                readArder.setSentence(replace);
                readList.add(readArder);
                i++;
            } else {
                if (readArder.getSentence().indexOf("#&#") != -1) {
                    returnList.add(readList);
                    readList = new ArrayList<>();
                }
                String replace = readArder.getSentence().replace("#&#", "");
                readArder.setSentence(replace);
                readList.add(readArder);
                i++;
            }
            if (i == readArders.size()) {
                returnList.add(readList);
            }
        }
        map.put("sentenceList", returnList);
    }

    private void getBlanks(Long typeId, Map<String, Object> map, List<List<Map<String, Object>>> sList) {
        ReadBlanks readBlanks = readBlanksMapper.selByTypeId(typeId);
        String[] answerList = readBlanks.getAnswer().split("&@&");
        String[] analysisList = readBlanks.getAnalysis().split("&@&");
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (int i = 0; i < analysisList.length; i++) {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("word", answerList[i]);
            returnMap.put("analysis", analysisList[i]);
            returnList.add(returnMap);
        }
       /* int brank = 0;
        List<List<Map<String, Object>>> reList = new ArrayList<>();
        for (List<Map<String, Object>> rList : sList) {
            List<Map<String, Object>> tList = new ArrayList<>();
            for (Map<String, Object> rMap : rList) {
                List<String> wordList = (List<String>) rMap.get("wordList");
                List<Map<String,Object>> wordsList=new ArrayList<>();
                for (String word : wordList) {
                    Map<String,Object> wordMap=new HashMap<>();
                    wordMap.put("word",word);
                    if (word.contains("&@&")) {
                        wordMap.put("answer", answerList[brank]);
                        brank++;
                    }
                    wordsList.add(wordMap);
                }
                rMap.put("wordList",wordsList);
                tList.add(rMap);
            }
            reList.add(tList);
        }
        map.put("sentenceList", reList);*/
        map.put("topic", returnList);
    }

    /**
     * 选句填空
     *
     * @param typeId
     * @param map
     */
    private void getChooseSentences(Long typeId, Map<String, Object> map, Integer readCount) {
        ReadChooseBlanks readChooseBlanks = readChooseBlanksMapper.selByTypeId(typeId);
        String analysis = readChooseBlanks.getAnalysis();
        String content = readChooseBlanks.getContent();
        List<String> contentList = Arrays.asList(content.split("&@&"));
        List<String> analysisList = Arrays.asList(analysis.split("&@&"));
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("number", i);
            if (i < readCount) {
                returnMap.put("isTrue",true);
            }else{
                returnMap.put("isTrue",false);

            }
            returnMap.put("analysisList", analysisList.get(i));
            returnMap.put("sentence", contentList.get(i));
            list.add(returnMap);
        }

        map.put("topic", list);
    }


    /**
     * 获取回答问题题目
     *
     * @param typeId
     * @param map
     */
    private void getAnswersToQuestions(Long typeId, Map<String, Object> map) {
        List<ReadQuestionAnswering> readQuestionAnswerings = readQuestionAnsweringMapper.selectByTypeIdOrCourseId(typeId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReadQuestionAnswering question : readQuestionAnswerings) {
            Map<String, Object> qyestioneMap = new HashMap<>();
            qyestioneMap.put("subject", question.getSubject());
            qyestioneMap.put("analysis", question.getAnalysis());
            String[] split = question.getAnswer().split("&@&");
            List<String> answerList = Arrays.asList(split);
            qyestioneMap.put("answer", answerList);
            list.add(qyestioneMap);
        }
        map.put("topic", list);
    }

    /**
     * 获取判断题
     *
     * @param typeId
     * @param courseId
     * @param map
     */
    private void getJudgmentQuestions(Long typeId, Long courseId, Map<String, Object> map) {
        List<ReadJudge> readJudges = readJudgeMapper.selectByTypeIdOrCourseId(typeId, courseId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReadJudge judge : readJudges) {
            Map<String, Object> judgeMap = new HashMap<>();
            judgeMap.put("subject", judge.getSubject());
            judgeMap.put("analysis", judge.getAnalysis());
            boolean answer = judge.getAnswer().trim().equals("T") ? true : false;
            judgeMap.put("answer", answer);
            list.add(judgeMap);
        }
        map.put("topic", list);
    }

    /**
     * 获取选择题
     *
     * @param typeId
     * @param courseId
     * @param map
     */
    private void getChoiceQuestions(Long typeId, Long courseId, Map<String, Object> map) {
        List<ReadChoose> readChooses = readChooseMapper.selectByTypeIdOrCourseId(typeId, courseId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReadChoose choose : readChooses) {
            Map<String, Object> chooseMap = new HashMap<>();
            //放入题目
            chooseMap.put("subject", choose.getSubject());
            //放入选择答案
            List<Map<String, Object>> reList = new ArrayList<>();
            Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer", choose.getAnswer());
            answerMap.put("falg", true);
            reList.add(answerMap);
            //获取错误答案
            String[] wronganswers = choose.getWrongAnswer().split("&@&");
            List<String> wrongList = Arrays.asList(wronganswers);
            //放入错误答案
            for (String str : wrongList.subList(0, 3)) {
                Map<String, Object> worngMap = new HashMap<>();
                worngMap.put("answer", str);
                worngMap.put("falg", false);
                reList.add(worngMap);
            }
            chooseMap.put("analysis", choose.getAnalysis());
            chooseMap.put("answer", reList);
            list.add(chooseMap);
        }
        map.put("topic", list);
    }


    /**
     * 更改正在学习的课程
     *
     * @param studentStudyPlan 正要课程信息
     * @param unit             正在学习课程信息
     * @param studentId        学生id
     */
    private void updStudyPlan(StudentStudyPlan studentStudyPlan, CapacityStudentUnit unit, Long studentId, Long unitId) {
        if (unit == null) {
            unit = new CapacityStudentUnit();
            unit.setStudentId(studentId);
        }
        unit.setType(6);
        unit.setUnitId(unitId);
        unit.setCourseId(studentStudyPlan.getCourseId());
        unit.setStartunit(studentStudyPlan.getStartUnitId());
        unit.setEndunit(studentStudyPlan.getEndUnitId());
        if (unit.getId() != null) {
            capacityStudentUnitMapper.updateById(unit);
        } else {
            capacityStudentUnitMapper.insert(unit);
        }
    }

    /**
     * 修改后版本
     *
     * @param plans
     * @return
     */
    private List<Map<String, Object>> getReadCourseList(List<StudentStudyPlan> plans, CapacityStudentUnit unit) {
        //获取全部课程id 去重
        List<Map<String, Object>> list = new ArrayList<>();
        Map<Long, List<StudentStudyPlan>> collect = plans.stream().collect(Collectors.groupingBy(vo -> vo.getCourseId()));
        Set<Long> courses = collect.keySet();
        for (Long course : courses) {
            List<StudentStudyPlan> studentStudyPlans = collect.get(course);
            String grade = getStringGrade(course);
            Map<String, Object> gradeMap = new HashMap<>();
            Map<Long, Long> unitMap = new HashMap<>();
            for (StudentStudyPlan studentStudyPlan : studentStudyPlans) {
                List<Long> longs = readCourseMapper.selReadSortByStartReadSortAndEndReadSort(studentStudyPlan.getStartUnitId(), studentStudyPlan.getEndUnitId(), grade);
                for (Long readSort : longs) {
                    unitMap.put(readSort, readSort);
                }
            }
            List<Long> unitList = new ArrayList<>();
            Set<Long> longs = unitMap.keySet();
            for (Long sortId : longs) {
                unitList.add(sortId);
            }
            gradeMap.put("grade", grade);
            gradeMap.put("unitMap", unitList);
            list.add(gradeMap);
        }
        return list;
    }

    //去重数据 修改前版本
    private List<ReadCourse> getReadCourse(List<StudentStudyPlan> plans) {
        //获取全部课程id 去重
        Map<Long, ReadCourse> map = new HashMap<>();
        for (StudentStudyPlan plan : plans) {
            List<ReadCourse> readCourses = readCourseMapper.selCourseByStartUnitAndEndUnit(plan.getStartUnitId(), plan.getEndUnitId());
            for (ReadCourse course : readCourses) {
                map.put(course.getId(), course);
            }
        }
        Set<Long> longs = map.keySet();
        List<ReadCourse> list = new ArrayList<>();
        for (Long courseId : longs) {
            list.add(map.get(courseId));
        }
        return list;
    }

    private String getStringGrade(Long gradeLong) {
        Integer grade = gradeLong.intValue();
        if (grade.equals(1)) {
            return "一年级";
        }
        if (grade.equals(2)) {
            return "二年级";
        }
        if (grade.equals(3)) {
            return "三年级";
        }
        if (grade.equals(4)) {
            return "四年级";
        }
        if (grade.equals(5)) {
            return "五年级";
        }
        if (grade.equals(6)) {
            return "六年级";
        }
        if (grade.equals(7)) {
            return "七年级";
        }
        if (grade.equals(8)) {
            return "八年级";
        }
        if (grade.equals(9)) {
            return "九年级";
        }
        if (grade.equals(10)) {
            return "高一";
        }
        if (grade.equals(11)) {
            return "高二";
        }
        if (grade.equals(12)) {
            return "高三";
        }
        return "三年级";
    }

    private Integer getMonthSort(String month) {
        if (month.equals("一月份")) {
            return 1;
        }
        if (month.equals("二月份")) {
            return 2;
        }
        if (month.equals("三月份")) {
            return 3;
        }
        if (month.equals("四月份")) {
            return 4;
        }
        if (month.equals("五月份")) {
            return 5;
        }
        if (month.equals("六月份")) {
            return 6;
        }
        if (month.equals("七月份")) {
            return 7;
        }
        if (month.equals("八月份")) {
            return 8;
        }
        if (month.equals("九月份")) {
            return 9;
        }
        if (month.equals("十月份")) {
            return 10;
        }
        if (month.equals("十一月份")) {
            return 11;
        }
        if (month.equals("十二月份")) {
            return 12;
        }
        return 0;
    }

    private String getMonthSort(Integer month) {
        if (month.equals(1)) {
            return "一月份";
        }
        if (month.equals(2)) {
            return "二月份";
        }
        if (month.equals(3)) {
            return "三月份";
        }
        if (month.equals(4)) {
            return "四月份";
        }
        if (month.equals(5)) {
            return "五月份";
        }
        if (month.equals(6)) {
            return "六月份";
        }
        if (month.equals(7)) {
            return "七月份";
        }
        if (month.equals(8)) {
            return "八月份";
        }
        if (month.equals(9)) {
            return "九月份";
        }
        if (month.equals(10)) {
            return "十月份";
        }
        if (month.equals(11)) {
            return "十一月份";
        }
        if (month.equals(12)) {
            return "十二月份";
        }
        return "一月份";
    }

    private Integer getGradeInteger(String grade) {
        if (grade.equals("一年级")) {
            return 1;
        }
        if (grade.equals("二年级")) {
            return 2;
        }
        if (grade.equals("三年级")) {
            return 3;
        }
        if (grade.equals("四年级")) {
            return 4;
        }
        if (grade.equals("五年级")) {
            return 5;
        }
        if (grade.equals("六年级")) {
            return 6;
        }
        if (grade.equals("七年级")) {
            return 7;
        }
        if (grade.equals("八年级")) {
            return 8;
        }
        if (grade.equals("九年级")) {
            return 9;
        }
        if (grade.equals("高一")) {
            return 10;
        }
        if (grade.equals("高二")) {
            return 11;
        }
        if (grade.equals("高三")) {
            return 12;
        }
        return 1;
    }
}

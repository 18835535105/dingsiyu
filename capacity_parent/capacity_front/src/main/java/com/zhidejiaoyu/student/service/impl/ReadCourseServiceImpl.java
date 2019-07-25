package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 获取全部单元信息
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getAllCourse(HttpSession session) {
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
        //获取学习计划
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId);
        if (studentStudyPlans != null && studentStudyPlans.size() > 0) {
            CapacityStudentUnit unit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
            //存放正在学习单元信息
            Map<String, Object> present = new HashMap<>();
            //存放全部版本信息
            List<Map<String, Object>> courseList = new ArrayList<>();
            //存放全部版本
            List<String> versionList = new ArrayList<>();
            //获取去重后的数据  及存放正在学习的版本
            List<Map<String, Object>> list = this.getReadCourseList(studentStudyPlans, present, unit);
            //存放返回信息
            Map<String, Object> returnMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                //获取年级
                String grade = map.get("grade").toString();
                //将年级放入版本信息中
                versionList.add(grade);
                //获取所有版本list集合
                List<Long> unitList = (List<Long>) map.get("unitMap");
                //获取月份信息
                List<Map<String, Object>> maps = readCourseMapper.selSort(grade, unitList);
                //判断正在学习的课程是否拥有，如果未拥有放入当前版本信息
                if (present == null || present.size() == 0) {
                    present = new HashMap<>();
                    present.put("grade", grade);
                    present.put("unitId", maps.get(0).get("unitId"));
                }
                Map<String, Object> versionMap = new HashMap<>();
                versionMap.put("grade", grade);
                versionMap.put("unitList", maps);
                courseList.add(versionMap);
            }
            returnMap.put("course", courseList);
            returnMap.put("version", versionList);
            returnMap.put("present", present);
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
            map.put("lookLearnTime", strB.toString());
            map.put("calculationLearnTime", second);
            map.put("questions", readType.getReadCount());
            TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitIdAndGenre(student.getId(), readType.getId(), "阅读测试", "阅读测试");
            if (testRecord != null) {
                map.put("rightCount", testRecord.getRightCount());
            } else {
                map.put("rightCount", 0);
            }
            returnList.add(map);
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

        if (typeId == null) {
            //查看趣味阅读
            this.getInterestingReadingData(typeId, map);
        } else {
            //查看队长讲英语课程
        }
        return null;
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
        List<List<ReadContent>> returnList = new ArrayList<>();
        List<ReadContent> readList = new ArrayList<>();
        for (ReadContent readContent : readContents) {
            if (readList.size() == 0) {
                readList = new ArrayList<>();
                readContent.setSentence(readContent.getSentence().replace("#&#", ""));
                readList.add(readContent);
            } else {
                if (readContent.getSentence().indexOf("#&#") != -1) {
                    returnList.add(readList);
                    readList = new ArrayList<>();
                }
                readContent.setSentence(readContent.getSentence().replace("#&#", ""));
                readList.add(readContent);
            }
        }
        map.put("sentenceList", returnList);

        if (readType.getTestType() == 1) {
            this.getChoiceQuestions(typeId, null, map);
        }
        if (readType.getTestType() == 2) {
            this.getJudgmentQuestions(typeId, null, map);
        }
        if (readType.getTestType() == 4) {
            this.getAnswersToQuestions(typeId, map);
        }

        if (readType.getTestType() == 3) {

        }

        if (readType.getTestType() == 5) {

        }
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
            chooseMap.put("subject", choose.getSubject());
            Map<String, Object> answerMap = new HashMap<>();
            answerMap.put(choose.getAnswer(), true);
            String[] wronganswers = choose.getWrongAnswer().split("&@&");
            List<String> wrongList = Arrays.asList(wronganswers);
            for (String str : wrongList.subList(0, 3)) {
                answerMap.put(str, false);
            }
            chooseMap.put("analysis", choose.getAnalysis());
            chooseMap.put("answer", answerMap);
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
    private List<Map<String, Object>> getReadCourseList(List<StudentStudyPlan> plans, Map<String, Object> present, CapacityStudentUnit unit) {
        //获取全部课程id 去重
        List<Map<String, Object>> list = new ArrayList<>();
        Map<Long, List<StudentStudyPlan>> collect = plans.stream().collect(Collectors.groupingBy(vo -> vo.getCourseId()));
        String unitGrade = getStringGrade(unit.getCourseId());
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
                if (grade.equals(unitGrade) && unit.getUnitId().equals(sortId)) {
                    present.put("grade", grade);
                    present.put("unitId", sortId);
                }
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

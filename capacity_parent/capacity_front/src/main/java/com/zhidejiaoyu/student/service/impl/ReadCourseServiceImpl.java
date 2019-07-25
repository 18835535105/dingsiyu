package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

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
        Long studentId = getStudentId(session);
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId);
        if (studentStudyPlans != null && studentStudyPlans.size() > 0) {
            //去掉重复添加的阅读数据
            List<ReadCourse> readCourses = this.getReadCourse(studentStudyPlans);
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
        }
        return ServerResponse.createByError(500, "未分配课程");
    }

    /**
     * 修改正在学习的单元信息
     *
     * @param session
     * @param courseId
     * @return
     */
    @Override
    public ServerResponse<Object> updStudyPlan(HttpSession session, Long courseId) {
        Long studentId = getStudentId(session);
        ReadCourse readCourse = readCourseMapper.selectById(courseId);
        CapacityStudentUnit unit = capacityStudentUnitMapper.selByStudentIdAndType(studentId, 6);
        updStudyPlan(readCourse, unit, studentId);
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取当前单元阅读课程信息
     *
     * @param session
     * @param courseId
     * @return
     */
    @Override
    public ServerResponse<Object> getStudyCourse(HttpSession session, Long courseId) {
        Student student = getStudent(session);
        List<ReadType> readTypes = readTypeMapper.selByCourseId(courseId);
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
        return null;
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
        List<ReadContent> readList=new ArrayList<>();
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
            judgeMap.put("answer", judge.getAnswer());
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
     * @param readCourse 正要课程信息
     * @param unit       正在学习课程信息
     * @param studentId  学生id
     */
    private void updStudyPlan(ReadCourse readCourse, CapacityStudentUnit unit, Long studentId) {
        if (unit == null) {
            unit = new CapacityStudentUnit();
            unit.setStudentId(studentId);
        }
        unit.setUnitName(readCourse.getCourseName());
        unit.setCourseName(readCourse.getCourseName());
        unit.setType(6);
        unit.setUnitId(readCourse.getId());
        StudentStudyPlan plan = studentStudyPlanMapper.selStudyReadPlanByStudentIdAndUnitId(studentId, readCourse.getId());
        unit.setStartunit(plan.getStartUnitId());
        unit.setEndunit(plan.getEndUnitId());
        if (unit.getId() != null) {
            capacityStudentUnitMapper.updateById(unit);
        } else {
            capacityStudentUnitMapper.insert(unit);
        }
    }

    //去重数据
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
}

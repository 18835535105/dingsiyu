package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.common.vo.testbeforstudy.QuestionAndAnswer;
import com.zhidejiaoyu.common.mapper.CourseMapper;
import com.zhidejiaoyu.common.mapper.ExerciseSheetBeforeClassMapper;
import com.zhidejiaoyu.common.mapper.UnitMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.ExerciseSheetBeforeClassService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课前测试习题表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-09-09
 */
@Service
public class ExerciseSheetBeforeClassServiceImpl extends BaseServiceImpl<ExerciseSheetBeforeClassMapper, ExerciseSheetBeforeClass> implements ExerciseSheetBeforeClassService {

    @Resource
    private ExerciseSheetBeforeClassMapper exerciseSheetBeforeClassMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UnitMapper unitMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

    @Override
    public Object getAllCourse(String studyParagraph) {
        //获取课程信息
        List<Course> courses = courseMapper.getAllCourseByStudyParagraph(studyParagraph);
        //将课程根据version分组
        Map<String, List<Course>> allCourse = courses.stream().collect(Collectors.groupingBy(Course::getVersion));
        //获取所有version
        Set<String> versions = allCourse.keySet();
        //返回集合
        List<Object> returnList = new ArrayList<>();
        for (String version : versions) {
            //根据version获取课程信息
            List<Course> maps = allCourse.get(version);
            Map<String, Object> map = new HashMap<>();
            map.put("version", version);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Course course : maps) {
                Map<String, Object> versionMap = new HashMap<>();
                versionMap.put("id", course.getId());
                versionMap.put("jointName", course.getGrade() + "-" + course.getLabel());
                list.add(versionMap);
            }
            map.put("list", list);
            returnList.add(map);
        }
        return returnList;
    }

    @Override
    public Object getUnitByCourseId(Long courseId) {
        //根据课程id获取单元
        List<Unit> units = unitMapper.selectUnitsByCourseId(courseId);
        //返回集合
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (Unit unit : units) {
            Map<String, Object> map = new HashMap<>();
            map.put("unitName", unit.getUnitName());
            map.put("id", unit.getId());
            returnList.add(map);
        }
        return returnList;
    }

    @Override
    public Object getSmallBeforeStudyUnitTest(HttpSession session, String[] jointNames) {
        Student student = super.getStudent(session);

        if (jointNames.length == 1) {
            // 本单元测试
            return this.getSmallCurrentBeforeStudyUnitTest(jointNames[0]);
        }
        // 多单元测试


        return null;
    }

    /**
     * 获取小学本单元测评
     *
     * @param jointName
     * @return
     */
    private Object getSmallCurrentBeforeStudyUnitTest(String jointName) {

        List<ExerciseSheetBeforeClass> exerciseSheetBeforeClasses = exerciseSheetBeforeClassMapper.selectByJointName(jointName);
        // 判断是否需要从单词表中查询当前单元的单词信息
        boolean flag = true;
        for (ExerciseSheetBeforeClass exerciseSheetBeforeClass : exerciseSheetBeforeClasses) {
            if (exerciseSheetBeforeClass.getType() == 1 || exerciseSheetBeforeClass.getType() == 2) {
                flag = false;
                break;
            }
        }

        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();
        QuestionAndAnswer questionAndAnswer;
        if (flag) {
            List<Vocabulary> vocabularies = vocabularyMapper.selectByJointName(jointName);
            int size = vocabularies.size();
            if (size >= 5) {
                Collections.shuffle(vocabularies);
                // 根据英单词选释义
                List<Vocabulary> type1 = vocabularies.subList(0, 5);
                for (Vocabulary vocabulary : type1) {
                    questionAndAnswer = new QuestionAndAnswer();
//                    questionAndAnswer.setAnswerMap();
                }

                // 根据释义选单词
                List<Vocabulary> type2 = vocabularies.subList(5, 10);
            }
        }
        return null;
    }
}

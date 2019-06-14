package com.zhidejiaoyu.common.study.simple;

import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * 学生用户使用过程中重复使用的功用方法
 *
 * @author wuchenxi
 * @date 2018/5/21 15:34
 */
@Slf4j
@Component
public class SimpleCommonMethod implements Serializable {

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleStudentCourseMapper simpleStudentCourseMapper;


    /**
     * 学生第一次学习完成学习引导页内容后，将该学生新信息置为不是第一次学习，可直接进入学习页面
     *
     * @param stuId      当前登录学生id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     */
    public void clearFirst(Long stuId, String studyModel) {
        Learn learn = new Learn();
        learn.setStudentId(stuId);
        learn.setStudyModel(studyModel);
        learnMapper.insert(learn);
    }

    /**
     * 初始化学生与单元之间的关系 默认开启所有课程的第一单元供学生选择，其余单元需通过学习进行
     *
     * @param student
     */
    public void initUnit(Student student) {

        // 判断学生是否已经推送过当前学段的课程
        int count = simpleStudentUnitMapper.countUnitCountByStudentId(student, CommonMethod.getPhase(student.getGrade()));
        if (count > 0) {
            // 当前学生已经推送过课程，不再重复推送
            throw new RuntimeException("学生当前学段课程已经推送！不可重复推送！");
        }

        // 推送当前学段下所有课程
        List<Course> courses = getCourses(student);

        if (courses.size() > 0) {
            // 初始化我的课程页面数据
            initMyCourse(student, courses);
            // 查找课程下的所有单元
            List<Long> courseIds = new ArrayList<>();
            for (Course course : courses) {
                courseIds.add(course.getId());
            }

            UnitOneExample unitExample = new UnitOneExample();
            unitExample.createCriteria().andCourseIdIn(courseIds).andDelstatusEqualTo(1);
            unitExample.setOrderByClause("course_id asc,unit_name asc");
            List<Unit> units = unitMapper.selectByExample(unitExample);

            // 用于存放课程id，在下面循环中判断新map中是否包含当前单元所属的课程id
            // 如果包含说明当前循环的单元不属于课程的第一单元，如果不包含说明当前单元属于课程的第一单元
            Map<Long, Long> map = new HashMap<>(16);

            if (units.size() > 0) {
                List<StudentUnit> studentUnits = new ArrayList<>();
                StudentUnit studentUnit;
                for (Unit unit1 : units) {
                    studentUnit = new StudentUnit();
                    studentUnit.setCourseId(unit1.getCourseId());
                    studentUnit.setUnitId(unit1.getId());
                    studentUnit.setStudentId(student.getId());
                    if (!map.containsKey(unit1.getCourseId())) {
                        // 所有课程的第一单元默认开启
                        map.put(unit1.getCourseId(), unit1.getCourseId());
                        studentUnit.setWordStatus(1);
                        studentUnit.setSentenceStatus(1);
                    } else {
                        studentUnit.setWordStatus(0);
                        studentUnit.setSentenceStatus(0);
                    }
                    studentUnits.add(studentUnit);
                }
                simpleStudentUnitMapper.insertList(studentUnits);
                Unit unit = units.get(0);

                // 初始化单词和例句首次学习的课程和单元
                Course course = simpleCourseMapper.selectByPrimaryKey(unit.getCourseId());
                long courseId = course.getId();
                long unitId = unit.getId();
                String courseName = course.getCourseName();
                student.setCourseName(courseName);
                student.setCourseId(courseId);
                student.setUnitId(unitId);
                student.setSentenceCourseId(courseId);
                student.setSentenceCourseName(courseName);
                student.setSentenceUnitId((int) unitId);
                simpleStudentMapper.updateByPrimaryKeySelective(student);
            }
        }
    }

    /**
     * 获取当前学生可学习的所有课程
     * @param student
     * @return
     */
    public List<Course> getCourses(Student student) {
        String version = student.getVersion();
        String grade = student.getGrade();

        CourseExample courseExample = new CourseExample();
        courseExample.createCriteria().andVersionEqualTo(version).andGradeLike(grade.contains("年级") ? "%年级" : "高%")
                .andStatusEqualTo(1);
        courseExample.setOrderByClause("id asc");
        return simpleCourseMapper.selectByExample(courseExample);
    }

    private void initMyCourse(Student student, List<Course> courses) {
        Course course = courses.get(0);
        // 初始化单词模块
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourseId(course.getId());
        studentCourse.setCourseName(course.getCourseName());
        studentCourse.setStudentId(student.getId());
        studentCourse.setUpdateTime(DateUtil.formatYYYYMMDDHHMMSS(new Date()));
        studentCourse.setType(1);
        simpleStudentCourseMapper.insertSelective(studentCourse);

        // 初始化例句模块
        studentCourse.setType(2);
        simpleStudentCourseMapper.insertSelective(studentCourse);
    }

    /**
     * 将例句顺序分割，其中的标点单独占用一个下标
     *
     * @param sentence 需要处理的例句
     * @return
     */
    public List<String> getEnglishList(String sentence) {
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, " " + s);
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split(" ");
        for (String s : arr) {
            if (s.contains("#")) {
                list.add(s.replace("#", " "));
            } else {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项
     * @return
     */
    public List<String> getOrderEnglishList(String sentence, String exampleDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, "");
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
            list.add(words[i]);
        }

        list.add(exampleDisturb);
        Collections.shuffle(list);
        return list;
    }

    /**
     * 获取乱序的中文选项
     *
     * @param sentence         例句中文
     * @param translateDisturb 中文干扰项
     * @return
     */
    public List<String> getOrderChineseList(String sentence, String translateDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, " ");
            }
        }

        // 拆分并去除*
        List<String> list = new ArrayList<>();
        String[] arr = sentence.split("\\*");
        Collections.addAll(list, arr);
        list.add(translateDisturb);
        Collections.shuffle(list);
        return list;
    }

    /**
     * 将例句中文翻译顺序分割，其中的标点单独占用一个下标
     *
     * @param sentence 需要处理的例句
     * @return
     */
    public List<String> getChineseList(String sentence) {
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, "*" + s + "*");
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split("\\*");
        for (String s : arr) {
            if (s.contains("*")) {
                list.add(s.replace("*", ""));
            } else {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 将清学版测试类型转换为汉字
     *
     * @param classify 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @return 测试类型中文
     */
    public String getTestType(Integer classify) {
        String[] typeStr = {"单词辨音", "词组辨音", "快速单词", "快速词组", "词汇考点", "快速句型", "语法辨析", "单词默写", "词组默写"};
        if (classify == null) {
            log.error("classify = null");
            return null;
        }
        if (classify <= typeStr.length) {
            return typeStr[classify - 1];
        }
        return null;
    }

    /**
     * 将智能版学习模块转换为汉字
     *
     * @param model 0：单词图鉴；1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7：五维测试
     * @return
     */
    public String getCapacityStudyModel(Integer model) {
        if (model == -1) {
            return "五维测试";
        }
        String[] typeStr = {"单词图鉴","慧记忆","慧听写","慧默写","例句听力","例句翻译","例句默写","五维测试"};
        int length = typeStr.length;
        if (model != null && model < length) {
            return typeStr[model];
        }
        return null;
    }

    public static void main(String[] args) {
        SimpleCommonMethod simpleCommonMethod = new SimpleCommonMethod();
        String str = "你已经*读过《小妇人》了吗？不，我还没有读过。";
        System.out.println(simpleCommonMethod.getChineseList(str));
    }


}

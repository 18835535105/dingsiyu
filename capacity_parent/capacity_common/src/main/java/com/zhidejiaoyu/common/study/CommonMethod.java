package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.*;

/**
 * 学生用户使用过程中重复使用的功用方法
 *
 * @author wuchenxi
 * @date 2018/5/21 15:34
 */
@Component
public class CommonMethod implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonMethod.class);

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private YouDaoTranslate youDaoTranslate;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudyCountMapper studyCountMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private CommonMethod commonMethod;

    /**
     * 判断本次学生登录是否学习了当前课程
     * <p>如果是本次登录第一次学习，将课程id放入session中，并在 study_count 表中将课程学习次数加1；如果当前课程是第一次学习，study_count 中不存在该条记录，插入新纪录；</p>
     * <p>如果不是本次登录第一次学习，不修改表数据</p>
     *
     * @param session
     * @param courseId
     * @return
     */
    public Integer saveStudyCount(HttpSession session, Long courseId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();
        Object courseObject = session.getAttribute("课程" + courseId);
        Long cId = null;
        if (courseObject != null) {
            cId = Long.valueOf(courseObject.toString());
        }
        Integer maxCount = studyCountMapper.selectMaxCountByCourseId(studentId, courseId);
        if (cId == null) {
            // 本次登录第一次学习当前课程
            session.setAttribute("课程" + courseId, courseId);
            if (maxCount == null) {
                // 当前课程是学生第一遍学习
                StudyCount studyCount = new StudyCount();
                studyCount.setStudentId(studentId);
                studyCount.setCourseId(courseId);
                studyCount.setCount(1);
                studyCount.setStudyCount(1);
                try {
                    studyCountMapper.insert(studyCount);
                } catch (Exception e) {
                    LOGGER.error("新增 study_count 信息出错！", e);
                    throw new RuntimeException("新增 study_count 信息出错！");
                }
            } else {
                List<StudyCount> studyCounts = studyCountMapper.selectByCourseIdAndCount(studentId, courseId, maxCount);
                StudyCount studyCount;
                if (studyCounts.size() > 0) {
                    // 如果查询出多条记录，只取第一条记录，其余记录删除
                    for (int i = 1; i < studyCounts.size(); i++) {
                        studyCount = studyCounts.get(i);
                        studyCountMapper.deleteByPrimaryKey(studyCount.getId());
                    }
                }
                studyCount = studyCounts.get(0);
                studyCount.setStudyCount(studyCount.getStudyCount() + 1);
                try {
                    studyCountMapper.updateByPrimaryKeySelective(studyCount);
                } catch (Exception e) {
                    LOGGER.error("更新 study_count 信息出错！", e);
                    throw new RuntimeException("更新 study_count 信息出错！");
                }
            }
        }
        return maxCount == null ? 1 : maxCount;
    }


    /**
     * 判断学生是否是第一次学习指定的模块
     * true:第一次学习当前模块，进入引导页
     * false：不是第一次学习当前模块，直接获取题目
     *
     * @param stuId      当前登录学生id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     * @return
     */
    public boolean isFirst(Long stuId, String studyModel) {
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andStudyModelEqualTo(studyModel).andTypeEqualTo(1);
        List<Learn> learns = learnMapper.selectByExample(learnExample);
        return learns.size() == 0;
    }

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
     * 获取当前年级所在的学段
     *
     * @param grade 当前年级
     * @return 初中 高中
     */
    public String getPhase(String grade) {
        String phase = null;
        switch (grade) {
            case "七年级":
            case "八年级":
            case "九年级":
                phase = "初中";
                break;

            case "高一":
            case "高二":
            case "高三":
            case "高中":
                phase = "高中";
                break;
            default:
        }
        return phase;
    }

    /**
     * 获取单词的音标
     *
     * @param word
     * @return
     */
    public String getSoundMark(String word) {
        Map<String, String> translateMap;
        try {
            translateMap = youDaoTranslate.getResultMap(word);
            String soundMark = "[" + translateMap.get("phonetic").split(";")[0] + "]";
            return soundMark.length() > 2 ? soundMark : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 初始化学生与单元之间的关系 默认开启所有课程的第一单元供学生选择，其余单元需通过学习进行
     *
     * @param student
     */
    public void initUnit(Student student) {

        // 判断学生是否已经推送过当前学段的课程
        int count = studentUnitMapper.countUnitCountByStudentId(student, commonMethod.getPhase(student.getGrade()));
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
                studentUnitMapper.insertList(studentUnits);
                Unit unit = units.get(0);

                // 初始化单词和例句首次学习的课程和单元
                Course course = courseMapper.selectByPrimaryKey(unit.getCourseId());
                long courseId = course.getId();
                long unitId = unit.getId();
                String courseName = course.getCourseName();
                student.setCourseName(courseName);
                student.setCourseId(courseId);
                student.setUnitId(unitId);
                student.setSentenceCourseId(courseId);
                student.setSentenceCourseName(courseName);
                student.setSentenceUnitId((int) unitId);
                studentMapper.updateByPrimaryKeySelective(student);
            }
        }
    }

    /**
     * 获取当前学生可学习的所有课程
     *
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
        return courseMapper.selectByExample(courseExample);
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
        studentCourseMapper.insertSelective(studentCourse);

        // 初始化例句模块
        studentCourse.setType(2);
        studentCourseMapper.insertSelective(studentCourse);
    }

    /**
     * 计算记忆追踪中字体大小等级
     *
     * @param object
     * @return -1:数据有误，其他：字体等级
     */
    public int getFontSize(Object object) {
        try {
            double memoryStrength = (Double) memoryDifficultyUtil.getFieldValue(object, object.getClass().getField("memoryStrength"));
            Date push = (Date) memoryDifficultyUtil.getFieldValue(object, object.getClass().getField("push"));
            if (push == null) {
                return 0;
            }
            double timeLag = this.timeLag(push);
            if (timeLag < 0) {
                return 0;
            } else if ((memoryStrength >= 0 && memoryStrength < 0.1) || timeLag > 10) {
                return 10;
            } else if ((memoryStrength >= 0.1 && memoryStrength < 0.2) || timeLag > 9) {
                return 9;
            } else if ((memoryStrength >= 0.2 && memoryStrength < 0.3) || timeLag > 8) {
                return 8;
            } else if ((memoryStrength >= 0.3 && memoryStrength < 0.4) || timeLag > 7) {
                return 7;
            } else if ((memoryStrength >= 0.4 && memoryStrength < 0.5) || timeLag > 6) {
                return 6;
            } else if ((memoryStrength >= 0.5 && memoryStrength < 0.6) || timeLag > 5) {
                return 5;
            } else if ((memoryStrength >= 0.6 && memoryStrength < 0.7) || timeLag > 4) {
                return 4;
            } else if ((memoryStrength >= 0.7 && memoryStrength < 0.8) || timeLag > 3) {
                return 3;
            } else if ((memoryStrength >= 0.8 && memoryStrength < 0.9) || timeLag > 2) {
                return 2;
            } else if ((memoryStrength >= 0.9 && memoryStrength < 1) || timeLag > 1) {
                return 1;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算当前时间黄金记忆点时间差
     *
     * @param push 黄金记忆点时间
     * @return <0:未到达黄金记忆点； >0:超过黄金记忆点的分钟
     */
    private double timeLag(Date push) {
        long pushTime = push.getTime();
        long nowTime = System.currentTimeMillis();
        return (nowTime * 1.0 - pushTime) / 60000;
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
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " . ");
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                        sentence = sentence + " .";
                    }
                } else {
                    sentence = sentence.replace(s, " " + s);
                }
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split(" ");
        for (String s : arr) {
            if (s.contains("#")||s.contains("*")) {
                list.add(s.replace("#", " ").replace("*"," ").replace("$",""));
            } else {
                list.add(s.trim());
            }
        }
        return list;
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
                words[i] = words[i].replace("#", " ").replace("$", "");
            }
            if (words[i].contains("*")) {
                words[i] = words[i].replace("*", " ");
            }
            list.add(words[i].trim());
        }

        if (StringUtils.isNotEmpty(exampleDisturb)) {
            list.add(exampleDisturb.replace("#", " ").replace("$", ""));
        }

        Collections.shuffle(list);
        return list;
    }

    public static void main(String[] args) {
        CommonMethod commonMethod = new CommonMethod();
        String str = "刚才*在街上*，我*碰巧*看见了我的叔叔。";
        System.out.println(commonMethod.getOrderChineseList(str, "昨天"));
        System.out.println(commonMethod.getChineseList(str));
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
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", " ");
                    if (sentence.substring(sentence.length() - 1).equals(".")) {
                        sentence = sentence.substring(0, sentence.length() - 1);
                    }
                } else {
                    sentence = sentence.replace(s, "*");
                }
            }
        }

        // 拆分并去除*
        String[] arr = sentence.split("\\*");
        List<String> list = new ArrayList<>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        for (String s : arr) {
            if (StringUtils.isNotEmpty(s)) {
                list.add(s);
            }
        }
        if (StringUtils.isNotEmpty(translateDisturb)) {
            list.add(translateDisturb);
        }
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
                if (".".equals(s)) {
                    sentence = sentence.replace(". ", "*" + "." + "*");
                    if (sentence.substring(sentence.length() - 1).equals(".")){
                        sentence = sentence.substring(0, sentence.length() - 1);
                        sentence = sentence + "*.*";
                    }
                } else {
                    sentence = sentence.replace(s, "*" + s + "*");
                }
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split("\\*");
        for (String s : arr) {
            if (s.contains("*")) {
                list.add(s.replace("*", "").replace("$", ""));
            } else if (StringUtils.isNotEmpty(s.trim())) {
                list.add(s.trim());
            }
        }
        return list;
    }

    /**
     * 将测试类型转换为汉字
     *
     * @param classify 类型 1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
     * @return 测试类型中文
     */
    public String getTestType(Integer classify) {
        if (classify != null) {
            if (classify == 0) {
                return "单词图鉴";
            } else if (1 == classify) {
                return "慧记忆";
            } else if (2 == classify) {
                return "慧听写";
            } else if (3 == classify) {
                return "慧默写";
            } else if (4 == classify) {
                return "例句听力";
            } else if (5 == classify) {
                return "例句翻译";
            } else if (6 == classify) {
                return "例句默写";
            } else if (7 == classify) {
                return "课文测试";
            }
        }
        return null;
    }
}

package com.zhidejiaoyu.common.study.simple;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class SimpleCommonMethod implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommonMethod.class);

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    @Autowired
    private LearnMapper learnMapper;

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
    private SimpleCommonMethod simpleCommonMethod;

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
        Long cId = (Long) session.getAttribute("课程" + courseId);
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
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andStudyModelEqualTo(studyModel);
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
     * 初始化学生与单元之间的关系 默认开启所有课程的第一单元供学生选择，其余单元需通过学习进行
     *
     * @param student
     */
    public void initUnit(Student student) {

        // 判断学生是否已经推送过当前学段的课程
        int count = studentUnitMapper.countUnitCountByStudentId(student, simpleCommonMethod.getPhase(student.getGrade()));
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
     * @param simpleCapacity
     * @return -1:数据有误，其他：字体等级
     */
    public int getFontSize(SimpleCapacity simpleCapacity) {
        Double memoryStrength = simpleCapacity.getMemoryStrength();
        Date push = simpleCapacity.getPush();
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

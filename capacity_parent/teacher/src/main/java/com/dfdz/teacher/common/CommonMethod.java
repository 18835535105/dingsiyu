package com.dfdz.teacher.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentUnitMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/8/15
 */
@Slf4j
@Component
public class CommonMethod {

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};


    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StudentUnitMapper studentUnitMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private StudentCourseMapper studentCourseMapper;
    @Resource
    private SimpleStudentUnitMapper simpleStudentUnitMapper;
    @Resource
    private CapacityStudentUnitMapper capacityStudentUnitMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CourseNewMapper courseNewMapper;
    @Resource
    private UnitNewMapper unitNewMapper;

    /**
     * 获取当前年级所在的学段
     *
     * @param grade 当前年级
     * @return 初中 高中
     */
    public static String getPhase(String grade) {
        if (StringUtils.isEmpty(grade)) {
            return null;
        }
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
    public List<CourseNew> initUnit(Student student, List<CourseNew> courses, Long startunit, Long endunit) {
        int count;
        if (courses == null) {
            // 判断学生是否已经推送过当前学段的课程
            count = studentUnitMapper.countUnitCountByStudentId(student, getPhase(student.getGrade()));
            if (count > 0) {
                // 当前学生已经推送过课程，不再重复推送
                throw new RuntimeException("学生当前学段课程已经推送！不可重复推送！");
            }
            // 推送当前学段下所有课程
            courses = getCourses(student);
        } else {
            courses = removePushedCourse(student, courses);
        }

        if (courses.size() > 0) {
            // 初始化我的课程页面数据
            initMyCourse(student, courses);

            // 查询当前学生在 simplestudentunit 中维护的所有type类型
            Map<Integer, Integer> typeMap = simpleStudentUnitMapper.selectTypeMap(student);

            // 查找课程下的所有单元
            List<Map<String, Object>> units = getAllUnitsInCourses(courses);
            CapacityStudentUnit capacity = capacityStudentUnitMapper.selectByStudentId(student.getId());
            boolean flag = true;
            // 课程id和课程版本的对应关系（用于精简版 simple_student_unit 初始化）
            Map<Long, String> idMap = new HashMap<>(16);
            Map<String, String> versionMap = getVersionMap();
            courses.parallelStream().forEach(course -> {
                String version = course.getVersion();
                if (version != null && version.length() > 4 && versionMap.containsKey(version.substring(version.length() - 4))) {
                    idMap.put(course.getId(), versionMap.get(version.substring(version.length() - 4)));
                }
            });

            // 对学习类型去重，防止simple_student_unit中type重复
            Map<Integer, Integer> modelMap = new HashMap<>(16);
            //判断是放入清学版还是智能版
            if (units.size() > 0) {
                for (Map<String, Object> unit : units) {
                    String version = (String) unit.get("version");
                    //如果version包含小学,初中,高中  为清学版   否则为智能版
                    if (version.contains("小学同步") || version.contains("初中同步") || version.contains("高中同步")
                            || version.contains("小学英语") || version.contains("初中英语") || version.contains("高中英语")) {
                        if (student.getUnitId() == null) {
                            // 初始化单词和例句首次学习的课程和单元
                            CourseNew course = courseNewMapper.selectById(Long.valueOf("" + unit.get("courseId")));
                            long courseId = course.getId();
                            long unitId = Long.parseLong("" + units.get(0).get("id"));
                            String courseName = course.getCourseName();
                            student.setCourseName(courseName);
                            student.setCourseId(courseId);
                            student.setUnitId(unitId);
                            student.setSentenceCourseId(courseId);
                            student.setSentenceCourseName(courseName);
                            student.setSentenceUnitId((int) unitId);
                            studentMapper.updateById(student);
                        }
                        addEdition(unit, modelMap, student, idMap, typeMap, versionMap);
                        String[] str = {"单词辨音", "词组辨音", "快速单词", "快速词组", "词汇考点", "快速句型", "语法辨析", "单词默写", "词组默写"};
                        for (String s1 : str) {
                            redisTemplate.opsForHash().delete(RedisKeysConst.PREFIX, RedisKeysConst.ALL_COURSE_WITH_STUDENT_IN_TYPE + student.getId() + ":" + s1);
                        }
                    } else {

                        addIntelligte(unit, student, idMap, typeMap, versionMap);
                        if (flag) {
                            Course course = null;
                            try {
                                course = courseMapper.selectById(Long.valueOf(unit.get("courseId").toString()));
                            } catch (NumberFormatException e) {
                                log.error("学生分配课程是获取课程信息出错", e);
                            }
                            flag = false;
                            if (capacity == null) {
                                CapacityStudentUnit capacityStudentUnit = new CapacityStudentUnit();
                                capacityStudentUnit.setCourseId(Long.valueOf("" + unit.get("courseId")));
                                capacityStudentUnit.setStudentId(student.getId());
                                capacityStudentUnit.setUnitId(Long.valueOf("" + unit.get("id")));
                                capacityStudentUnit.setUnitName((String) unit.get("unitName"));
                                capacityStudentUnit.setCourseName(course == null ? (String) unit.get("jointName") : course.getCourseName());
                                capacityStudentUnit.setType(1);
                                capacityStudentUnit.setVersion((String) unit.get("version"));
                                capacityStudentUnit.setStartunit(startunit);
                                capacityStudentUnit.setEndunit(endunit);
                                capacityStudentUnitMapper.insert(capacityStudentUnit);
                            } else {
                                capacity.setCourseId(Long.valueOf(unit.get("courseId").toString()));
                                capacity.setUnitName((String) unit.get("unitName"));
                                capacity.setUnitId(Long.valueOf(unit.get("id").toString()));
                                capacity.setCourseName(course == null ? (String) unit.get("jointName") : course.getCourseName());
                                capacity.setVersion((String) unit.get("version"));
                                capacity.setStartunit(startunit);
                                capacity.setEndunit(endunit);
                                capacityStudentUnitMapper.updateById(capacity);
                            }
                        }
                    }
                }
            }
        }
        return courses;
    }

    private void addIntelligte(Map<String, Object> map, Student student, Map<Long, String> idMap, Map<Integer, Integer> typeMap, Map<String, String> versionMap) {
        // 用于存放 simpleStudentUnit 中已存在的模块
        Map<Integer, Integer> modelMap = new HashMap<>(16);
        StudentUnit studentUnit;
        studentUnit = new StudentUnit();
        studentUnit.setCourseId(Long.valueOf(map.get("courseId") + ""));
        studentUnit.setUnitId(Long.valueOf(map.get("id") + ""));
        studentUnit.setStudentId(student.getId());
        String version = (String) map.get("version");
        if (version != null) {
            studentUnit.setType(1);
        }
        studentUnitMapper.insert(studentUnit);
    }

    /**
     * 增加清学版添加
     */
    private void addEdition(Map<String, Object> map, Map<Integer, Integer> modelMap, Student student, Map<Long, String> idMap, Map<Integer, Integer> typeMap, Map<String, String> versionMap) {
        // 用于存放 simpleStudentUnit 中已存在的模块
        StudentUnit studentUnit;
        SimpleStudentUnit simpleStudentUnit;
        studentUnit = new StudentUnit();
        studentUnit.setCourseId(Long.valueOf(map.get("courseId") + ""));
        studentUnit.setUnitId(Long.valueOf(map.get("id") + ""));
        studentUnit.setStudentId(student.getId());
        String version = (String) map.get("version");
        if (version != null) {
            studentUnit.setType(2);
        }
        if (!map.containsKey(Long.valueOf(map.get("courseId") + ""))) {
            // 所有课程的第一单元默认开启
            map.put(map.get("courseId") + "", Long.valueOf(map.get("courseId") + ""));
            UnitNew unit = unitNewMapper.selectFirstByUnitId(Long.valueOf(map.get("courseId").toString()));
            if (Long.valueOf(map.get("id").toString()).equals(unit.getId())) {
                studentUnit.setWordStatus(1);
                studentUnit.setSentenceStatus(1);
            } else {
                studentUnit.setWordStatus(0);
                studentUnit.setSentenceStatus(0);
            }
            String model = versionMap.get(idMap.get(Long.valueOf(map.get("courseId") + "")));
            if (model != null) {
                Integer type = getSimpleStudentUnitType(model);
                // modelMap 中不包含当前前 type 并且 typeMap中也不含有当前type才进行表维护
                boolean hasModel = !modelMap.containsKey(type) && !typeMap.containsKey(type);
                if (hasModel) {
                    modelMap.put(getSimpleStudentUnitType(model), 0);
                    simpleStudentUnit = new SimpleStudentUnit();
                    simpleStudentUnit.setCourseId(Long.valueOf("" + map.get("courseId")));
                    simpleStudentUnit.setStudentId(student.getId());
                    simpleStudentUnit.setType(getSimpleStudentUnitType(model));
                    simpleStudentUnit.setUnitId(Long.valueOf("" + map.get("id")));
                    simpleStudentUnitMapper.insert(simpleStudentUnit);
                }
            }

        } else {
            studentUnit.setWordStatus(0);
            studentUnit.setSentenceStatus(0);
        }

        studentUnitMapper.insert(studentUnit);


    }


    /**
     * 获取精简版版本map
     *
     * @return
     */
    private Map<String, String> getVersionMap() {
        Map<String, String> versionMap = new HashMap<>(16);
        versionMap.put("单词辨音", "单词辨音");
        versionMap.put("单词默写", "单词默写");
        versionMap.put("快速词组", "快速词组");
        versionMap.put("词组默写", "词组默写");
        versionMap.put("词组辨音", "词组辨音");
        versionMap.put("快速句型", "快速句型");
        versionMap.put("词汇考点", "词汇考点");
        versionMap.put("语法辨析", "语法辨析");
        versionMap.put("快速单词", "快速单词");
        return versionMap;
    }

    /**
     * 将清学版版本转换为相应的数字type
     *
     * @param model
     * @return
     */
    private Integer getSimpleStudentUnitType(String model) {
        String[] str = {"单词辨音", "词组辨音", "快速单词", "快速词组", "词汇考点", "快速句型", "语法辨析", "单词默写", "词组默写"};
        int length = str.length;
        for (int i = 0; i < length; i++) {
            if (str[i].equals(model)) {
                return i + 1;
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getAllUnitsInCourses(List<CourseNew> courses) {
        List<Long> courseIds = new ArrayList<>();
        courses.forEach(course -> courseIds.add(course.getId()));

        UnitOneExample unitExample = new UnitOneExample();
        unitExample.createCriteria().andCourseIdIn(courseIds).andDelstatusEqualTo(1);
        unitExample.setOrderByClause("unit.course_id asc,unit.id asc");
        return unitNewMapper.selectByExample(unitExample);
    }

    /**
     * 去除已经推送过的课程信息
     *
     * @param student
     * @param courses
     */
    private List<CourseNew> removePushedCourse(Student student, List<CourseNew> courses) {
        List<Long> courseIds = new ArrayList<>(courses.size());
        courses.parallelStream().forEach(course -> courseIds.add(course.getId()));
        // 判断学生当前课程是否已经推送，如果已经推送不再重复推送
        QueryWrapper<StudentUnit> studentUnitEntityWrapper = new QueryWrapper<>();
        studentUnitEntityWrapper.eq("student_id", student.getId()).in("course_id", courseIds);
        List<StudentUnit> studentUnits = studentUnitMapper.selectList(studentUnitEntityWrapper);

        // 去除已经推送过的课程信息
        if (studentUnits.size() > 0) {
            Map<Long, Long> map = new HashMap<>(16);
            studentUnits.parallelStream().forEach(studentUnit -> map.put(studentUnit.getCourseId(), student.getCourseId()));

            List<CourseNew> newCourseList = new ArrayList<>();

            for (CourseNew course : courses) {
                if (!map.containsKey(course.getId())) {
                    newCourseList.add(course);
                }
            }

            return newCourseList;
        }
        return courses;
    }

    /**
     * 获取当前学生可学习的所有课程
     *
     * @param student
     * @return
     */
    private List<CourseNew> getCourses(Student student) {
        String version = student.getVersion();
        String grade = student.getGrade();

        CourseExample courseExample = new CourseExample();
        courseExample.createCriteria().andVersionEqualTo(version).andGradeLike(grade.contains("年级") ? "%年级" : "高%")
                .andStatusEqualTo(1);
        courseExample.setOrderByClause("id asc");
        return courseNewMapper.selectByExample(courseExample);
    }

    private void initMyCourse(Student student, List<CourseNew> courses) {
        CourseNew course = courses.get(0);
        // 初始化单词模块
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourseId(course.getId());
        studentCourse.setCourseName(course.getCourseName());
        studentCourse.setStudentId(student.getId());
        studentCourse.setUpdateTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        studentCourse.setType(1);
        studentCourseMapper.insert(studentCourse);

        // 初始化例句模块
        studentCourse.setType(2);
        studentCourseMapper.insert(studentCourse);
    }
}

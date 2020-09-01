package com.dfdz.teacher.common;

import com.dfdz.teacher.business.student.service.StudentUnitService;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentUnit;
import com.zhidejiaoyu.common.pojo.UnitOneExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018/8/15
 */
@Slf4j
@Component
public class CommonMethod {

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private StudentUnitService studentUnitService;

    /**
     * 初始化学生与单元之间的关系 默认开启所有课程的第一单元供学生选择，其余单元需通过学习进行
     *
     * @param students
     */
    public void initUnit(List<Student> students, List<CourseNew> courses) {
        // 查找课程下的所有单元
        List<Map<String, Object>> units = getAllUnitsInCourses(courses);

        List<StudentUnit> list = new ArrayList<>();
        students.forEach(student -> units.forEach(unit -> {
            // 用于存放 simpleStudentUnit 中已存在的模块
            StudentUnit studentUnit = packageStudentUnit(student, unit);
            list.add(studentUnit);
        }));
        studentUnitService.saveBatch(list);
    }

    private StudentUnit packageStudentUnit(Student student, Map<String, Object> unit) {
        StudentUnit studentUnit = new StudentUnit();
        studentUnit.setCourseId(Long.valueOf(unit.get("courseId") + ""));
        studentUnit.setUnitId(Long.valueOf(unit.get("id") + ""));
        studentUnit.setStudentId(student.getId());
        String version = (String) unit.get("version");
        if (version != null) {
            studentUnit.setType(1);
        }
        return studentUnit;
    }

    private List<Map<String, Object>> getAllUnitsInCourses(List<CourseNew> courses) {
        List<Long> courseIds = new ArrayList<>();
        courses.forEach(course -> courseIds.add(course.getId()));

        UnitOneExample unitExample = new UnitOneExample();
        unitExample.createCriteria().andCourseIdIn(courseIds);
        unitExample.setOrderByClause("unit.course_id asc,unit.id asc");
        return unitNewMapper.selectByExample(unitExample);
    }

}

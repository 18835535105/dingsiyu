package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleTeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 获取教师及校长信息工具类
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
@Component
public class TeacherInfoUtil {

    private static TeacherMapper teacherMapper;

    @Autowired
    private TeacherMapper teacherMapperInit;

    @Autowired
    private SimpleTeacherMapper simpleTeacherInit;

    private static SimpleTeacherMapper simpleTeacherMapper;

    @PostConstruct
    public void init() {
        teacherMapper = this.teacherMapperInit;
        simpleTeacherMapper = this.simpleTeacherInit;
    }

    /**
     * 获取校管id
     *
     * @param student
     * @return
     */
    public static Integer getSchoolAdminId(Student student) {
        if (student.getTeacherId() == null) {
            return null;
        }
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(Integer.valueOf(student.getTeacherId().toString()));
        if (schoolAdminId == null) {
            return Integer.valueOf(student.getTeacherId().toString());
        }
        return schoolAdminId;
    }

    /**
     * 获取教师id
     */
    public static Long getSchoolAdminIdAndTeacherId(Student student, List<Integer> teachers) {
        Long schoolAdminId = null;
        if (student.getTeacherId() != null) {
            Integer schoolAdminById = simpleTeacherMapper.getSchoolAdminById(student.getTeacherId().intValue());
            if (schoolAdminById == null) {
                Integer teacherCountByAdminId = simpleTeacherMapper.getTeacherCountByAdminId(student.getTeacherId());
                if (teacherCountByAdminId != null && teacherCountByAdminId > 0) {
                    schoolAdminId = student.getTeacherId();
                }
            } else {
                schoolAdminId = schoolAdminById.longValue();
            }
        }
        teachers = simpleTeacherMapper.getTeacherIdByAdminId(schoolAdminId.intValue());
        return schoolAdminId;
    }
}

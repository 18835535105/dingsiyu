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

    @PostConstruct
    public void init() {
        teacherMapper = this.teacherMapperInit;
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
}

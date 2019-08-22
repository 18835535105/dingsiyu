package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 学生常用操作方法
 *
 * @author: wuchenxi
 * @Date: 2019-08-22 15:39
 */
@Component
public class StudentUtil {

    private static StudentMapper studentMapper;

    @Autowired
    private StudentMapper studentMapper1;

    @PostConstruct
    public void init() {
        studentMapper = this.studentMapper1;
    }

    /**
     * 根据校管 id 查询当前学校下所有学生信息
     *
     * @param schoolAdminId 校管 id（不能是教师 id）
     * @return
     */
    public static List<Student> getStudentBySchoolAdmin(Integer schoolAdminId) {
        // 查询校管下所有教师负责的学生信息
        List<Student> students = studentMapper.selectBySchoolAdminId(schoolAdminId);
        // 查询校管直接负责的学生信息
        students.addAll(studentMapper.selectByTeacherId(schoolAdminId));
        return students;
    }
}

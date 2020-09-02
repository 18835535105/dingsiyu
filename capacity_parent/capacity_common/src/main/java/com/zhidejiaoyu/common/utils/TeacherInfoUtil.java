package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 获取教师及校长信息工具类
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
@Slf4j
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
        Long teacherId = student.getTeacherId();
        if (teacherId == null) {
            log.error("未查询到学生[{} - {} - {}]的教师信息！", student.getId(), student.getAccount(), student.getStudentName());
            throw new ServiceException(500, "当前学生没有教师信息！");
        }
        return getSchoolAdminId(Integer.parseInt(teacherId.toString()));
    }

    /**
     * 获取校管id
     *
     * @param teacherId 学生的所属教师id
     * @return
     */
    public static Integer getSchoolAdminId(Integer teacherId) {
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(teacherId);
        if (schoolAdminId == null) {
            return teacherId;
        }
        return schoolAdminId;
    }
}

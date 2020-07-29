package com.dfdz.teacher.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dfdz.teacher.dto.AddNewStudentDto;
import com.zhidejiaoyu.common.pojo.Student;

public interface StudentService extends IService<Student> {
    Object createNewStudent(AddNewStudentDto dto);
}

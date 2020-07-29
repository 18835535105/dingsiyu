package com.dfdz.teacher.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;

public interface CourseService extends IService<CourseNew> {


    void deleteStudyUnit(Student student);
}

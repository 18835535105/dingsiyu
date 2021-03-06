package com.dfdz.teacher.business.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Student;

/**
 * 学生账号生成相关逻辑
 *
 * @author: wuchenxi
 * @Date: 2019/11/26 09:56
 */
public interface CreateStudentService extends IService<Student> {

    Object canCreateCount(String openId);
}

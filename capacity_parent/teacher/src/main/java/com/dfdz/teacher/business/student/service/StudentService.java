package com.dfdz.teacher.business.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;

/**
 * @author: wuchenxi
 * @date: 2020/7/29 15:48:48
 */
public interface StudentService extends IService<Student> {

    /**
     * 查询学生列表
     *
     * @param dto
     * @return
     */
    ServerResponse<PageVo<StudentManageVO>> listStudent(StudentListDto dto);

    Object createNewStudent(AddNewStudentDto dto);
}

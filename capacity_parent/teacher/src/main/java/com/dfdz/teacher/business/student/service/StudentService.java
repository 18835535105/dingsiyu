package com.dfdz.teacher.business.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.SaveEditStudentInfoDTO;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;

import java.util.Map;

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

    /**
     * 创建学生
     *
     * @param dto
     * @return
     */
    Object createNewStudent(AddNewStudentDto dto);

    /**
     * 获取教师后台学生需要编辑的信息
     *
     * @param uuid
     * @return
     */
    ServerResponse<EditStudentVo> getEditStudentVoByUuid(String uuid);

    /**
     * 教师后台保存编辑后的学生信息
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> saveStudentInfoAfterEdit(SaveEditStudentInfoDTO dto);


    ServerResponse<Object> deleteStudentByUuid(String studentUuid,String userUuid);
}

package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.SaveEditStudentInfoDTO;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 教师服务
 *
 * @author wuchenxi
 * @date 2020-07-27 11:50:12
 */
public interface BaseTeacherInfoFeignClient {

    @GetMapping("/teacher/getByUuid")
    ServerResponse<SysUser> getByUuid(@RequestParam String uuid);

    /**
     * 查询学生列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/student/listStudent")
    ServerResponse<PageVo<StudentManageVO>> listStudent(@SpringQueryMap StudentListDto dto);

    /**
     * 教师后台获取需要编辑的学生信息
     *
     *
     * @param openId
     * @param uuid  学生uuid
     * @return
     */
    @GetMapping("/student/edit/getEditStudentVoByUuid")
    ServerResponse<EditStudentVo> getEditStudentVoByUuid(@RequestParam String openId, @RequestParam String uuid);

    /**
     * 保存编辑后的学生信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/student/edit/saveStudentInfo")
    ServerResponse<Object> saveStudentInfo(@RequestBody SaveEditStudentInfoDTO dto);

    /**
     * 教师后台获取需要编辑的学生信息
     *
     * @return
     */
    @GetMapping("/student/create/createNewStudent")
    Object createNewStudent(@RequestBody AddNewStudentDto dto);
}

package com.zhidejioayu.center.business.teacher.controller;

import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import com.zhidejioayu.center.business.feignclient.student.BaseStudentFeignClient;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherInfoFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author wuchenxi
 * @date 2020-07-29 11:45:37
 */
@Validated
@RestController
@RequestMapping("/teacher/student")
public class StudentController {

    /**
     * 获取学生列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/listStudent")
    public ServerResponse<PageVo<StudentManageVO>> listStudent(@Valid StudentListDto dto) {
        if (StringUtil.isNotEmpty(dto.getOrderWay()) && !dto.getOrderWay().equals("desc") && !dto.getOrderWay().equals("asc")) {
            return ServerResponse.createByError(400, "orderWay 参数错误！");
        }
        BaseTeacherInfoFeignClient baseTeacherInfoFeignClient = FeignClientUtil.getBaseTeacherInfoFeignClientByOpenId(dto.getOpenId());
        return baseTeacherInfoFeignClient.listStudent(dto);
    }

    /**
     * 获取需要编辑的学生信息
     *
     * @param uuid  学生uuid
     * @return
     */
    @GetMapping("/edit/getByUuid")
    public ServerResponse<EditStudentVo> getByUuid(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        BaseStudentFeignClient baseStudentFeignClientByUuid = FeignClientUtil.getBaseStudentFeignClientByUuid(uuid);
        return baseStudentFeignClientByUuid.getEditStudentVoByUuid(uuid);
    }
}

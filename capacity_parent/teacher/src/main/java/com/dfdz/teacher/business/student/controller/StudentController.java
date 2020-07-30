package com.dfdz.teacher.business.student.controller;

import com.dfdz.teacher.business.student.service.StudentService;
import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.support.StrKit;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wuchenxi
 * @date 2020-07-29 15:46:33
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 查询学生列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/listStudent")
    ServerResponse<PageVo<StudentManageVO>> listStudent(@SpringQueryMap StudentListDto dto) {
        return studentService.listStudent(dto);
    }

    /**
     * 生成学生账号和密码，用于学校教师分配给学生
     *
     * @param dto
     */
    @ResponseBody
    @PostMapping("/create/createNewStudent")
    public Object createNewStudent(@Valid AddNewStudentDto dto) {
        checkPhase(dto.getPhase());
        if (StrKit.isEmpty(dto.getSchoolName())) {
            return ServerResponse.createByError(400, "请选择学校名称！");
        }
        if(dto.getAdminUUID()==null){
            return ServerResponse.createByError(400, "未传入管理员id！");
        }
        return studentService.createNewStudent(dto);
    }


    private void checkPhase(String phase) {
        Map<String, String> phaseMap = new HashMap<>(16);
        phaseMap.put("小学", "小学");
        phaseMap.put("初中", "初中");
        phaseMap.put("高中", "高中");
        if (!phaseMap.containsKey(phase)) {
            throw new ServiceException(400, "请选择正确的学段！");
        }
    }
}

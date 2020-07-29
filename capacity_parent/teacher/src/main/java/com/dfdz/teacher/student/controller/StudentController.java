package com.dfdz.teacher.student.controller;

import com.dfdz.teacher.common.Const;
import com.dfdz.teacher.dto.AddNewStudentDto;
import com.dfdz.teacher.exception.GunsException;
import com.dfdz.teacher.student.service.StudentService;
import com.zhidejiaoyu.common.support.StrKit;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 生成学生账号和密码，用于学校教师分配给学生
     *
     * @param dto
     */
    @ResponseBody
    @PostMapping("/create/createNewStudent")
    public Object createNewStudent(@Valid AddNewStudentDto dto, BindingResult result) {
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
            throw new GunsException(400, "请选择正确的学段！");
        }
    }
}

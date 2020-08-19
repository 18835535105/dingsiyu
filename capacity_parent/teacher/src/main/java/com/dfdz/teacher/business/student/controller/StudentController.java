package com.dfdz.teacher.business.student.controller;

import com.dfdz.teacher.business.student.service.StudentService;
import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.SaveEditStudentInfoDTO;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
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
    public ServerResponse<PageVo<StudentManageVO>> listStudent(@SpringQueryMap StudentListDto dto) {
        return studentService.listStudent(dto);
    }

    /**
     * 教师后台获取需要编辑的学生信息
     *
     * @param uuid 学生uuid
     * @return
     */
    @GetMapping("/edit/getEditStudentVoByUuid")
    public ServerResponse<EditStudentVo> getEditStudentVoByUuid(@RequestParam String uuid) {
        return studentService.getEditStudentVoByUuid(uuid);
    }

    /**
     * 教师后台删除学员
     *
     * @param studentUuid
     * @param userUuid
     * @return
     */
    @GetMapping("/edit/deleteStudentByUuid")
    public ServerResponse<Object> deleteStudentByUuid(@RequestParam String studentUuid, @RequestParam String userUuid) {
        return studentService.deleteStudentByUuid(studentUuid,userUuid);
    }

    /**
     * 保存编辑后的学生信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/edit/saveStudentInfo")
    ServerResponse<Object> saveStudentInfo(@RequestBody SaveEditStudentInfoDTO dto) {
        return studentService.saveStudentInfoAfterEdit(dto);
    }

    /**
     * 生成学生账号和密码，用于学校教师分配给学生
     *
     * @param dto
     */
    @ResponseBody
    @PostMapping("/create/createNewStudent")
    public Object createNewStudent(@RequestBody AddNewStudentDto dto) {
        checkPhase(dto.getPhase());
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

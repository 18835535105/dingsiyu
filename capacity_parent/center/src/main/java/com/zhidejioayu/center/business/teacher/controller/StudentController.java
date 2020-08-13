package com.zhidejioayu.center.business.teacher.controller;

import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.SaveEditStudentInfoDTO;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherInfoFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author wuchenxi
 * @date 2020-07-29 11:45:37
 */
@Validated
@RestController
@RequestMapping("/teacher/student")
public class StudentController {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

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
     * @param openId 教师openId
     * @param uuid   学生uuid
     * @return
     */
    @GetMapping("/edit/getByUuid")
    public ServerResponse<EditStudentVo> getByUuid(@RequestParam String openId, @RequestParam String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }

        BaseTeacherInfoFeignClient baseTeacherInfoFeignClientByOpenId = FeignClientUtil.getBaseTeacherInfoFeignClientByOpenId(openId);
        return baseTeacherInfoFeignClientByOpenId.getEditStudentVoByUuid(openId, uuid);
    }

    /**
     * 保存编辑后的学生信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/edit/saveStudentInfo")
    public ServerResponse<Object> saveStudentInfo(@Valid SaveEditStudentInfoDTO dto) {
        BaseTeacherInfoFeignClient baseTeacherInfoFeignClientByOpenId = FeignClientUtil.getBaseTeacherInfoFeignClientByOpenId(dto.getOpenId());
        return baseTeacherInfoFeignClientByOpenId.saveStudentInfo(dto);
    }

    /**
     * 生成学生账号和密码，用于学校教师分配给学生
     *
     * @param dto
     */
    @PostMapping("/create/createNewStudent")
    public Object createNewStudent(@Valid AddNewStudentDto dto) {
        BaseTeacherInfoFeignClient baseStudentFeignClientByUuid = FeignClientUtil.getBaseTeacherInfoFeignClientByOpenId(dto.getOpenId());
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByTeacherOpenid(dto.getOpenId());
        dto.setServerNo(serverConfig.getServerNo());
        return baseStudentFeignClientByUuid.createNewStudent(dto);
    }

    /**
     * 生成学生账号和密码，用于学校教师分配给学生
     *
     * @param openId
     */
    @GetMapping("/create/createStudentCount")
    public Object createNewStudent(@RequestParam String openId) {
        BaseTeacherInfoFeignClient baseStudentFeignClientByUuid = FeignClientUtil.getBaseTeacherInfoFeignClientByOpenId(openId);
        return baseStudentFeignClientByUuid.canCreateCount(openId);
    }

}

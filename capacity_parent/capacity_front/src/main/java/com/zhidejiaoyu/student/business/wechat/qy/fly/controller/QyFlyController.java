package com.zhidejiaoyu.student.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 企业微信智慧飞行
 *
 * @author: wuchenxi
 * @date: 2020/6/16 13:52:52
 */
@RestController
@RequestMapping("/qy/fly")
public class QyFlyController {

    @Resource
    private QyFlyService qyFlyService;

    /**
     * 上传飞行记录
     *
     * @param file
     * @param studentId
     * @param num       作业本中二维码序号
     * @return
     */
    @PostMapping("/uploadFlyRecord")
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num) {
        if (file == null) {
            return ServerResponse.createByError(400, "上传的图片不能为空！");
        }
        if (studentId == null) {
            return ServerResponse.createByError(400, "studentId can't be null!");
        }
        if (num == null) {
            return ServerResponse.createByError(400, "二维码序号不能为空！");
        }
        return qyFlyService.uploadFlyRecord(file, studentId, num);
    }

    /**
     * 获取当前教师下的所有学生
     *
     * @param openId 教师openId
     * @param dto    查询条件
     * @return
     */
    @GetMapping("/getStudents")
    public ServerResponse<Object> getStudents(String openId, SearchStudentDTO dto) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        return qyFlyService.getStudents(openId, dto);
    }

}

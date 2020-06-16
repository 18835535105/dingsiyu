package com.zhidejiaoyu.student.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
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

}

package com.zhidejiaoyu.student.business.video.controller;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.feignclient.center.VideoFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author wuchenxi
 * @date 2020-09-03 16:07:10
 */
@RestController
@RequestMapping("/video")
public class VideoController extends BaseController {

    @Resource
    private VideoFeignClient videoFeignClient;

    @PostMapping("/saveVideo")
    public ServerResponse<Object> saveVideo(String videoId) {
        Student student = super.getStudent();
        return videoFeignClient.saveVideo(student.getUuid(), videoId);
    }
}

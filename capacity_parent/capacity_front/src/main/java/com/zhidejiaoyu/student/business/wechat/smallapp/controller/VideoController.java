package com.zhidejiaoyu.student.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 小程序视频接口
 *
 * @author: wuchenxi
 * @date: 2020/4/15 16:51:51
 */
@RestController
@RequestMapping("/smallApp/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    /**
     * 获取视频路径
     *
     * @param openId
     * @return
     */
    @GetMapping("/getVideo")
    public ServerResponse<Object> getVideo(String openId) {
        return ServerResponse.createBySuccess("http://media.yydz100.com/sv/5116dc8a-170e26c49cb/5116dc8a-170e26c49cb.mp4");
    }

    /**
     * 保存已观看视频信息
     *
     * @param openId
     * @param gold   奖励的金币数
     * @return
     */
    @PostMapping("/saveVideo")
    public ServerResponse<Object> saveVideo(String openId, Integer gold) {
        return ServerResponse.createBySuccess();
    }
}

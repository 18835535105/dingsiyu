package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.VideoService;
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
@RequestMapping("/wechat/smallApp/video")
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
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createBySuccess(400, "openId can't be null!");
        }
        return videoService.getVideo(openId);
    }

    /**
     * 保存已观看视频信息
     *
     * @param openId
     * @param gold   奖励的金币数
     * @return
     */
    @PostMapping("/saveVideo")
    public ServerResponse<Object> saveVideo(String openId, Integer gold, String videoId) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createBySuccess(400, "openId can't be null!");
        }
        if (StringUtil.isEmpty(videoId)) {
            return ServerResponse.createBySuccess(400, "videoId can't be null!");
        }
        if (gold == null) {
            return ServerResponse.createBySuccess(400, "gold can't be null!");
        }
        return videoService.saveVideo(openId, gold, videoId);
    }
}

package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.study.video.VideoCourseVO;
import com.zhidejiaoyu.common.vo.study.video.VideoUnitVO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.VideoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 查询指定年级的视频数据
     *
     * @param grades    小于或者等于当前年级的所有年级
     * @param nextGrade 当前年级的下个年级数据
     * @return
     */
    @GetMapping("/getVideoCourse")
    List<VideoCourseVO> getVideoCourse(@RequestParam List<String> grades, @RequestParam(required = false) String nextGrade) {
        return videoService.getVideoCourse(grades, nextGrade);
    }

    /**
     * 查询视频的单元信息
     *
     * @param uuid    学生uuid
     * @param videoId
     * @return
     */
    @GetMapping("/getVideoUnitInfo")
    public List<VideoUnitVO> getVideoUnitInfo(@RequestParam String uuid, @RequestParam String videoId) {
        return videoService.getVideoUnitInfo(uuid, videoId);
    }

    /**
     * 保存观看的视频
     *
     * @param uuid
     * @param videoId
     * @return
     */
    @PostMapping("/savePCVideo")
    public ServerResponse<Object> saveVideo(@RequestParam String uuid, @RequestParam String videoId) {
        return videoService.saveVideo(uuid, videoId);
    }
}

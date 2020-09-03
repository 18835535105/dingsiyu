package com.zhidejiaoyu.student.business.feignclient.center;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.study.video.VideoCourseVO;
import com.zhidejiaoyu.common.vo.study.video.VideoUnitVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 视频
 *
 * @author: wuchenxi
 * @date: 2020/9/3 14:03:03
 */
@FeignClient(name = "center", path = "/center/wechat/smallApp/video")
public interface VideoFeignClient {

    /**
     * 查询指定年级的视频数据
     *
     * @param grades    小于或者等于当前年级的所有年级
     * @param nextGrade 当前年级的下个年级数据
     * @return
     */
    @GetMapping("/getVideoCourse")
    List<VideoCourseVO> getVideoCourse(@RequestParam List<String> grades, @RequestParam(required = false) String nextGrade);

    /**
     * 查询视频的单元信息
     *
     * @param uuid
     * @param videoId
     * @return
     */
    @GetMapping("/getVideoUnitInfo")
    List<VideoUnitVO> getVideoUnitInfo(@RequestParam String uuid, @RequestParam String videoId);

    /**
     * 保存观看的视频
     *
     * @param uuid
     * @param videoId
     * @return
     */
    @PostMapping("/savePCVideo")
    ServerResponse<Object> saveVideo(@RequestParam String uuid, @RequestParam String videoId);
}

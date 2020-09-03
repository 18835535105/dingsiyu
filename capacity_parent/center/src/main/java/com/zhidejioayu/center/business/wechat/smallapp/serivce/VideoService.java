package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.study.video.VideoCourseVO;
import com.zhidejiaoyu.common.vo.study.video.VideoUnitVO;

import java.util.List;

/**
 * 小程序视频
 *
 * @author: wuchenxi
 * @date: 2020/4/15 16:53:53
 */
public interface VideoService {

    /**
     * 保存已观看视频信息
     *
     * @param openId
     * @param gold    奖励的金币数
     * @param videoId
     * @return
     */
    ServerResponse<Object> saveVideo(String openId, Integer gold, String videoId);

    /**
     * 获取小程序学习视频
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> getVideo(String openId);

    /**
     * 查询指定年级的视频数据
     *
     * @param grades    小于或者等于当前年级的所有年级
     * @param nextGrade 当前年级的下个年级数据
     * @return
     */
    List<VideoCourseVO> getVideoCourse(List<String> grades, String nextGrade);

    /**
     * 查询视频的单元信息
     *
     *
     * @param uuid
     * @param videoId
     * @return
     */
    List<VideoUnitVO> getVideoUnitInfo(String uuid, String videoId);
}

package com.zhidejiaoyu.common.vo.study.video;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wuchenxi
 * @date 2020-09-03 15:24:50
 */
@Data
public class VideoListVO implements Serializable {

    /**
     * 视频id
     */
    private String id;

    /**
     * 视频播放路径
     */
    private String url;

    /**
     * 视频播放状态；1：已播放；2：未播放
     */
    private Integer state;

    private String unit;
}

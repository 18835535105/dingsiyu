package com.zhidejiaoyu.common.vo.study.video;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频课程
 *
 * @author wuchenxi
 * @date 2020-09-03 14:12:09
 */
@Data
public class VideoCourseVO implements Serializable {

    /**
     * 视频id
     */
    private String id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 视频上下册
     */
    private String label;
}

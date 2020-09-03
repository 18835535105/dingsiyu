package com.zhidejiaoyu.common.vo.study.video;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 视频的单元信息
 *
 * @author wuchenxi
 * @date 2020-09-03 14:52:37
 */
@Data
public class VideoUnitVO implements Serializable {

    private String unitName;

    private List<VideoListVO> voList;
}

package com.zhidejiaoyu.common.vo.wechat.smallapp.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信小视频
 *
 * @author wuchenxi
 * @date 2020-07-28 09:18:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoVO implements Serializable {

    /**
     * 视频id
     */
    private String id;

    /**
     * 视频播放地址
     */
    private String url;
}

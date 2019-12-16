package com.zhidejiaoyu.common.vo.feedbackvo;

import lombok.Data;

/**
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
@Data
public class FeedBackInfoList {
    /**
     * 学生或者管理人员头像路径
     */
    private String headUrl;

    /**
     * 回复及反馈内容
     */
    private String content;

    /**
     * 角色 0：学生；1：管理人员<br>
     * 当 role == 0 时，content 内容显示在右侧，否则显示在左侧
     */
    private Integer role;

    /**
     * 反馈及回复时间
     */
    private String time;
}

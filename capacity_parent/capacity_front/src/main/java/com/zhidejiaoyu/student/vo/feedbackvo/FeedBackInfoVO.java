package com.zhidejiaoyu.student.vo.feedbackvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生进入反馈页面响应数据vo
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
@Data
public class FeedBackInfoVO {

    /**
     * 金币奖励个数
     */
    private Integer awardGold;

    /**
     * 是否显示金币奖励提示框
     */
    private Boolean hint;

    private List<FeedBackInfoList> feedBackInfoLists;
}

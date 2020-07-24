package com.zhidejioayu.center.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnAdminVo {
    /**
     * 校管id
     */
    private Long adminId;
    /**
     * 有效时常
     */
    private Long vaildTime;
    /**
     * 学习单词数
     */
    private Integer wordCount;
    /**
     * 学习获取金币数
     */
    private Integer gold;
    /**
     * 点赞微信信息
     */
    private List<Map<String,Object>>  weChatList;
    /**
     * 分数
     */
    private Integer point;
    /**
     * 头像图片
     */
    private String imgUrl;
    /**
     * 背景图片
     */
    private String weChatName;
    /**
     * 微信图片
     */
    private String weChatImgUrl;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 头像id
     */
    private String headPortrait;
    /**
     * 学生的openId
     */
    private String studentOpenId;
    /**
     * 学习时间
     */
    private String learnTime;

}

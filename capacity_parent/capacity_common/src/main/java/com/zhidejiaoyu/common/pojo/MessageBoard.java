package com.zhidejiaoyu.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生反馈留言板
 *
 * @author wuchenxi
 */
@Getter
@Setter
@ToString
public class MessageBoard implements Serializable {
    private Long id;

    /**
     * 写留言的学生id
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生学号
     */
    private String studentAccount;

    private String schoolName;

    /**
     * 回复学生留言的后台人员id
     */
    private Long replyUserId;

    /**
     * 学生留言内容和后台人员回复内容
     */
    private String content;

    /**
     * 学生提交留言的时间或者后台人员回复时间
     */
    private Date time;

    /**
     * 1：管理人员已读；2：管理人员未读；3:管理人员已回复,学生未读；4：学生已读
     */
    private Integer readFlag;

    /**
     * 1：采纳；2：未采纳
     */
    private Integer acceptFlag;

    /**
     * 奖励金币数
     */
    private Integer awardGold;

    /**
     * 1：提示；2：不提示；是否提示学生金币奖励信息（学生留言被采纳后会有奖励信息提示）
     */
    private Integer hintFlag;

    /**
     * 0：学生；1：后台人员
     */
    private Integer role;

    /**
     * 禁言结束时间
     */
    private Date stopSpeakEndTime;
}
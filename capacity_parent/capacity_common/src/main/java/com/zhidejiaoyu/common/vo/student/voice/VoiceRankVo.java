package com.zhidejiaoyu.common.vo.student.voice;

import lombok.Data;

/**
 * 好声音排行vo
 *
 * @author wuchenxi
 * @date 2018/8/29
 */
@Data
public class VoiceRankVo {

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 排行分数
     */
    private Integer score;

    /**
     * 录音地址
     */
    private String voiceUrl;

    /**
     * 学生头像
     */
    private String headUrl;
}

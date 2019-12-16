package com.zhidejiaoyu.common.vo.student.voice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 好声音题目集合vo
 *
 * @author wuchenxi
 * @date 2018/8/29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoiceVo {

    /**
     * 单词、例句id
     */
    private Long id;

    /**
     * 单词、例句英文
     */
    private String word;

    /**
     * 单词、例句中文翻译
     */
    private String chinese;

    /**
     * 单词音节
     */
    private String syllable;

    /**
     * 单词音标
     */
    private String soundMark;

    /**
     * 单词、例句读音地址
     */
    private String readUrl;

    /**
     * 声音
     */
    private String voice;
}

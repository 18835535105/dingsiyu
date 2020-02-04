package com.zhidejiaoyu.common.vo.beforelearngame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 单词学前游戏测试题目
 *
 * @author: wuchenxi
 * @date: 2020/2/4 14:50:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectVO implements Serializable {

    /**
     * 题目：单词翻译
     */
    private String topic;

    /**
     * 选项：单词
     */
    private List<AnswerVO> select;

}

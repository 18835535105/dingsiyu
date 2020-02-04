package com.zhidejiaoyu.common.vo.beforelearngame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 单词学前游戏测试答案
 *
 * @author: wuchenxi
 * @date: 2020/2/4 14:53:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVO implements Serializable {

    /**
     * 答案英文单词
     */
    private String title;

    private Boolean right;
}

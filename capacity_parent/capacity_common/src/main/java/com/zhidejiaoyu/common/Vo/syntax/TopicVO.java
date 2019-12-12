package com.zhidejiaoyu.common.Vo.syntax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 写语法题目
 *
 * @author: wuchenxi
 * @Date: 2019/11/4 09:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicVO implements Serializable {
    /**
     * 题目
     */
    private String title;

    /**
     * 答案
     */
    private String answer;
}

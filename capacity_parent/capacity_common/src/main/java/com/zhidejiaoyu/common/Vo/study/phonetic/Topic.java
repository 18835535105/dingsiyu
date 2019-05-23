package com.zhidejiaoyu.common.Vo.study.phonetic;

import lombok.Data;

import java.io.Serializable;

/**
 * 音节辨音答案
 *
 * @author wuchenxi
 * @date 2019-05-20
 */
@Data
public class Topic implements Serializable {
    /**
     * 单词
     */
    private String word;

    /**
     * 是否是正确答案
     */
    private boolean answer;
}

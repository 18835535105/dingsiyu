package com.zhidejiaoyu.common.vo.read;

import lombok.Data;

import java.io.Serializable;

/**
 * 生词本列表数据
 *
 * @author wuchenxi
 * @date 2019-07-24
 */
@Data
public class NewWordsBookVo implements Serializable {

    private String word;

    /**
     * 音标
     */
    private String phonetic;

    /**
     * 读音地址
     */
    private String readUrl;

    /**
     * 词义
     */
    private String chinese;
}

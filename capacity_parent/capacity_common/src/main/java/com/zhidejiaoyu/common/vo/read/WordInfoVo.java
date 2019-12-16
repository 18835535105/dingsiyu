package com.zhidejiaoyu.common.vo.read;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 单词翻译数据
 *
 * @author wuchenxi
 * @date 2019-07-23
 */
@Data
public class WordInfoVo implements Serializable {

    /**
     * 单词 id
     */
    private Long wordId;

    /**
     * 单词
     */
    private String word;

    /**
     * 美式读音地址
     */
    private String usReadUrl;

    /**
     * 美式音标
     */
    private String usPhonetic;

    /**
     * 英式读音地址
     */
    private String ukReadUrl;

    /**
     * 英式音标
     */
    private String ukPhonetic;

    /**
     * 释义
     */
    private List<String> translates;

    /**
     * 是否可以加入生词本（系统中没有的单词不能加入生词本） <br>
     * true:可以加入<br>
     * false:不能加入
     */
    private Boolean canAddNewWordsBook;
}

package com.zhidejiaoyu.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnitSentence implements Serializable{
    private Long unitId;

    private Long sentenceId;

    /**
     * 例句翻译
     */
    private String chinese;

    /**
     * 例句英文干扰项
     */
    private String englishDisturb;

    /**
     * 例句中文干扰项
     */
    private String chineseDisturb;

}
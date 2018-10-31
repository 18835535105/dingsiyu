package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class UnitSentence implements Serializable{
    private Long unitId;

    private Long sentenceId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Long sentenceId) {
        this.sentenceId = sentenceId;
    }
}
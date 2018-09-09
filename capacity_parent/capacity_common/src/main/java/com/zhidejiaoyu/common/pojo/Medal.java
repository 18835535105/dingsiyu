package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class Medal implements Serializable {
    private Long id;

    private String parentName;

    private Long nextParent;

    private String childName;

    private Long nextChild;

    /**
     * 任务奖励中勋章进度提示语
     */
    private String markedWords;

    /**
     * 勋章说明
     */
    private String explain;

    private String childImgUrl;

    private String parentImgUrl;

    public String getMarkedWords() {
        return markedWords;
    }

    public void setMarkedWords(String markedWords) {
        this.markedWords = markedWords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName == null ? null : parentName.trim();
    }

    public Long getNextParent() {
        return nextParent;
    }

    public void setNextParent(Long nextParent) {
        this.nextParent = nextParent;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName == null ? null : childName.trim();
    }

    public Long getNextChild() {
        return nextChild;
    }

    public void setNextChild(Long nextChild) {
        this.nextChild = nextChild;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain == null ? null : explain.trim();
    }

    public String getChildImgUrl() {
        return childImgUrl;
    }

    public void setChildImgUrl(String childImgUrl) {
        this.childImgUrl = childImgUrl == null ? null : childImgUrl.trim();
    }

    public String getParentImgUrl() {
        return parentImgUrl;
    }

    public void setParentImgUrl(String parentImgUrl) {
        this.parentImgUrl = parentImgUrl == null ? null : parentImgUrl.trim();
    }
}
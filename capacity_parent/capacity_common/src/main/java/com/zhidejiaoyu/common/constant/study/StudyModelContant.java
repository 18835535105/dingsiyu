package com.zhidejiaoyu.common.constant.study;

/**
 * 学习/测试模块
 *
 * @author wuchenxi
 * @date 2019-05-21
 */
@SuppressWarnings("all")
public enum StudyModelContant {

    /**
     * 音标测试学习模块
     */
    PHONETIC_SYMBOL_TEST("音标测试");

    StudyModelContant(String model) {
        this.model = model;
    }
    private String model;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

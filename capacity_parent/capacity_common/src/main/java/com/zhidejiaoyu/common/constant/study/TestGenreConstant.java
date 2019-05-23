package com.zhidejiaoyu.common.constant.study;

/**
 * 测试类型常量类
 *
 * @author wuchenxi
 * @date 2019-05-21
 */
@SuppressWarnings("all")
public enum  TestGenreConstant {

    UNIT_TEST("单元闯关测试");

    private String genre;

    TestGenreConstant(String genre) {
        this.genre = genre;
    }
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

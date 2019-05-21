package com.zhidejiaoyu.common.constant.study;

/**
 * 分数常量类
 *
 * @author wuchenxi
 * @date 2019-05-21
 */
@SuppressWarnings("all")
public enum PointConstant {

    FIFTY(50),
    SIXTY(60),
    SEVENTY(70),
    EIGHTY(80),
    NINETY(90),
    HUNDRED(100);

    PointConstant(Integer point) {
        this.point = point;
    }

    private Integer point;

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
}

package com.zhidejiaoyu.common.utils;

/**
 * 计算慧追踪中单词字体及样式相关
 *
 * @author wuchenxi
 * @date 2018/6/28 18:31
 */
public class CapacityFontUtil {

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 字体颜色
     */
    private String fontColor;

    /**
     * 字重
     */
    private String fontWeight;

    public Integer getFontSize() {
        return fontSize;
    }

    /**
     * 初始化类中信息
     *
     * @param fontNum 字体标识
     */
    public CapacityFontUtil(Integer fontNum) {
        this.setFontSize(fontNum);
    }

    private void setFontSize(Integer fontSize) {
        switch (fontSize) {
            case 0:
                this.fontSize = 14;
                this.fontColor = "#aaaaaa";
                this.fontWeight="normal";
                break;
            case 1:
                this.fontSize = 18;
                this.fontColor = "#9d9d9d";
                this.fontWeight="normal";
                break;
            case 2:
                this.fontSize = 22;
                this.fontColor = "#999999";
                this.fontWeight="normal";
                break;
            case 3:
                this.fontSize = 26;
                this.fontColor = "#888888";
                this.fontWeight="normal";
                break;
            case 4:
                this.fontSize = 30;
                this.fontColor = "#777777";
                this.fontWeight="normal";
                break;
            case 5:
                this.fontSize = 34;
                this.fontColor = "#666666";
                this.fontWeight="normal";
                break;
            case 6:
                this.fontSize = 38;
                this.fontColor = "#777777";
                this.fontWeight="bold";
                break;
            case 7:
                this.fontSize = 42;
                this.fontColor = "#666666";
                this.fontWeight="bold";
                break;
            case 8:
                this.fontSize = 46;
                this.fontColor = "#555555";
                this.fontWeight="bold";
                break;
            case 9:
                this.fontSize = 50;
                this.fontColor = "#333333";
                this.fontWeight="bold";
                break;
            case 10:
                this.fontSize = 54;
                this.fontColor = "#111111";
                this.fontWeight="bold";
                break;
                default:
        }
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }
}

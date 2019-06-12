package com.zhidejiaoyu.student.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记忆追踪（慧追踪）摘要页面内容——只有单词或例句显示的页面，通过字体大小来确定复习紧迫程度的页面
 *
 * @author wuchenxi
 * @date 2018年5月18日 上午9:21:27
 */
@Data
public class CapacityDigestVo implements Serializable {
    /**
     * 本课程生词/生句个数
     */
    private Integer strangenessCount;

    /**
     * 学要复习的生词/生句的个数
     */
    private Integer needReview;

    /**
     * 进入慧追踪时是否显示提示框。
     * 1：显示提示框；
     * 2：不显示提示框
     */
    private boolean showCapacity;

    private List<WordInfo> wordInfos = new ArrayList<>();

    @Data
    public static class WordInfo {
        /**
         * 单词/例句id
         */
        private Long id;

        /**
         * 单元id
         */
        private Long unitId;

        /**
         * 单词/例句英文
         */
        private String content;

        /**
         * 字体大小
         */
        private Integer fontSize;

        /**
         * 用于控制显示/隐藏当前单词详细信息标识 true:显示单词详细信息；false：隐藏单词详细信息
         */
        private Boolean showInfo;

        /**
         * 字体颜色
         */
        private String fontColor;

        /**
         * 字重
         */
        private String fontWeight;

        /**
         * 读音地址
         */
        private String readUrl;
    }

}

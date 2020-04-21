package com.zhidejiaoyu.common.vo.goldtestvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 金币试卷响应的数据
 *
 * @author: wuchenxi
 * @date: 2020/4/17 10:19:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldTestSubjectsVO implements Serializable {

    /**
     * 类型
     */
    private String type;

    /**
     * 阅读理解、完形填空等文章内容，每段是数组一项
     */
    private String[] content;

    private List<Subjects> subjects;

    /**
     * 试题内容
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subjects {

        /**
         * 题目id
         */
        private Long id;

        /**
         * 题目
         */
        private String[] title;

        /**
         * 选项列表
         */
        private List<String> selects;

        /**
         * 答案
         */
        private String[] answer;

        /**
         * 解析
         */
        private String[] analysis;
    }

}

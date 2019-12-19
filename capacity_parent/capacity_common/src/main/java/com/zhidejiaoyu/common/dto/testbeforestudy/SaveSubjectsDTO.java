package com.zhidejiaoyu.common.dto.testbeforestudy;

import lombok.Data;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2019/12/19 15:01:01
 */
@Data
public class SaveSubjectsDTO {

    /**
     * 测试结果
     */
    private List<Result> resultList;

    /**
     * 分数
     */
    private Integer point;

    @Data
    public static
    class Result {
        /**
         * 单元id
         */
        private Long unitId;

        /**
         * 是否答对
         */
        private Boolean right;
    }
}

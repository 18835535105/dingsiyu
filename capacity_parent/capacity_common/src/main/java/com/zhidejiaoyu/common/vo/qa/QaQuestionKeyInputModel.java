package com.zhidejiaoyu.common.vo.qa;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关键词问题对应表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QaQuestionKeyInputModel extends BaseRowModel {

    /**
     * 问题序号
     */
    @ExcelProperty(index = 0)
    private String questionId;

    /**
     * 关键词
     */
    @ExcelProperty(index = 1)
    private String key;

    /**
     * 关键词序号
     */
    @ExcelProperty(index = 2)
    private String keyId;


}

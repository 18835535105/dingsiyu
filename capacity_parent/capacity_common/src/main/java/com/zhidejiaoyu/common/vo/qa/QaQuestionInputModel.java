package com.zhidejiaoyu.common.vo.qa;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 李糖心Qa问题导入表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QaQuestionInputModel extends BaseRowModel {

    /**
     * 序号
     */
    @ExcelProperty(index = 0)
    private String number;

    /**
     * 问题名称
     */
    @ExcelProperty(index = 1)
    private String problem;

    /**
     * 答案
     */
    @ExcelProperty(index = 2)
    private String answer;

    /**
     * 录音
     */
    @ExcelProperty(index = 3)
    private String sound;


}

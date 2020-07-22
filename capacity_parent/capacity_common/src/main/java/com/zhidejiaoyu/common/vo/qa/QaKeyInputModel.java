package com.zhidejiaoyu.common.vo.qa;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关键词表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QaKeyInputModel extends BaseRowModel {
    /**
     * 序号
     */
    @ExcelProperty(index = 0)
    private String id;

    /**
     * 关键词
     */
    @ExcelProperty(index = 1)
    private String key;


}

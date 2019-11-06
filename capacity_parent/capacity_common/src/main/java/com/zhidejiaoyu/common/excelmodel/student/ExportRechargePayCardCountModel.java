package com.zhidejiaoyu.common.excelmodel.student;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.zhidejiaoyu.common.annotation.excel.CellStyleFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出充课学生数量 model
 *
 * @author liumaoyu
 * @date 2019-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportRechargePayCardCountModel extends BaseRowModel {
    @CellStyleFormat()
    @ExcelProperty(value = "学校", index = 0)
    private String school;

    @CellStyleFormat()
    @ExcelProperty(value = "充课学生数量", index = 1)
    private String count;

    @CellStyleFormat()
    @ExcelProperty(value = "查询开始日期", index = 2)
    private String startTime;

    @CellStyleFormat()
    @ExcelProperty(value = "查询结束日期", index = 3)
    private String endTime;
}

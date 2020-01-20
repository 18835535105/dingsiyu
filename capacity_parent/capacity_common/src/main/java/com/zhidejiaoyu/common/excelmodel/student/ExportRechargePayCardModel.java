package com.zhidejiaoyu.common.excelmodel.student;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.zhidejiaoyu.common.annotation.excel.CellStyleFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 导出充课卡学生信息 model
 *
 * @author liumaoyu
 * @date 2019-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportRechargePayCardModel {
    @CellStyleFormat()
    @ExcelProperty(value = "学校", index = 0)
    @ColumnWidth(40)
    private String school;

    @CellStyleFormat()
    @ExcelProperty(value = "学生账号", index = 1)
    private String studentAccount;

    @CellStyleFormat()
    @ExcelProperty(value = "学生姓名", index = 2)
    private String studentName;

    @CellStyleFormat()
    @ExcelProperty(value = "充课详情", index = 3)
    private String lessonDetails;

    @CellStyleFormat()
    @ExcelProperty(value = "充课时间", index = 4)
    private String createTime;

    @CellStyleFormat()
    @ExcelProperty(value = "最后来校时间", index = 5)
    private String loginTime;
}

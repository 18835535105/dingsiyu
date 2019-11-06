package com.zhidejiaoyu.common.excelmodel.student;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zhidejiaoyu.common.annotation.excel.CellStyleFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出各个校区昨天学生登录情况汇总
 *
 * @author wuchenxi
 * @date 2019-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportStudentOnlineTimeWithSchoolSummary {

    @CellStyleFormat()
    @ExcelProperty(value = "学校名称", index = 0)
    private String schoolName;

    @CellStyleFormat()
    @ExcelProperty(value = "登录学生数", index = 1)
    private Long studentCount;

    @CellStyleFormat()
    @ExcelProperty(value = "统计日期", index = 2)
    private String time;
}

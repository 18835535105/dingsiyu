package com.zhidejiaoyu.common.excelmodel.student;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zhidejiaoyu.common.annotation.excel.CellStyleFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出各个校区昨天学生登录情况及各个学生在线时长信息明细
 *
 * @author wuchenxi
 * @date 2019-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExportStudentOnlineTimeWithSchoolDetail {

    @CellStyleFormat()
    @ExcelProperty(value = "学校名称", index = 0)
    private String schoolName;

    @CellStyleFormat()
    @ExcelProperty(value = "学生账号", index = 1)
    private String account;

    @CellStyleFormat()
    @ExcelProperty(value = "学生姓名", index = 2)
    private String studentName;

    @CellStyleFormat()
    @ExcelProperty(value = "在线时长", index = 3)
    private Long onlineTime;

    @CellStyleFormat()
    @ExcelProperty(value = "统计日期", index = 4)
    private String time;
}

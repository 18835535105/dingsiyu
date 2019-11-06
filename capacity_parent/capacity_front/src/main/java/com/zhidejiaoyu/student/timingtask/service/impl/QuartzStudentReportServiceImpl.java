package com.zhidejiaoyu.student.timingtask.service.impl;

import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelWriterFactory;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.timingtask.service.QuartzStudentReportService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @Date: 2019/11/5 10:54
 */
@Service
public class QuartzStudentReportServiceImpl implements QuartzStudentReportService {

    @Override
    public ServerResponse statisticsStudentWithSchoolInfo() {
        // 昨天的日期
        String yesterday = DateUtil.formatYYYYMMDD(DateTime.now().minusDays(1).toDate());
        String fileName = "校区学生每日信息" + System.currentTimeMillis();


        // 汇总sheet页
//        ExcelWriterFactory excelWriterFactory = exportStudentOnlineTimeWithSchoolSummary(yesterday, fileName);
//
//        // 详情sheet页
//        if (exportStudentWithSchoolDetail(yesterday, excelWriterFactory)) {
//            return new ErrorTip(BizExceptionEnum.NO_DATA);
//        }
        return null;
    }
}

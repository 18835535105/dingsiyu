package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GrowReportService;
import com.zhidejiaoyu.student.vo.reportvo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 成长报告相关
 *
 * @author wuchenxi
 * @date 2018/7/19
 */
@RestController
@RequestMapping("/report")
public class GrowReportController {

    @Autowired
    private GrowReportService growReportService;

    /**
     * 获取学习成果数据
     *
     * @param session
     * @return
     */
    @GetMapping("/getLeanResult")
    public ServerResponse<ReportVO> getLearnResult(HttpSession session) {
        return growReportService.getLearnResult(session);
    }


}

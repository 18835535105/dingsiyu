package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.reportvo.ReportVO;

import javax.servlet.http.HttpSession;

/**
 * 成长报告相关service
 *
 * @author wuchenxi
 * @date 2018/7/19
 */
public interface GrowReportService {

    /**
     * 获取学习成果数据
     *
     * @param session
     * @return
     */
    ServerResponse<ReportVO> getLearnResult(HttpSession session);
}

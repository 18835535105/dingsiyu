package com.zhidejiaoyu.student.vo.reportvo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 成长报告VO
 *
 * @author wuchenxi
 * @date 2018/7/20
 */
@Data
public class ReportVO implements Serializable {
    private List<LearnResultVO> learnResult;
    private LearnSuperviseVO learnSupervise;
}

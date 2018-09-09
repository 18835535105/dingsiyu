package com.zhidejiaoyu.student.vo.reportvo;

import java.util.List;

/**
 * 成长报告VO
 *
 * @author wuchenxi
 * @date 2018/7/20
 */
public class ReportVO {
    private List<LearnResultVO> learnResult;
    private LearnSuperviseVO learnSupervise;

    public List<LearnResultVO> getLearnResult() {
        return learnResult;
    }

    public void setLearnResult(List<LearnResultVO> learnResult) {
        this.learnResult = learnResult;
    }

    public LearnSuperviseVO getLearnSupervise() {
        return learnSupervise;
    }

    public void setLearnSupervise(LearnSuperviseVO learnSupervise) {
        this.learnSupervise = learnSupervise;
    }

    @Override
    public String toString() {
        return "ReportVO{" +
                "learnResult=" + learnResult +
                ", learnSupervise=" + learnSupervise +
                '}';
    }
}

package com.zhidejiaoyu.student.vo;

/**
 * 课程进度弹框显示内容
 *
 * @author wuchenxi
 * @date 2018/5/22 14:55
 */
public class CoursePlanVo {

    /**
     * 模块名
     */
    private String studyModel;

    /**
     * 进度
     */
    private Double plan;

    /**
     * 已学量
     */
    private Integer learned;

    /**
     * 总量
     */
    private Integer total;

    /**
     * 待复习量（达到黄金记忆点的量）
     */
    private Integer needReview;

    public String getStudyModel() {
        return studyModel;
    }

    public void setStudyModel(String studyModel) {
        this.studyModel = studyModel;
    }

    public Double getPlan() {
        return plan;
    }

    public void setPlan(Double plan) {
        this.plan = plan;
    }

    public Integer getLearned() {
        return learned;
    }

    public void setLearned(Integer learned) {
        this.learned = learned;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNeedReview() {
        return needReview;
    }

    public void setNeedReview(Integer needReview) {
        this.needReview = needReview;
    }
}

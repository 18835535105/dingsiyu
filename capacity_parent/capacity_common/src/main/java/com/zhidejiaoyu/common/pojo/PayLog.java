package com.zhidejiaoyu.common.pojo;

import java.util.Date;

public class PayLog {
    private Long id;

    private Long studentId;

    private Date recharge;

    private String cardNo;

    private Integer cardDate;

    private Date foundDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Date getRecharge() {
        return recharge;
    }

    public void setRecharge(Date recharge) {
        this.recharge = recharge;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? null : cardNo.trim();
    }

    public Integer getCardDate() {
        return cardDate;
    }

    public void setCardDate(Integer cardDate) {
        this.cardDate = cardDate;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }
}
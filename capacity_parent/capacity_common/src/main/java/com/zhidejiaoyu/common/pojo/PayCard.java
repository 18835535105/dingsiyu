package com.zhidejiaoyu.common.pojo;

import java.util.Date;

public class PayCard {
    private Long id;

    private String orderNo;

    private String cardNo;

    private Integer validityTime;

    private Date createTime;

    private Date useTime;

    private String useUser;

    private String createUser;

    private String goodsUser;

    private Integer cardNum;

    private Integer canUse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? null : cardNo.trim();
    }

    public Integer getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(Integer validityTime) {
        this.validityTime = validityTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getUseUser() {
        return useUser;
    }

    public void setUseUser(String useUser) {
        this.useUser = useUser == null ? null : useUser.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getGoodsUser() {
        return goodsUser;
    }

    public void setGoodsUser(String goodsUser) {
        this.goodsUser = goodsUser == null ? null : goodsUser.trim();
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Integer getCanUse() {
        return canUse;
    }

    public void setCanUse(Integer canUse) {
        this.canUse = canUse;
    }
}
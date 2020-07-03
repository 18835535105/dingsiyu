package com.zhidejiaoyu.common.vo;

import lombok.Data;

@Data
public class WebSocketMessage {

    /**
     * 返回的路由路径
     */
    private String headUrl;

    /**
     * 返回需要查询的数据id
     */
    private String toSendId;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 查看数量
     */
    private String count;

    /**
     * 数据单元
     */
    private String model;
    /**
     * 头部信息
     */
    private String title;




}

package com.zhidejiaoyu.common.dto.wechat.qy.fly;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 上传学生飞行记录
 *
 * @author: wuchenxi
 * @date: 2020/7/1 14:44:44
 */
@Data
public class UploadFlyRecordDTO {

    /**
     * 学生openId
     */
    @NotBlank(message = "openId can't be null!")
    private String openId;

    @NotNull(message = "二维码序号不能为空！")
    private Integer num;

    @NotNull(message = "studentId can't be null!")
    private Long studentId;

    /**
     * 老师对学生的评价
     */
    private String evaluate;

    /**
     * 学生表现（1-5颗星）
     */
    private Integer show;

    /**
     * 备注
     */
    private String comment;

    /**
     * 座位号（在家填写0）
     */
    private Integer siteNo;
}

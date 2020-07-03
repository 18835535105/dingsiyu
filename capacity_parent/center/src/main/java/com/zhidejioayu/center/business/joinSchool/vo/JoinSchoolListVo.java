package com.zhidejioayu.center.business.joinSchool.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class JoinSchoolListVo implements Serializable {
    //编号
    private String id;
    //学校名称
    private String  schoolName;
    //校长名称
    private String pressidentName;
    //手机号
    private String phone;
    //地址
    private String address;
    //状态
    private Integer auditStatus;
    //状态文
    private String auditStatusToString;
    //审核状态
    private Integer reporting;



}

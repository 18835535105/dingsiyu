package com.zhidejiaoyu.common.dto.joinSchool;


import lombok.Data;

import java.util.Date;

@Data
public class JoinSchoolDto {
    //账号
    private String id;
    //学校名称
    private String schoolName;
    //学校状态
    private Integer auditStatus;
    //审核时间
    private Date dateOfaudit;
    //管理员id
    private Integer userId;
    private String uuid;
    //学校加盟数量
    private Integer joiningNumber;
    /**
     * 审核状态
     */
    private Integer reporting;
    /**
     * 账号
     */
    private String account;
    /**
     * 签约号
     */
    private Integer reservationNumber;
    //校长姓名
    private String pessidentName;
    private Integer pageNum;
    private Integer pageSize;
}

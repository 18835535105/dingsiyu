package com.zhidejiaoyu.common.vo.studentExchangPrize;

import lombok.Data;

import java.util.Date;

@Data
public class StudentExchangePrizeVo {
    private Long id;
    private String prize;
    private String account;
    private String studentName;
    private String createTime;
    private Integer state;
    private Date createTimes;
    private Integer index;

}

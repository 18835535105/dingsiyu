package com.zhidejiaoyu.common.vo.prizeExchangeList;

import lombok.Data;

@Data
public class PrizeExchangeListVo {
    private Long id;
    private String prize;
    private Integer exchangePrize;
    private Integer totalNumber;
    private Integer surplusNumber;
    private String partUrl;
    private String createTime;
    private String state;
    private String schoolName;
    private Long teacherId;
    private Integer index;
    private boolean checked;
}

package com.zhidejiaoyu.common.vo.simple.ccieVo;

import lombok.Data;

/**
 * @author wuchenxi
 * @date 2018/9/12
 */
@Data
public class CcieVo {

    private String testName;

    private String student_name;

    private Long student_id;

    private String ccieName;

    private Long id;

    private String ccie_no;

    private String content;

    private String year;

    private String month;

    private String day;

    private String modelAndTestType;

    private String petName;

    private Integer point;

    /**
     * 当前证书是否已查看过
     * true：已查看
     * false：未查看
     */
    private Boolean readFlag;
}

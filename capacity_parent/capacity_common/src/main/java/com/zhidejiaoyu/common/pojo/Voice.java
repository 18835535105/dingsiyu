package com.zhidejiaoyu.common.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Voice {
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    private Long wordId;

    private Integer type;

    private Double score;

    private String voiceUrl;

    private Date createTime;

    private String studentName;
}
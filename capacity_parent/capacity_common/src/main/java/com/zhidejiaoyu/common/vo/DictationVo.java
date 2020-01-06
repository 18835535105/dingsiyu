package com.zhidejiaoyu.common.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class DictationVo implements Serializable {

    private Boolean firstStudy;
    private Long id;
    private double memoryStrength;
    private int plan;
    private String readUrl;
    private String soundmark;
    private Boolean studyNew;
    private String word;
    private String wordChinese;
    private int wordCount;
    private String wordyj;


}

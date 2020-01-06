package com.zhidejiaoyu.common.vo;

import lombok.Data;

import java.util.Map;

@Data
public class WordPictorialVo {


    private String word_chinese;
    private int memoryDifficulty;
    private String smallPictureUrl;
    private int wordCount;
    private String recordpicurl;
    private String middlePictureUrl;
    private Map<String, Boolean> subject;
    private int type;
    private boolean studyNew;
    private String sound_mark;
    private Integer engine;
    private Boolean firstStudy;
    private Long id;
    private String memoryStrength;
    private String readUrl;
    private String word;


}

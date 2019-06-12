package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 点击例句中某个单词，返回该单词的相关信息
 *
 * @author wuchenxi
 * @date 2018/6/28 17:02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SentenceWordInfoVo implements Serializable {

    private Long wordId;

    /**
     * 单词所属单元id
     */
    private Long unitId;

    /**
     * 单词所属课程
     */
    private Long courseId;

    /**
     * 单词英文
     */
    private String word;

    /**
     * 单词释义
     */
    private String wordChinese;

    /**
     * 单词操作状态
     * 1：说明该单词不在当前学生应学单词中或者该单词学生还没有学习，无操作按钮；
     * 2：该单词是熟词，可以加入生词本
     * 3：该单词已经在生词本中
     */
    private Integer state;

}

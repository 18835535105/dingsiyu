package com.zhidejiaoyu.common.dto.syntax;

import com.zhidejiaoyu.common.pojo.Student;
import lombok.Data;

/**
 * 保存语法记录dto
 *
 * @author: wuchenxi
 * @date: 2020/1/14 10:16:16
 */
@Data
public class SaveSyntaxDTO {

    private Long courseId;

    private Long unitId;

    /**
     * 是否答对true答对false答错
     */
    private Boolean known;

    /**
     * 语法id
     */
    private Long vocabularyId;

    /**
     * 非请求参数
     */
    private Student student;

    /**
     * 非请求参数,语法模块名
     */
    private String studyModel;

    /**
     * 非请求参数，1：简单；2：复杂
     */
    private Integer easyOrHard;

    /**
     * 非请求参数,studyCapacity中type值
     */
    private Integer type;

}

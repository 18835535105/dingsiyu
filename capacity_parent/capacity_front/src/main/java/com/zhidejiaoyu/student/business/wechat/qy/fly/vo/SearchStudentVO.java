package com.zhidejiaoyu.student.business.wechat.qy.fly.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 15:14:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchStudentVO implements Serializable {

    private String studentName;

    private Long studentId;

    private String uuid;

    /**
     * 判断教师是否可以提交该学生记录
     */
    private Boolean canSubmit;

}

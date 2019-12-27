package com.zhidejiaoyu.student.business.index.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页面课程信息
 *
 * @author: wuchenxi
 * @date: 2019/12/27 09:50:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfoVO {

    /**
     * 所在年级
     */
    private String InGrade;

    /**
     * 过往年级课程信息
     */
    private List<CourseVO> previousGrade;

    /**
     * 当前年级课程信息
     */
    private List<CourseVO> currentGrade;
}

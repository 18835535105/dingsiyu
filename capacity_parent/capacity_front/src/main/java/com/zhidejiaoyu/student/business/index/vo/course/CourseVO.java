package com.zhidejiaoyu.student.business.index.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 课程详情
 *
 * @author: wuchenxi
 * @date: 2019/12/27 10:22:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseVO implements Serializable {

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 英文上下册  one-up
     */
    private String englishGrade;

    /**
     * 中文上下册 三年级（上册）
     */
    private String grade;

    /**
     * 战斗进度
     */
    private Integer combatProgress;

    /**
     * 1，未战斗 2，正在战斗 3，已完成
     */
    private Integer battle;
}

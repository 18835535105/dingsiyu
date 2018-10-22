package com.zhidejiaoyu.common.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进度排行Vo
 */
@Data
@NoArgsConstructor
public class SeniorityVo {

    /** 学生id*/
    private Long student_id;

    /** 排行数据, 用于排序 */
    private Integer SeniorityData;

    /** 姓名 */
    private String student_name;
    
    /** 头像 */
    private String headUrl;

    /** 班级 */
    private String squad;

    /** 学校 */
    private String school_name;

    /** 已学单元 */
    private Integer countUnit;

    /** 已做测试 */
    private Integer countTest;

    /** 学习时长 */
    private Integer learnDate;

}

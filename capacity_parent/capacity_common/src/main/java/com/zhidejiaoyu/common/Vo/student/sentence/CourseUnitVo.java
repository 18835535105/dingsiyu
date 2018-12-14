package com.zhidejiaoyu.common.Vo.student.sentence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 进入例句/课文学习页时展示学生所有课程和单元及其学习状态
 *
 * @author wuchenxi
 * @date 2018-12-12
 */
@Data
public class CourseUnitVo implements Serializable {

    private String courseName;
    private Long courseId;

    /**
     * key:unitName 单元名
     * key:unitId   单元id
     * key:state 单元学习状态：1：未学习,不可点击；3：当前正在学习，4：已学习，可以重新学习
     */
    private List<Map<String, Object>> unitVos;
}

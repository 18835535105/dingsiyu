package com.zhidejiaoyu.student.dto.sentence;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 句型单元闯关dto
 *
 * @author wuchenxi
 * @date 2018-12-15
 */
@Data
public class SentenceUnitTestDto implements Serializable {
    /**
     * 单元id
     */
    private Long unitId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 得分
     */
    private Integer point;

    /**
     * 错题数量
     */
    private Integer errorCount;

    /**
     * 正确题数量
     */
    private Integer rightCount;
}

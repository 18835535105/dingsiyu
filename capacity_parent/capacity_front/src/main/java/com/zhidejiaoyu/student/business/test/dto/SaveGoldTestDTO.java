package com.zhidejiaoyu.student.business.test.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 保存金币试卷数据
 *
 * @author: wuchenxi
 * @date: 2020/4/17 09:22:22
 */
@Data
public class SaveGoldTestDTO {
    /**
     * 单元id
     */
    @NotNull(message = "unitId 不能为空")
    private Long unitId;

    /**
     * 课程id
     */
    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    /**
     * 得分
     */
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数最小值为0")
    @Max(value = 100, message = "分数最大值为100")
    private Integer point;

    /**
     * 错题数量(用于例句单元闯关测试)
     */
    private Integer errorCount;

    /**
     * 正确题数量(用于例句单元闯关测试)
     */
    private Integer rightCount;

}

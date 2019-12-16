package com.zhidejiaoyu.common.dto.phonetic;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 音标单元闯关测试 dto
 *
 * @author wuchenxi
 * @date 2019-05-21
 */
@Data
public class UnitTestDto {
    /**
     * 单元id
     */
    @NotNull(message = "unitId 不能为空")
    private Long unitId;

    /**
     * 得分
     */
    @NotNull(message = "分数不能为空")
    @Min(value = 0, message = "分数最小值为0")
    @Max(value = 100, message = "分数最大值为100")
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

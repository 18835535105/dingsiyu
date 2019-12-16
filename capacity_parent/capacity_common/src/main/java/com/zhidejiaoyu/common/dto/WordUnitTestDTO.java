package com.zhidejiaoyu.common.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用于接收单元测试交卷之后的参数
 *
 * @author wuchenxi
 * @date 2018年5月16日 上午10:53:26
 */
@Data
public class WordUnitTestDTO {
    /**
     * 单元id
     */
    @NotNull(message = "unitId 不能为空")
    private Long[] unitId;

    /**
     * 课程id
     */
    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    /**
     * 正确单词数组
     */
    private String[] correctWord;

    /**
     * 错误单词数组
     */
    private String[] errorWord;

    /**
     * 正确单词id
     */
    private Long[] correctWordId;

    /**
     * 错误单词id
     */
    private Long[] errorWordId;

    /**
     * 类型 0=单词图鉴 1=慧记忆 2=听写 3=默写 4=例句听力 5=例句翻译 6=例句默写 8=例句测试 9=课文测试 11=音标测试
     */
    @NotNull(message = "测试类型不能为空")
    private Integer classify;

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

package com.zhidejiaoyu.common.dto.read;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 保存强化数据
 *
 * @author wuchenxi
 * @date 2019-07-26
 */
@Data
public class SaveStrengthenDto {
    @NotNull(message = "courseId 不能为 null！")
    private Long courseId;

    /**
     * 强化类型：1.慧记忆;2.单词图鉴3.慧听写4.慧默写
     */
    @NotNull(message = "type 不能为 null！")
    @Range(min = 1, max = 4, message = "type 类型错误！")
    private Integer type;

    @NotNull(message = "wordId 不能为 null！")
    private Long wordId;

    /**
     * 认识不认识
     * <br>
     * true:认识 <br>
     * false:不认识
     */
    private Boolean isKnown;
}

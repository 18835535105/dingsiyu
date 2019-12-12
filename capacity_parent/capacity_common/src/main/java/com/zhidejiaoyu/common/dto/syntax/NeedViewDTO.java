package com.zhidejiaoyu.common.dto.syntax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 语法学习页获取记忆追踪数据时需要传递的参数
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 15:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeedViewDTO {
    private Long studentId;

    private Long unitId;

    /**
     * 进度
     */
    private Integer plan;

    /**
     * 总进度
     */
    private Integer total;

    /**
     * 记忆追踪的type
     */
    private Integer type;
}


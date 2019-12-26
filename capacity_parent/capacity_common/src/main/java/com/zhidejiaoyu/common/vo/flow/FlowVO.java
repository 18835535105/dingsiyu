package com.zhidejiaoyu.common.vo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wuchenxi
 * @date: 2019/12/26 15:13:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowVO implements Serializable {
    private Long courseId;

    private Long unitId;

    private String courseName;

    private String unitName;

    /**
     * 当前节点名称
     */
    private String modelName;

    /**
     * 流程id
     */
    private Long id;
}

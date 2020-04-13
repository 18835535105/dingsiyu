package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 其他学生的飞船配置数据
 *
 * @author: wuchenxi
 * @date: 2020/4/13 09:40:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherStudentShipIndexVO implements Serializable {
    /**
     * 首页数据
     */
    private IndexVO index;

    /**
     * 排行数据
     */
    private OtherStudentRankVO rank;

    /**
     * pk胜率
     */
    private String successRate;
}

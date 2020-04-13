package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 查看其他学生的排行信息
 *
 * @author: wuchenxi
 * @date: 2020/4/13 09:35:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherStudentRankVO implements Serializable {
    /**
     * 校区排行
     */
    private Long schoolRank;

    /**
     * 全国排行
     */
    private Long countryRank;
}

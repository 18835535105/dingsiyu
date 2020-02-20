package com.zhidejiaoyu.student.business.smallapp.vo.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 签到表数据展示
 *
 * @author: wuchenxi
 * @date: 2020/2/17 11:09:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardVO implements Serializable {

    /**
     * 连续打卡天数
     */
    private Integer cardDays;

    private Infos infos;

    /**
     * 签到信息
     */
    @Data
    static class Infos {
    }
}

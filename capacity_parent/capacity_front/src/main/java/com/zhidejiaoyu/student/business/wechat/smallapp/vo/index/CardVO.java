package com.zhidejiaoyu.student.business.wechat.smallapp.vo.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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

    /**
     * 签到信息
     */
    private List<String> infos;

    /**
     * 能否打卡
     * <ul>
     * <li>true：可以打卡</li>
     * <li>false：不能打卡</li>
     * </ul>
     */
    private Boolean canCard;

    /**
     * 不能打卡原因
     */
    private String msg;
}

package com.zhidejiaoyu.common.vo.ship;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生的勋章状态
 *
 * @author wuchenxi
 * @date 2020-08-05 10:27:50
 */
@Data
public class MedalStatusVO implements Serializable {

    /**
     * 勋章id
     */
    private Long medalId;

    private String imgUrl;

    /**
     * 状态
     * <ul>
     *     <li>1:已获取，显示”展示“</li>
     *     <li>2：未获取</li>
     *     <li>3：已展示</li>
     * </ul>
     */
    private Integer state;

    /**
     * 说明文字
     */
    private String msg;
}

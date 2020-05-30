package com.zhidejiaoyu.common.vo.ship;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 可以挑战的副本信息
 *
 * @author: wuchenxi
 * @date: 2020/5/21 10:19:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkBaseInfoVO implements Serializable {

    /**
     * 单人副本
     */
    private List<CopyInfoVO> personCopyInfo;

    /**
     * 校区副本
     */
    private CopyInfoVO schoolPkCopyInfo;

    /**
     * 副本可挑战信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CopyInfoVO {
        Long id;

        String imgUrl;

        String name;

        /**
         * <ul>
         *     <li>单人副本：当天可挑战总次数</li>
         *     <li>校区副本：本周可挑战总次数</li>
         * </ul>
         */
        Integer totalCount;

        /**
         * <ul>
         *     <li>单人副本：当天剩余挑战总次数</li>
         *     <li>校区副本：本周剩余挑战总次数</li>
         * </ul>
         */
        Integer surplusCount;

        /**
         * 校区副本boss总耐久度
         */
        private Integer totalDurability;

        /**
         * 校区副本boss剩余耐久度
         */
        private Integer surplusDurability;

        /**
         * 校区boss消失倒计时
         */
        private Long countDown;
    }


}

package com.zhidejiaoyu.student.business.shipconfig.vo;

import com.zhidejiaoyu.common.utils.ship.EquipmentVo;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * pk、副本试题数据
 *
 * @author: wuchenxi
 * @date: 2020/3/19 10:38:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkInfoVO implements Serializable {

    /**
     * 被挑战者
     */
    private Challenged challenged;

    /**
     * 挑战者
     */
    private Challenged originator;

    /**
     * 挑战者
     */
    private BossPoll boss;

    /**
     * 试题
     */
    private List<SubjectsVO> subject;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BossPoll {


        /**
         * 战斗数据
         */
        private IndexVO.BaseValue battle;

        /**
         * 图片名
         */
        private String imgUrl;
        /**
         * 装备名
         */
        private String name;
        /**
         * 装备等级
         */
        private Integer degree;
        /**
         * 飞船型号
         */
        private String grade;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Challenged {
        /**
         * 飞船数据
         */
        private EquipmentVo shipEquipment;

        /**
         * 战斗数据
         */
        private IndexVO.BaseValue battle;

        /**
         * 装甲数据
         */
        private EquipmentVo armorEquipment;

        /**
         * 导弹数据
         */
        private EquipmentVo missileEquipment;
        /**
         * 武器数据
         */
        private EquipmentVo armsEquipment;

    }
}

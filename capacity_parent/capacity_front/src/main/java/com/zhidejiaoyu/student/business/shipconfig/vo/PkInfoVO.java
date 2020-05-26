package com.zhidejiaoyu.student.business.shipconfig.vo;

import com.zhidejiaoyu.common.vo.ship.EquipmentVo;
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
     * boss挑战
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
         * 飞船数据
         */
        private EquipmentVo shipEquipment;
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
        /**
         * 战斗数据
         */
        private IndexVO.BaseValue battle;

        /**
         * 学生头像
         */
        private String hardImg;
        /**
         * 学生昵称
         */
        private String nickName;

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
        /**
         * 学生头像
         */
        private String hardImg;
        /**
         * 学生昵称
         */
        private String nickName;

    }
}

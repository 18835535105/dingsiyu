package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 装备经验保存vo
 */
@Data
public class EquipmentExperienceVo implements Serializable {

    /**
     * 飞船经验
     */
    private int shipExperience;
    /**
     * 导弹经验
     */
    private int missileExperience;
    /**
     * 武器经验
     */
    private int weaponExperience;
    /**
     * 装甲经验
     */
    private int armorExperience;

}

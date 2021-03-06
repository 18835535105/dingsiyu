package com.zhidejiaoyu.common.vo.ship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学生飞船、装备……各个属性信息
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentVo implements Serializable {

    /**
     * 图片名
     */
    private String imgUrl;
    /**
     * 图片名
     */
    private String leftImgUrl;
    /**
     * 装备名
     */
    private String equipmentName;
    /**
     * 装备等级
     */
    private Integer degree;
    /**
     * 飞船型号
     */
    private String grade;
    /**
     * 装备id
     */
    private Integer id;

}

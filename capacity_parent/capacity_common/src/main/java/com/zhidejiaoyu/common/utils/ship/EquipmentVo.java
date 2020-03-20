package com.zhidejiaoyu.common.utils.ship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

package com.zhidejiaoyu.student.business.shipconfig.vo;

import com.zhidejiaoyu.common.pojo.Equipment;
import com.zhidejiaoyu.common.pojo.EquipmentExpansion;
import lombok.Data;

import java.util.Map;

@Data
public class EquipmentEquipmentExperienceVo extends Equipment {

    private Map<Integer, EquipmentExpansion> experienceMap;


}

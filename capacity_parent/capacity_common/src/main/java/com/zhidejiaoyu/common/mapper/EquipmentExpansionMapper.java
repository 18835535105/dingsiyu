package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.EquipmentExpansion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-27
 */
public interface EquipmentExpansionMapper extends BaseMapper<EquipmentExpansion> {

    /**
     * 获取所有装备lv1的图片
     *
     * @return
     */
    @MapKey("equipmentId")
    Map<Long, Map<String, Object>> selectLvOneAllUrl();

    List<Map<String,Object>> selectAllUrlByType(@Param("type")Integer type);

    String selectUrlByEquipmentIdAndType(@Param("equipmentId") Long equipmentId,@Param("degree") Integer intensificationDegree);

    Map<String, Object> selectByEquipmentIdAndLevel(@Param("equipmentId") Long equipmentId,@Param("degree") int level);

    List<EquipmentExpansion> selectAll();
}

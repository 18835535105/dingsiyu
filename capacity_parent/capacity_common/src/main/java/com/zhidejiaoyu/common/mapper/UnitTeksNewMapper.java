package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitTeksNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface UnitTeksNewMapper extends BaseMapper<UnitTeksNew> {

    /**
     * 查询当前单元下个group
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("select `group` from unit_teks_new where unit_id = #{unitId} and `group` > #{group} limit 1")
    Integer selectNextGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询课文模块当前单元group的个数
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("SELECT COUNT(1) FROM unit_teks_new WHERE (unit_id = #{unitId} AND `group` = #{group})")
    Integer countByUnitIdAndGroup(Long unitId, Integer group);
}

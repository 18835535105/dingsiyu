package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Equipment;
import com.baomidou.mybatisplus.mapper.BaseMapper;
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
public interface EquipmentMapper extends BaseMapper<Equipment> {

    /**
     * 查询学生已装备的飞船及装备信息
     *
     * @param studentId
     * @return <ul>
     *     <li>key:type 类型</li>
     *     <li>key:imgUrl 图片地址</li>
     * </ul>
     */
    List<Map<String, Object>> selectUsedByStudentId(@Param("studentId") Long studentId);

}

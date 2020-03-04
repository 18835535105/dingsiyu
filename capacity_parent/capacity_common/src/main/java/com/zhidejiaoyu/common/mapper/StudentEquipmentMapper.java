package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentEquipment;
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
public interface StudentEquipmentMapper extends BaseMapper<StudentEquipment> {

    StudentEquipment selectByStudentIdAndEquipmentId(@Param("studentId") Long studentId, @Param("equipmentId") Long equipmentId);

    List<Long> selectEquipmentIdsByStudentId(@Param("studentId") Long studentId);

    @MapKey("equipmentId")
    Map<Long, StudentEquipment> selectByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);

    void updateTypeByEqumentsId(@Param("equmentIds") List<Long> equipmentIds,@Param("studentId") Long studentId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentEquipment;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-27
 */
public interface StudentEquipmentMapper extends BaseMapper<StudentEquipment> {

    StudentEquipment selectByStudentIdAndEquipmentId(@Param("studentId") Long studentId,@Param("equipmentId") Long equipmentId);

}

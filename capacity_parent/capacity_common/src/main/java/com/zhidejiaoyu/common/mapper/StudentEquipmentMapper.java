package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentEquipment;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
@Repository
public interface StudentEquipmentMapper extends BaseMapper<StudentEquipment> {

    StudentEquipment selectByStudentIdAndEquipmentId(@Param("studentId") Long studentId, @Param("equipmentId") Long equipmentId);

    List<Long> selectEquipmentIdsByStudentId(@Param("studentId") Long studentId);

    @MapKey("equipmentId")
    Map<Long, StudentEquipment> selectByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);

    void updateTypeByEquipmentId(@Param("equipmentIds") List<Long> equipmentIds, @Param("studentId") Long studentId);

    /**
     * 统计学生的数据数量
     *
     * @param studentId
     * @return
     */
    int countByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询正在使用的右侧装备图片
     *
     * @param studentId
     * @param type
     * @return
     */
    String selectImgUrlByStudentId(@Param("studentId") Long studentId, @Param("type") Integer type);

    /**
     * 查询正在使用的左侧装备图片
     *
     * @param studentId
     * @param type
     * @return
     */
    String selectLeftUrlByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);

    /**
     * 统计学生已装备的飞船个数
     *
     * @param studentId
     * @return
     */
    int countEquipmentShipByStudentId(@Param("studentId") Long studentId);
}

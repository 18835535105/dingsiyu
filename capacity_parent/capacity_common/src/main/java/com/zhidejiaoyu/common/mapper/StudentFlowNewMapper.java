package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface StudentFlowNewMapper extends BaseMapper<StudentFlowNew> {

    /**
     * 根据查询学生当前单元的一键排课流程
     *
     * @param studentId
     * @param unitId
     * @return
     */
    StudentFlowNew selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 更新学生流程
     *
     * @param studentId
     * @param flowId
     * @param unitId
     * @param type
     */
    @Update("update student_flow_new set current_flow_id = #{flowId} where student_id = #{studentId} and unit_id = #{unitId} and type = #{type}")
    void updateFlowIdByStudentIdAndUnitIdAndType(@Param("studentId") Long studentId, @Param("flowId") Long flowId,
                                                 @Param("unitId") Long unitId, @Param("type") Integer type);


    StudentFlowNew selectByStudentIdAndUnitIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                     @Param("type") Integer type);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import org.apache.ibatis.annotations.Delete;
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
     * 更新学生流程
     *
     * @param flowId
     * @param learnId
     */
    @Update("update student_flow_new set current_flow_id = #{flowId} where learn_id = #{learnId}")
    void updateFlowIdByStudentIdAndUnitIdAndType(@Param("flowId") Long flowId, @Param("learnId") Long learnId);

    /**
     * 根据查询学生当前单元的指定类型流程
     *
     * @param studentId
     * @param unitId
     * @param type
     * @return
     */
    StudentFlowNew selectByStudentIdAndUnitIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                     @Param("type") Integer type);
}

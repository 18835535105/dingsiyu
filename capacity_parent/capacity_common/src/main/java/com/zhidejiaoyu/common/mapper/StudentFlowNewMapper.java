package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
     * @param type
     * @return
     */
    StudentFlowNew selectByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);

    List<Long> selectDelIdByLearnIdsAndStudentId(@Param("learnList") List<Long> longs, @Param("studentId") Long studentId);

    /**
     * 删除学生指定类型的流程信息
     *
     * @param learnId
     */
    @Delete("delete from student_flow_new where learn_id = #{learnId}")
    void deleteByLearnId(@Param("learnId") Long learnId);

    /**
     * 通过learn_id查询计划信息
     *
     * @param learnId
     * @return
     */
    StudentFlowNew selectByLearnId(@Param("learnId") Long learnId);

    StudentFlowNew selectByLearnIdAndType(@Param("learnId") Long learnId, @Param("type") int type);

    void deleteByLearnIds(@Param("delLearnIds") List<Long> delLearnIds);

    /**
     * 查询学生学习一键学习流程
     *
     * @param studentId
     * @param unitId
     * @param easyOrHard
     * @return
     */
    StudentFlowNew selectByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("easyOrHard") Integer easyOrHard);
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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

}

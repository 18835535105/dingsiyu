package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 智能版学习流程数据表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface StudyFlowNewMapper extends BaseMapper<StudyFlowNew> {

    /**
     * 获取学生当前正在学习的流程
     *
     * @param dto
     * @return
     */
    StudyFlowNew selectStudentCurrentFlow(@Param("dto") NodeDto dto);

    /**
     * @param studentId
     * @param unitId
     * @param type
     * @return
     */
    StudyFlowNew selectByStudentIdAndUnitIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type);
}

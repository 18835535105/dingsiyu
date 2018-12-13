package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.CapacityStudentUnit;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 智能版学生当前学习课程和单元记录表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-12
 */
public interface CapacityStudentUnitMapper extends BaseMapper<CapacityStudentUnit> {

    /**
     * 获取学生当前模块正在学习的课程和单元
     *
     * @param studentId
     * @param type
     * @return
     */
    CapacityStudentUnit selectCurrentUnitIdByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);
}

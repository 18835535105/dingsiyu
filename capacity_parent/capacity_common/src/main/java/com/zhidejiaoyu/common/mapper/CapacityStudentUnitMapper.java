package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityStudentUnit;
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
     * @param type  学习模块：1：单词模块；2：例句听力；3：例句默写；4：例句翻译
     * @return
     */
    CapacityStudentUnit selectCurrentUnitIdByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") Integer type);
}

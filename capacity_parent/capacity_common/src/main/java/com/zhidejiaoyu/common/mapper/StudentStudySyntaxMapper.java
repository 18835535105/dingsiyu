package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-30
 */
public interface StudentStudySyntaxMapper extends BaseMapper<StudentStudySyntax> {

    /**
     * 查询学生语法当前单元学习的模块
     *
     *
     * @param studentId
     * @param unitId
     * @return
     */
    StudentStudySyntax selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}

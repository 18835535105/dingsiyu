package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-30
 */
public interface StudentStudySyntaxMapper extends BaseMapper<StudentStudySyntax> {

    /**
     * 查询学生语法当前单元学习的模块
     *
     * @param studentId
     * @param unitId
     * @return
     */
    StudentStudySyntax selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 查询学生语法所有单元学习模块并进行分组
     *
     * @param studentId
     * @return
     */
    @MapKey("courseId")
    Map<Long, Map<String, Object>> selectStudyAllByStudentId(@Param("studentId") Long studentId);

    /**
     * 删除学生指定课程的语法节点
     *
     * @param studentId
     * @param courseId
     */
    void deleteByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}

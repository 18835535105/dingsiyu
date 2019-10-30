package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
    StudentStudySyntax selectByStudentId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 查询学生语法所有单元学习模块并进行分组
     * @param studentId
     */
    @MapKey("courseId")
    Map<Long,Map<String,Object>> selectStudyAllByStudentId(@Param("studentId") Long studentId);
}

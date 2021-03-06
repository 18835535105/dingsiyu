package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
@Repository
public interface StudentStudyPlanMapper extends BaseMapper<StudentStudyPlan> {

    List<Map<String, Object>> selTeksByStudentId(@Param("studentId") Long studentId, @Param("type") int type);

    List<StudentStudyPlan> selReadCourseByStudentId(@Param("studentId") Long studentId, @Param("gradeInteger") Integer gradeInteger);

    List<Map<String, Object>> selByStudentIdAndCourseIdAndType(@Param("studentId") Long studentId, @Param("type") int type, @Param("courseId") Long courseId);

    List<Map<String, Object>> selBySentenceStudentId(@Param("studentId") Long studentId, @Param("type") int type);

    List<StudentStudyPlan> selByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("type") Integer type);

    List<StudentStudyPlan> selByStudentIdAndCourseIdAndUnitId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("type") Integer type, @Param("unitId") Long unitId);

    /**
     * 查询学生当前的学习计划
     *
     * @param studentId
     * @param startUnit
     * @param endUnit
     * @param type
     * @return
     */
    StudentStudyPlan selectCurrentPlan(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit, @Param("type") int type);

    /**
     * 查找下一个学习计划
     *
     * @param studentId
     * @param type
     * @return
     */
    StudentStudyPlan selectNextPlan(@Param("studentId") Long studentId, @Param("type") int type);

    /**
     * 查看学生未学习的计划个数
     *
     * @param studentId
     * @param type
     * @return
     */
    int countUnlearnedPlan(@Param("studentId") Long studentId, @Param("type") int type);

    /**
     * 查询学生指定类型的学习计划个数
     *
     * @param studentId
     * @param type
     * @return
     */
    int countByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") int type);

    StudentStudyPlan selSymbolByStudentId(Long id);

    StudentStudyPlan selLetterByStudentId(Long studentId);

    StudentStudyPlan selLetterStudyByStudentAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    StudentStudyPlan selStudyReadPlanByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    StudentStudyPlan selByCourseIdAndUnitIdAndType(@Param("courseId") Integer courseId,
                                                   @Param("unitId") Long unitId, @Param("type") int type,
                                                   @Param("studentId") Long studentId);

    StudentStudyPlan selReadByStudentId(@Param("studentId") Long studentId);

    int countByStudentId(@Param("studentId") Long studentId, @Param("type") Integer type);

    List<Map<String, Object>> selectSyntaxByStudentAndType(@Param("studentId") Long studentId);

    /**
     * 获取学生学习计划
     *
     * @param studentId
     * @param courseId
     * @param type
     * @return
     */
    List<StudentStudyPlan> selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("type") int type);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}

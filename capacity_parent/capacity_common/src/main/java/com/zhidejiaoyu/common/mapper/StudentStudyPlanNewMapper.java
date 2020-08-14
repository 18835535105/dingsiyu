package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface StudentStudyPlanNewMapper extends BaseMapper<StudentStudyPlanNew> {


    List<StudentStudyPlanNew> selectAllByStudentId(@Param("studentId") Long studentId);

    /**
     * 查看数据
     *
     * @param studentId
     * @param unitId
     * @param easyOrHard
     * @return
     */
    StudentStudyPlanNew selectByStudentIdAndUnitIdAndEasyOrHard
    (@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("easyOrHard") int easyOrHard);

    /**
     * 获取最终优先级最高的 limit 条记录
     *
     * @param studentId
     * @return
     */
    List<StudentStudyPlanNew> selectMaxFinalLevelByLimit(@Param("studentId") Long studentId, @Param("limit") Integer limit);

    int selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    StudentStudyPlanNew selectMaxFinalByStudentId(@Param("studentId") Long studentId);


    List<Long> getCourseIdAndGradeList(@Param("studentId") Long studentId, @Param("gradeList") List<String> gradeList);

    /**
     * 删除学生的优先级
     *
     * @param studentId
     */
    @Delete("delete from student_study_plan_new where student_id = #{studentId}")
    void deleteByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据id删除学生学习信息
     *
     * @param deleteIds
     */
    void deleteByIds(@Param("list") List<Integer> deleteIds);

    List<StudentStudyPlanNew> selectStudyPlanByStudentIdAndPage(@Param("studentId") Long studentId);

    StudentStudyPlanNew selectByStudentIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("easyOrHard") int easyOrHard);
}

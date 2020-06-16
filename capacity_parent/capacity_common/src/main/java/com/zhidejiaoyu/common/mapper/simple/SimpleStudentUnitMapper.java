package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.vo.SeniorityVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface SimpleStudentUnitMapper {
    /**
     * 解锁下一单元
     *
     * @param studentId  学生id
     * @param courseId   课程id
     * @param nextUnitId 下一单元id
     * @param type
     */
    void updateStatus(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                      @Param("nextUnitId") Long nextUnitId, @Param("type") Integer type);

    /**
     * 查看所有课程下未解锁单元的个数
     *
     * @param studentId
     * @return
     */
    @Select("select COUNT(id) from student_unit where student_id = #{studentId} ")
    int countAllUnlockByStudentId(Long studentId);

    /**
     * 根据学生id集合删除学生相关的课程信息
     *
     * @param students 学生集合
     * @return
     */
    int deleteByStudentIds(@Param("students") List<Student> students);


    List<SeniorityVo> planSeniority(@Param("area") String area, @Param("school_name") String school_name, @Param("grade") String grade, @Param("squad") String squad, @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);

    @Select("select SUM(word_status) AS sumUnit FROM student_unit WHERE student_id = #{stuId} GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("area") String area, @Param("school_name") String school_name, @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);


    List<Map> getSimpleUnitByStudentIdByCourseId(@Param("studentId") long studentId, @Param("courseId") long courseId);

    @MapKey("id")
    Map<Long, Map<Long, Object>> getOpenUnitId(@Param("studentId") long studentId, @Param("courseId") long courseId);

    @Update("update simple_student_unit set unit_id=#{unitId} where student_id=#{studentId} and type=#{type}")
    int updUnitByStudentIdAndType(@Param("studentId") Long studentId,@Param("type") int type,@Param("unitId") Long unitId);

    List<Long> getAllCourseIdByTypeToStudent(@Param("studentId") Long studentId, @Param("type") int type);
}

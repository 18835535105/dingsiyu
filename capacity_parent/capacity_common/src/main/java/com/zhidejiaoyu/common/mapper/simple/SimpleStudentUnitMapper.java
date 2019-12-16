package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.vo.SeniorityVo;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentUnit;
import com.zhidejiaoyu.common.pojo.StudentUnitExample;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface SimpleStudentUnitMapper {
    int countByExample(StudentUnitExample example);

    int deleteByExample(StudentUnitExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentUnit record);

    int insertSelective(StudentUnit record);

    List<StudentUnit> selectByExample(StudentUnitExample example);

    StudentUnit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentUnit record);

    int updateByPrimaryKey(StudentUnit record);

    /**
     * 批量增加学生与课程、单元的对应关系
     *
     * @param studentUnits
     */
    void insertList(List<StudentUnit> studentUnits);

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

    @Delete("delete from student_unit where student_id=#{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 查看当前课程下未解锁的单元个数
     *
     * @param studentId
     * @param courseId
     * @return
     */
    @Select("select count(id) from student_unit where unit_id = #{studentId} and student_id = #{courseId}")
    int countUnlockUnitByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

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

    /**
     * 当前课程单词/例句已开启单元数
     *
     * @param stuId
     * @param courses
     * @param flag    1:单词开启单元数；2：例句开启单元数
     * @return map key:课程id，value:单词/例句已开启的单元数
     */
    @MapKey("id")
    Map<Long, Map<String, Long>> countUnlockUnitMapByStudentIdAndCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses, @Param("flag") Integer flag);

    /**
     * 查询当前学生已经开通的课程数
     *
     * @param stuId
     * @return
     */
    Integer countOpenCourseByStudentId(@Param("stuId") Long stuId);

    /**
     * 查看当前学生所有单元的个数
     * <ul>
     * <li>如果 grade 不为空，查询当前学生当前学段的所有单元个数</li>
     * <li>如果 grade 为空，查询当前学生所有单元个数</li>
     * </ul>
     *
     * @param student
     * @param grade   学段
     * @return
     */
    int countUnitCountByStudentId(@Param("student") Student student, @Param("grade") String grade);

    @Select("select id from student_unit where course_id = #{id} LIMIT 0,1")
    Integer getStudentToCourse(@Param("id") Long id);

    @Select("select id from student_unit where unit_id = #{unitId} LIMIT 0,1")
    Integer getStudentToUnit(@Param("unitId") Integer unitId);

    /**
     * 查询学生可学习的下一个课程
     *
     * @param student
     * @param courseId
     * @return 剩余的所有可学习的课程，集合中第一个课程即为下个课程
     */
    List<Course> selectNextCourse(@Param("student") Student student, @Param("courseId") Long courseId);

    /**
     * 根据课程查询单词模块开启了几个单元
     *
     * @param courseIdw 课程id
     * @return
     */
    @Select("select count(id) from student_unit where course_id = #{courseIdw} and student_id = #{studentId} and word_status = 1")
    int getCountUnit(@Param("courseIdw") Long courseIdw, @Param("studentId") Long studentId);

    /**
     * 根据课程查询单元模块开启了几个单元
     *
     * @param courseIdw 课程id
     * @return
     */
    @Select("select count(id) from student_unit where course_id = #{courseIdw} and student_id = #{studentId} and sentence_status = 1")
    int getCountUnitSentenceStatus(@Param("courseIdw") Long courseIdw, @Param("studentId") Long studentId);

    List<SeniorityVo> planSeniority(@Param("area") String area, @Param("school_name") String school_name, @Param("grade") String grade, @Param("squad") String squad, @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);

    @Select("select SUM(word_status) AS sumUnit FROM student_unit WHERE student_id = #{stuId} GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("area") String area, @Param("school_name") String school_name, @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version);

    /**
     * 当前所学单元 - 单词模块
     *
     * @param course_id
     * @param studentId
     * @return
     */
    @Select("select MAX(unit_id) from student_unit where student_id = #{studentId} AND course_id = #{course_id} AND word_status = 1")
    Integer maxUnitIdByWordByCourseIdByStudentIdBy(@Param("course_id") Long course_id, @Param("studentId") Long studentId);

    /**
     * 当前所学单元 - 例句模块
     *
     * @param course_id
     * @param studentId
     * @return
     */
    @Select("select MAX(unit_id) from student_unit where student_id = #{studentId} AND course_id = #{course_id} AND sentence_status = 1")
    Integer maxUnitIdBySentenceByCourseIdByStudentIdBy(@Param("course_id") Long course_id, @Param("studentId") Long studentId);

    List<Map> getSimpleUnitByStudentIdByCourseId(@Param("studentId") long studentId, @Param("courseId") long courseId);

    @MapKey("id")
    Map<Long, Map<Long, Object>> getOpenUnitId(@Param("studentId") long studentId, @Param("courseId") long courseId);

    /**
     * 统计学生智能版课程数量
     *
     * @param student
     * @return
     */
    int countCapacity(@Param("student") Student student);

    @Update("update simple_student_unit set unit_id=#{unitId} where student_id=#{studentId} and type=#{type}")
    int updUnitByStudentIdAndType(@Param("studentId") Long studentId,@Param("type") int type,@Param("unitId") Long unitId);

    List<Long> getAllCourseIdByTypeToStudent(@Param("studentId") Long studentId, @Param("type") int type);
}

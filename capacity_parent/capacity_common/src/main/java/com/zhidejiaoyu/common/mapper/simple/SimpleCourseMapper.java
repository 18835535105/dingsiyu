package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.CourseExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SimpleCourseMapper extends BaseMapper<Course> {
    int countByExample(CourseExample example);

    int deleteByExample(CourseExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Course record);

    List<Course> selectByExample(CourseExample example);

    Course selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByExample(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByPrimaryKeySelective(Course record);

    int updateByPrimaryKey(Course record);

    String selectCourseName(int id);


    /**
     * 根据学生id查询可以看到的年级
     *
     * @param id
     * @return
     */
    @Select("select b.grade from student_unit a JOIN course b ON a.course_id = b.id AND a.student_id = #{id} GROUP BY b.grade")
    List<String> chooseGrade(@Param("id") Long id);

    /**
     * 根据学习id和年级查询标签
     *
     * @param cou
     * @return
     */
    List<Map<String, Object>> chooseGradeToLabel(Course cou);

    @Select("select grade from course_new where delStatus = 1 and grade <> '六年级' GROUP BY grade ORDER BY id")
    List<Course> retGrade();

    @Select("select version from course_new where grade = #{grade} and delStatus = 1 and `status` = 1 GROUP BY version")
    List<Course> retVersion(@Param("grade") String grade);

    @Select("select id, label, course_name as courseName from course_new where grade = #{grade} and version = #{version} and delStatus = 1 and `status` = 1")
    List<Course> retLabel(@Param("grade") String grade, @Param("version") String version);


    List<Map<String, Object>> courseLearnInfo(Long studentId);

    Map<String, Object> postStudentByCourse(@Param("courseId") Integer courseId);

    /**
     * 根据id获取课程拼接名
     *
     * @param courseId
     * @return
     */
    @Select("select course_name from course_new where id = #{id}")
    String selectByCourseName(@Param("id") String courseId);

    /**
     * 查询课程下所有单元信息及单元内单词数量
     *
     * @param courseId 课程id
     * @return
     */
    @Select("SELECT count(uv.vocabulary_id) wordCount, u.unit_name unitName, u.id unitId FROM unit_new u, unit_vocabulary_new uv, vocabulary v WHERE u.id = uv.unit_id AND v.id =uv.vocabulary_id AND v.delStatus = 1 AND u.course_id = #{courseId} GROUP BY u.id")
    List<Map<String, Object>> getAllUnitInfos(@Param("courseId") Long courseId);

    List<Map> getSimpleCourseByStudentIdByType(@Param("studentId") long studentId, @Param("type") String type);

    /**
     * 获取初学试炼学生指定学段可学习的课程
     *
     * @param studentId
     * @param phase
     * @return
     */
    List<Map<String, Object>> getSimpleCourseByStudentIdByPhase(@Param("studentId") Long studentId, @Param("phase") String phase);

    /**
     * 获取学生所有课程
     *
     * @param studentId
     * @return
     */
    List<String> getStudentCourseAllByStudentId(@Param("studentId") long studentId);

    /**
     * 获取学生指定模块下的所有课程
     *
     * @param stuId
     * @param typeStr 模块名
     * @return key 课程id
     * value 课程名称
     */
    List<Map<String, Object>> selectAllCourseByStuIdAndType(@Param("stuId") Long stuId, @Param("typeStr") String typeStr);

    List<Map<String, Object>> getCourseByIds(@Param("courseIds") List<Long> courseIds);
}

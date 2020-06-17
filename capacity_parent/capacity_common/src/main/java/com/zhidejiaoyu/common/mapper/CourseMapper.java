package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.CourseExample;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CourseMapper extends BaseMapper<Course> {
    int deleteByExample(CourseExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Course record);

    List<Course> selectByExample(CourseExample example);

    Course selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByExample(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByPrimaryKeySelective(Course record);

    int updateByPrimaryKey(Course record);

    List<Course> page(Course cou);

    Integer countUnit(Course long1);

    int addCourse(Course course);

    String selectCourseNameById(Long id);


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
     * @param student
     * @param courseId 课程id
     * @param type
     * @return
     */
    List<Map<String, Object>> getAllUnitInfos(@Param("student") Student student, @Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 获取学生
     *
     * @param student
     * @return
     */
    List<Map<String, Object>> selectVersionByStudent(@Param("student") Student student);

    /**
     * 获取学生当前版本下所有课程信息 id，courseName
     *
     * @param student
     * @param versionName
     * @return
     */
    List<Map<String, Object>> selectCourseByVersion(@Param("student") Student student, @Param("versionName") String versionName);

    List<Map<String, Object>> getAllVersion(Long studnetId);

    List<Course> getAllCourseByStudyParagraph(@Param("paragraph") String studyParagraph);

    List<Map<String, Object>> selectCourseByCourseIds(@Param("courseIds") List<Long> simpleCouseIds);

    List<Map<String, Object>> courseLearnInfo(Long studentId);
}

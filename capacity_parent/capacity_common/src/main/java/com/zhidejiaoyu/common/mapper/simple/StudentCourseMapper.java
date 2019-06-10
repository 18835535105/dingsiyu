package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.StudentCourse;
import com.zhidejiaoyu.common.pojo.StudentCourseExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentCourseMapper {
    int countByExample(StudentCourseExample example);

    int deleteByExample(StudentCourseExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentCourse record);

    int insertSelective(StudentCourse record);

    List<StudentCourse> selectByExample(StudentCourseExample example);

    StudentCourse selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudentCourse record, @Param("example") StudentCourseExample example);

    int updateByExample(@Param("record") StudentCourse record, @Param("example") StudentCourseExample example);

    int updateByPrimaryKeySelective(StudentCourse record);

    int updateByPrimaryKey(StudentCourse record);

    @Select("select course_id as courseId, course_name as courseName, update_time as updateTime from student_course where student_id = #{student_id} and type = #{i}")
	List<StudentCourse> selectCourse(@Param("student_id") Long student_id, @Param("i") int i);

    @Select("select id from student_course where course_id = #{courseId} and student_id = #{id} limit 0,1")
	Integer selectCourseisExist(@Param("courseId") Integer courseId, @Param("id") long id);
}

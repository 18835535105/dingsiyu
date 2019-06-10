package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.StudyCount;
import com.zhidejiaoyu.common.pojo.StudyCountExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudyCountMapper {
    int countByExample(StudyCountExample example);

    int deleteByExample(StudyCountExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudyCount record);

    int insertSelective(StudyCount record);

    List<StudyCount> selectByExample(StudyCountExample example);

    StudyCount selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudyCount record, @Param("example") StudyCountExample example);

    int updateByExample(@Param("record") StudyCount record, @Param("example") StudyCountExample example);

    int updateByPrimaryKeySelective(StudyCount record);

    int updateByPrimaryKey(StudyCount record);

    @Delete("delete from study_count where student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 点击再学一遍，学习遍数+1
     *
     * @param studentId
     * @param courseId
     * @return
     */
    int updateByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据课程id查询学习遍数对象
     *
     * @param studentId
     * @param courseId
     * @return
     */
    StudyCount selectByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 查询当前课程的最大学习遍数
     *
     * @param studentId
     * @param courseId
     * @return
     */
    Integer selectMaxCountByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 查询当前学习遍数的信息
     *
     * @param studentId 学生id
     * @param courseId  课程id
     * @param maxCount  当前学习遍数
     * @return
     */
    List<StudyCount> selectByCourseIdAndCount(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("maxCount") Integer maxCount);

    /**
     * 当前学生课程学习次数
     *
     * @param stuId
     * @param courses
     * @return map key：课程id，value：当前课程的学习次数
     */
    @MapKey("id")
    Map<Long, Map<String, Integer>> selectMaxStudyCountByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);

    /**
     * 获取课程的学习遍数
     *
     * @param stuId
     * @param courses
     * @return map key：课程id，value：当前课程的学习遍数
     */
    @MapKey("id")
    Map<Long, Map<String, Integer>> selectMaxCountMapByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);
}

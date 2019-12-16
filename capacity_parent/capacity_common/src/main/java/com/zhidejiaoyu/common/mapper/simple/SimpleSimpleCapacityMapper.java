package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.simple.SimpleCapacityVo;
import com.zhidejiaoyu.common.vo.simple.capacityVo.CapacityListVo;
import com.zhidejiaoyu.common.pojo.SimpleCapacity;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 简版记忆追踪表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-09-06
 */
public interface SimpleSimpleCapacityMapper extends BaseMapper<SimpleCapacity> {

    /**
     * 查询9个模块是否有需要复习的数据
     *
     * @return
     */
    List<SimpleCapacityVo> getSimpleWhetherFeview(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("date") Date date, @Param("type") int type);

    /**
     * 生成试卷-根据条件取记忆追踪的题
     *
     * @param courseId 课程id
     * @param typeTwo  题量
     * @return
     */
    List<Vocabulary> getTestPaperGenerationAll(@Param("courseId") long courseId, @Param("typeTwo") int typeTwo, @Param("unitId") String[] unitId);

    @Select("select DISTINCT(course_id) from simple_capacity where student_id = #{studentId} AND type = #{a} AND push < #{time}")
    List<Integer> getReviewCourseIdAllByModel(@Param("studentId") Long studentId, @Param("a") int a, @Param("time") String time);

    List<Map<String, Object>> selectStatusBigTenNine(@Param("studentId") Long studentId, @Param("courseId") Integer courseId, @Param("model") int model, @Param("time") String time);

    Map<String, Object> getWordLimitOneByStudentIdByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("date") Date date);

    @Delete("delete from simple_capacity where student_id = #{studentId} and unit_id = #{unitId} and type=#{type}")
    void deleteByStudenIdByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type);

    /**
     * 查询需要复习的单词个数
     *
     * @param student
     * @param courseId
     * @param type
     * @return
     */
    Integer countNeedReview(@Param("student") Student student, @Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 获取追词纪列表信息
     *
     * @param stuId    学生id
     * @param courseId 课程id
     * @param type     类型
     * @return
     */
    List<CapacityListVo> selectByCourseId(@Param("stuId") Long stuId, @Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 获取单词错误次数
     *
     * @param id
     * @param vocabularyId
     * @param unitId
     * @param type
     * @return
     */
    @Select("select fault_time from simple_capacity where student_id = #{studentId} AND vocabulary_id = #{vocabularyId} AND type = #{type} and unit_id = #{unitId}")
    Integer getFaultTime(@Param("studentId") Long id, @Param("vocabularyId") Long vocabularyId, @Param("unitId") Long unitId, @Param("type") Integer type);

    /**
     * 黄金记忆时间加指定的3小时
     *
     * @param id
     * @param vocabularyId
     * @param pushRise
     */
    @Update("update simple_capacity set push = date_add(push, interval ${pushRise} hour) where student_id = #{studentId} AND vocabulary_id = #{vocabularyId} AND type = #{type}")
    void updatePush(@Param("studentId") Long id, @Param("vocabularyId") Long vocabularyId, @Param("pushRise") int pushRise, @Param("type") int type);

    /**
     * 获取当前学生当前学习内容的记忆追踪信息
     *
     * @param studentId
     * @param vocabularyId
     * @param unitId
     * @param type
     * @return
     */
    List<SimpleCapacity> selectSimpleCapacityRecord(@Param("studentId") Long studentId, @Param("vocabularyId") Long vocabularyId, @Param("unitId") Long unitId, @Param("type") Integer type);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}

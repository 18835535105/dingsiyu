package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SimpleStudentUnit;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生当前所学课程单元 - 清学智能版 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-09-07
 */
public interface SimpleStudentUnitMapper extends BaseMapper<SimpleStudentUnit> {

	/**
	 * 获取所有模块对应的单元
	 *
	 * @return
	 */
	@MapKey(value = "type")
	Map<Integer, Map<String, Long>> getAllUnit(Long studentId);

	/**
	 * 获取指定模块学生正在学习的课程id
	 *
	 * @param studentId
	 * @param type
	 * @return
	 */
	@Select("select course_id from simple_student_unit where student_id = #{studentId} AND type = #{type}")
	Long getCourseIdByTypeToStudent(@Param("studentId") long studentId, @Param("type") int type);

	@Select("select unit_id from simple_student_unit where student_id = #{studentId} AND type = #{type}")
	Long getUnitIdByTypeToStudent(@Param("studentId") long studentId, @Param("type") int type);

	@Update("update simple_student_unit set unit_id = #{unitId} where student_id = #{studentId} and course_id = #{courseId}")
	void updateCourseIdAndUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("studentId") Long studentId);

	@Update("update simple_student_unit set unit_id = #{unitId}, course_id = #{courseId} where student_id = #{studentId} and type = #{type}")
	void updateCourseIdAndUnitIdByCourseIdByModel(@Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("studentId") Long studentId, @Param("type") int type);


    List<Long> getAllCourseIdByTypeToStudent(@Param("studentId") Long studentId, @Param("type") int type);
}

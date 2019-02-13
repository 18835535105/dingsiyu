package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityWrite;
import com.zhidejiaoyu.common.pojo.CapacityWriteExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CapacityWriteMapper extends BaseMapper<CapacityWrite> {
	int countByExample(CapacityWriteExample example);

	int deleteByExample(CapacityWriteExample example);

	int deleteByPrimaryKey(Long id);

	int insertSelective(CapacityWrite record);

	List<CapacityWrite> selectByExample(CapacityWriteExample example);

	CapacityWrite selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") CapacityWrite record, @Param("example") CapacityWriteExample example);

	int updateByExample(@Param("record") CapacityWrite record, @Param("example") CapacityWriteExample example);

	int updateByPrimaryKeySelective(CapacityWrite record);

	int updateByPrimaryKey(CapacityWrite record);

	/**
	 * 根据学生id删除
	 * 
	 * @param studentId
	 */
	@Delete("delete from capacity_write where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);

	/**
	 * 根据单元id和单词id数组查找对应的慧默写记忆追踪信息
	 * 
	 * @param studentId
	 * @param unitId
	 * @param correctWordId
	 * @return
	 */
	List<CapacityWrite> selectByUnitIdAndId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
			@Param("correctWordId") Long correctWordId);

	/**
	 * 根据课程id和单词id数组查找对应的慧默写记忆追踪信息
	 *
	 * @param studentId
	 * @param courseId
	 * @param correctWordId
	 * @return
	 */
	CapacityWrite selectByCourseIdAndId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
										@Param("correctWordId") Long correctWordId);

	/**
	 * 根据学生id和课程id获取生词信息
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 */
	List<CapacityWrite> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
			@Param("studentId") Long studentId);

	/**
	 * 根据学生id和单元id获取生词信息
	 *
	 * @param unitId
	 * @param studentId
	 * @return
	 */
	List<CapacityWrite> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
			@Param("studentId") Long studentId);

	/**
	 * 根据课程id和学生id获取需要需要达到黄金记忆点的单词数
	 *
	 * @param studentId
	 * @param courseId
	 * @return
	 */
	int countNeedReviewByStudentIdAndCourseId( @Param("studentId") Long studentId, @Param("courseId") Long courseId);

	@Select("select count(id) from capacity_write where student_id = #{student_id} and push < #{dateTime}")
	Integer countByPushByCourseid(@Param("student_id")Long student_id,@Param("dateTime") String dateTime);

	/**
	 * 根据单元id和学生id获取需要需要达到黄金记忆点的单词数
	 *
	 * @param stuId
	 * @param unitId
	 * @return
	 */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("studentId") Long stuId, @Param("unitId") Long unitId);

	/**
     * 获取单词错误次数
     *
     * @param id
     * @param vocabularyId
     * @return
     */
    @Select("select fault_time from capacity_write where student_id = #{studentId} AND vocabulary_id = #{vocabularyId} limit 1")
	Integer getFaultTime(@Param("studentId")Long id, @Param("vocabularyId")Long vocabularyId);

    /**
     * 黄金记忆时间加指定的3小时
     *
     * @param id
     * @param vocabularyId
     * @param pushRise
     */
    @Update("update capacity_write set push = date_add(push, interval ${pushRise} hour) where student_id = #{studentId} AND vocabulary_id = #{vocabularyId}")
	void updatePush(@Param("studentId")Long id, @Param("vocabularyId")Long vocabularyId, @Param("pushRise")int pushRise);

	/**
	 * 删除学生当前单元记忆追踪信息
	 *
	 * @param studentId
	 * @param startUnit
	 * @param endUnit
	 */
	@Delete("delete from capacity_write where student_id = #{studentId} and unit_id >= #{startUnit} and unit_id <= #{endUnit}")
	void deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit);
}
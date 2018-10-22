package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SentenceWrite;
import com.zhidejiaoyu.common.pojo.SentenceWriteExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SentenceWriteMapper {
	int countByExample(SentenceWriteExample example);

	int deleteByExample(SentenceWriteExample example);

	int deleteByPrimaryKey(Long id);

	int insert(SentenceWrite record);

	int insertSelective(SentenceWrite record);

	List<SentenceWrite> selectByExample(SentenceWriteExample example);

	SentenceWrite selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") SentenceWrite record, @Param("example") SentenceWriteExample example);

	int updateByExample(@Param("record") SentenceWrite record, @Param("example") SentenceWriteExample example);

	int updateByPrimaryKeySelective(SentenceWrite record);

	int updateByPrimaryKey(SentenceWrite record);

	/**
	 * 根据学生id删除
	 * 
	 * @param studentId
	 * @return
	 */
	@Delete("delete from sentence_write where student_id =#{studentId}")
	int deleteByStudentId(Long studentId);

	/**
	 * 根据学生id和课程id获取生句信息
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 */
	List<SentenceWrite> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
			@Param("studentId") Long studentId);

	/**
	 * 根据学生id和单元id获取生句信息
	 *
	 * @param unitId
	 * @param studentId
	 * @return
	 */
	List<SentenceWrite> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
			@Param("studentId") Long studentId);

	/**
	 * 根据课程id和学生id获取需要需要达到黄金记忆点的例句数
	 * 
	 * @param studentId
	 * @param courseId
	 * @return
	 */
	int countNeedReviewByStudentIdAndCourseId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

	/**
	 * 通过学生id，单元id和单词id获取当前例句的记忆追踪信息
	 * 
	 * @param studentId
	 * @param unitId
	 * @param wordId
	 * @return
	 */
	SentenceWrite selectByStuIdAndUnitIdAndWordId(@Param("stuId") Long studentId, @Param("unitId") Long unitId,
			@Param("wordId") Long wordId);

	/**
	 * 通过学生id，课程id和单词id获取当前例句的记忆追踪信息
	 *
	 * @param studentId
	 * @param courseId
	 * @param wordId
	 * @return
	 */
	SentenceWrite selectByStuIdAndCourseIdAndWordId(@Param("stuId") Long studentId, @Param("courseId") Long courseId,
													@Param("wordId") Long wordId);

	@Select("select count(id) from sentence_write where student_id = #{student_id} and push < #{dateTime}")
	Integer countByPushByCourseid(@Param("student_id")Long student_id,@Param("dateTime") String dateTime);

	/**
	 * 根据单元id和学生id获取需要需要达到黄金记忆点的例句数
	 *
	 * @param unitId
	 * @param stuId
	 * @return
	 */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("unitId") Long unitId, @Param("studentId") Long stuId);

    /**
     * 获取单词错误次数
     *
     * @param id
     * @param vocabularyId
     * @return
     */
    @Select("select fault_time from sentence_write where student_id = #{studentId} AND vocabulary_id = #{exampleId}")
	Integer getFaultTime(@Param("studentId")Long id, @Param("exampleId")Long exampleId);

    /**
     * 黄金记忆时间加指定的3小时
     *
     * @param id
     * @param vocabularyId
     * @param pushRise
     */
    @Update("update sentence_write set push = date_add(push, interval ${pushRise} hour) where student_id = #{studentId} AND vocabulary_id = #{exampleId}")
	void updatePush(@Param("studentId")Long id, @Param("exampleId")Long exampleId, @Param("pushRise")int pushRise);
}
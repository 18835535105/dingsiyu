package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityMemory;
import com.zhidejiaoyu.common.pojo.CapacityMemoryExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CapacityMemoryMapper extends BaseMapper<CapacityMemory> {
	int countByExample(CapacityMemoryExample example);

	int deleteByExample(CapacityMemoryExample example);

	int deleteByPrimaryKey(Long id);

	int insertSelective(CapacityMemory record);

	List<CapacityMemory> selectByExample(CapacityMemoryExample example);

	CapacityMemory selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") CapacityMemory record,
			@Param("example") CapacityMemoryExample example);

	int updateByExample(@Param("record") CapacityMemory record, @Param("example") CapacityMemoryExample example);

	int updateByPrimaryKeySelective(CapacityMemory record);

	int updateByPrimaryKey(CapacityMemory record);

	/**
	 * 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
	 * 
	 * @param studentId
	 * @param unitId
	 * @param wordId
	 * @return
	 */
	CapacityMemory selectByStuIdAndUnitIdAndWordId(@Param("stuId") Long studentId, @Param("unitId") Long unitId,
			@Param("wordId") Long wordId);

	/**
	 * 根据学生id删除
	 * 
	 * @param studentId
	 * @return
	 */
	@Delete("delete from capacity_memory where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);

	/**
	 * 根据单元id和单词id查找对应的慧记忆记忆追踪信息
	 * 
	 * @param studentId
	 * @param unitId
	 * @param id
	 * @return
	 */
	List<CapacityMemory> selectByUnitIdAndId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                       @Param("correctWordId") Long id);

    /**
     * 根据课程id和单词id查找对应的慧记忆记忆追踪信息
     *
     * @param studentId
     * @param courseId
     * @param id
     * @return
     */
    List<CapacityMemory> selectByCourseIdAndId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                         @Param("correctWordId") Long id);

	/**
	 * 根据学生id和课程id获取生词信息
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 */
	List<CapacityMemory> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                      @Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id获取生词信息
     *
     * @param unitId
     * @param studentId
     * @return
     */
    List<CapacityMemory> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
                                                    @Param("studentId") Long studentId);

	/**
	 * 根据课程id和学生id获取需要需要达到黄金记忆点的单词数(除去删除状态的单词)
	 * 
	 * @param studentId
	 * @param courseId
	 * @return
	 */
	int countNeedReviewByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);


	List<Integer> selectStatusBig(@Param("studentId")Long studentId, @Param("classify")int classify);
	
	List<Map<String,Object>> selectStatusBigTenNine(@Param("studentId")Long studentId, @Param("courseId") Integer courseId, @Param("classify")int classify, @Param("push")String push);

	@Select("select count(id) from capacity_memory where student_id = #{student_id} and push < #{dateTime}")
	Integer countByPushByCourseid(@Param("student_id")Long student_id, @Param("dateTime")String dateTime);

	Integer selectCountPush(@Param("student_id")Long student_id, @Param("unit_id")Integer unit_id, @Param("push") String push, @Param("classify")int classify);

	/**
	 * 根据单元id和学生id获取需要需要达到黄金记忆点的单词数(除去删除状态的单词)
	 *
	 * @param stuId
	 * @param unitId
	 * @return
	 */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("studentId") Long stuId, @Param("unitId") Long unitId);

    void delWord(@Param("id") String id, @Param("status") int status);

	/**
	 * 删除学生当前单元的记忆追踪信息
	 *
	 * @param studentId
	 * @param startUnit
	 * @param endUnit
	 */
	@Delete("delete from capacity_memory where student_id = #{studentId} and unit_id >= #{startUnit} and unit_id <= #{endUnit}")
	void deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit);

	@Delete("delete from capacity_memory where student_id = #{studentId} and unit_id = #{unitId}")
	void deleteByStudentIdAndStudyUnitId(@Param("studentId") Long studentId,@Param("unitId") Long unitId);
}
package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.MemoryTestCenter;
import com.zhidejiaoyu.common.pojo.MemoryTestCenterExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemoryTestCenterMapper {
	int countByExample(MemoryTestCenterExample example);

	int deleteByExample(MemoryTestCenterExample example);

	int deleteByPrimaryKey(Long id);

	int insert(MemoryTestCenter record);

	int insertSelective(MemoryTestCenter record);

	List<MemoryTestCenter> selectByExample(MemoryTestCenterExample example);

	MemoryTestCenter selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") MemoryTestCenter record,
                                 @Param("example") MemoryTestCenterExample example);

	int updateByExample(@Param("record") MemoryTestCenter record, @Param("example") MemoryTestCenterExample example);

	int updateByPrimaryKeySelective(MemoryTestCenter record);

	int updateByPrimaryKey(MemoryTestCenter record);

	/**
	 * 根据学生id删除
	 *
	 * @param studentId
	 * @return
	 */
	@Delete("delete from memory_test_center where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}

package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.WriteTestCenter;
import com.zhidejiaoyu.common.pojo.WriteTestCenterExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WriteTestCenterMapper {
	int countByExample(WriteTestCenterExample example);

	int deleteByExample(WriteTestCenterExample example);

	int deleteByPrimaryKey(Long id);

	int insert(WriteTestCenter record);

	int insertSelective(WriteTestCenter record);

	List<WriteTestCenter> selectByExample(WriteTestCenterExample example);

	WriteTestCenter selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") WriteTestCenter record,
                                 @Param("example") WriteTestCenterExample example);

	int updateByExample(@Param("record") WriteTestCenter record, @Param("example") WriteTestCenterExample example);

	int updateByPrimaryKeySelective(WriteTestCenter record);

	int updateByPrimaryKey(WriteTestCenter record);

	@Delete("delete from write_test_center where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}

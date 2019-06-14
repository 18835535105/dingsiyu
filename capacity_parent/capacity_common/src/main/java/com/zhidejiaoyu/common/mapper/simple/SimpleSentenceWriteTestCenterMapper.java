package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.SentenceWriteTestCenter;
import com.zhidejiaoyu.common.pojo.SentenceWriteTestCenterExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SimpleSentenceWriteTestCenterMapper {
	int countByExample(SentenceWriteTestCenterExample example);

	int deleteByExample(SentenceWriteTestCenterExample example);

	int deleteByPrimaryKey(Long id);

	int insert(SentenceWriteTestCenter record);

	int insertSelective(SentenceWriteTestCenter record);

	List<SentenceWriteTestCenter> selectByExample(SentenceWriteTestCenterExample example);

	SentenceWriteTestCenter selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") SentenceWriteTestCenter record,
                                 @Param("example") SentenceWriteTestCenterExample example);

	int updateByExample(@Param("record") SentenceWriteTestCenter record,
                        @Param("example") SentenceWriteTestCenterExample example);

	int updateByPrimaryKeySelective(SentenceWriteTestCenter record);

	int updateByPrimaryKey(SentenceWriteTestCenter record);

	/**
	 * 根据学生id删除
	 *
	 * @param studentId
	 * @return
	 */
	@Delete("delete from sentence_write_test_center where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}

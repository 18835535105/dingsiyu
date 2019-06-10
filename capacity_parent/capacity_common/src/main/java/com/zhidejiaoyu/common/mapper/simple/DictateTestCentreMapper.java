package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.DictateTestCentre;
import com.zhidejiaoyu.common.pojo.DictateTestCentreExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictateTestCentreMapper {
	int countByExample(DictateTestCentreExample example);

	int deleteByExample(DictateTestCentreExample example);

	int deleteByPrimaryKey(Long id);

	int insert(DictateTestCentre record);

	int insertSelective(DictateTestCentre record);

	List<DictateTestCentre> selectByExample(DictateTestCentreExample example);

	DictateTestCentre selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") DictateTestCentre record,
                                 @Param("example") DictateTestCentreExample example);

	int updateByExample(@Param("record") DictateTestCentre record, @Param("example") DictateTestCentreExample example);

	int updateByPrimaryKeySelective(DictateTestCentre record);

	int updateByPrimaryKey(DictateTestCentre record);

	/**
	 * 根据学生id删除
	 *
	 * @param studentId
	 * @return
	 */
	@Delete("delete from dictate_test_centre where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}

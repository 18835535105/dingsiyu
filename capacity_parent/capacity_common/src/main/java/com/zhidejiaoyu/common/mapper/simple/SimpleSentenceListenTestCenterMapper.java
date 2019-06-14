package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.SentenceListenTestCenter;
import com.zhidejiaoyu.common.pojo.SentenceListenTestCenterExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SimpleSentenceListenTestCenterMapper {
    int countByExample(SentenceListenTestCenterExample example);

    int deleteByExample(SentenceListenTestCenterExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SentenceListenTestCenter record);

    int insertSelective(SentenceListenTestCenter record);

    List<SentenceListenTestCenter> selectByExample(SentenceListenTestCenterExample example);

    SentenceListenTestCenter selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SentenceListenTestCenter record, @Param("example") SentenceListenTestCenterExample example);

    int updateByExample(@Param("record") SentenceListenTestCenter record, @Param("example") SentenceListenTestCenterExample example);

    int updateByPrimaryKeySelective(SentenceListenTestCenter record);

    int updateByPrimaryKey(SentenceListenTestCenter record);

    /**
     * 根据学生id删除
     * @param studentId
     * @return
     */
    @Delete("delete from sentence_listen_test_center where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}

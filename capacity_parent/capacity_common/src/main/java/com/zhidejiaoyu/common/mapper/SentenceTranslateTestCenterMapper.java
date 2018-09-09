package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SentenceTranslateTestCenter;
import com.zhidejiaoyu.common.pojo.SentenceTranslateTestCenterExample;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface SentenceTranslateTestCenterMapper {
    int countByExample(SentenceTranslateTestCenterExample example);

    int deleteByExample(SentenceTranslateTestCenterExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SentenceTranslateTestCenter record);

    int insertSelective(SentenceTranslateTestCenter record);

    List<SentenceTranslateTestCenter> selectByExample(SentenceTranslateTestCenterExample example);

    SentenceTranslateTestCenter selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SentenceTranslateTestCenter record, @Param("example") SentenceTranslateTestCenterExample example);

    int updateByExample(@Param("record") SentenceTranslateTestCenter record, @Param("example") SentenceTranslateTestCenterExample example);

    int updateByPrimaryKeySelective(SentenceTranslateTestCenter record);

    int updateByPrimaryKey(SentenceTranslateTestCenter record);

    /**
     * 根据学生id删除
     * @param studentId
     * @return
     */
    @Delete("delete from sentence_translate_test_center where student_id = #{studentId}")
	int deleteByStudentId(Long studentId);
}
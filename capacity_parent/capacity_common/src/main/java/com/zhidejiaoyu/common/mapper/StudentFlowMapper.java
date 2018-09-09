package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentFlow;
import com.zhidejiaoyu.common.pojo.StudentFlowExample;
import java.util.List;

import com.zhidejiaoyu.common.pojo.StudyFlow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface StudentFlowMapper {
    int countByExample(StudentFlowExample example);

    int deleteByExample(StudentFlowExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentFlow record);

    int insertSelective(StudentFlow record);

    List<StudentFlow> selectByExample(StudentFlowExample example);

    StudentFlow selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudentFlow record, @Param("example") StudentFlowExample example);

    int updateByExample(@Param("record") StudentFlow record, @Param("example") StudentFlowExample example);

    int updateByPrimaryKeySelective(StudentFlow record);

    int updateByPrimaryKey(StudentFlow record);

    @Update("update student_flow set current_flow_id = #{node} where student_id = #{studentId}")
    int updateFlowByStudentId(@Param("studentId") long studentId, @Param("node") long node);


}
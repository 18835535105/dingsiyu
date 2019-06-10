package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.StudentFlow;
import com.zhidejiaoyu.common.pojo.StudentFlowExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}

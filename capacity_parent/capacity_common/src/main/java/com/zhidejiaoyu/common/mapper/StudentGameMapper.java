package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentGame;
import com.zhidejiaoyu.common.pojo.StudentGameExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StudentGameMapper {
    int countByExample(StudentGameExample example);

    int deleteByExample(StudentGameExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentGame record);

    int insertSelective(StudentGame record);

    List<StudentGame> selectByExample(StudentGameExample example);

    StudentGame selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudentGame record, @Param("example") StudentGameExample example);

    int updateByExample(@Param("record") StudentGame record, @Param("example") StudentGameExample example);

    int updateByPrimaryKeySelective(StudentGame record);

    int updateByPrimaryKey(StudentGame record);
}
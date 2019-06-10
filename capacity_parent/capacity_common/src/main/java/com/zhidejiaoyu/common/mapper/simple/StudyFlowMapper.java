package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.StudyFlow;
import com.zhidejiaoyu.common.pojo.StudyFlowExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudyFlowMapper {
    int countByExample(StudyFlowExample example);

    int deleteByExample(StudyFlowExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudyFlow record);

    int insertSelective(StudyFlow record);

    List<StudyFlow> selectByExample(StudyFlowExample example);

    StudyFlow selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudyFlow record, @Param("example") StudyFlowExample example);

    int updateByExample(@Param("record") StudyFlow record, @Param("example") StudyFlowExample example);

    int updateByPrimaryKeySelective(StudyFlow record);

    int updateByPrimaryKey(StudyFlow record);
}

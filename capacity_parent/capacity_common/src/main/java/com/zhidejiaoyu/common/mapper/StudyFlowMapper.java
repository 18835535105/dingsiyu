package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudyFlow;
import com.zhidejiaoyu.common.pojo.StudyFlowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 根据流程名获取第一条流程数据
     * @param nodeName 流程名
     * @return
     */
    StudyFlow getNodeByNodeName(@Param("nodeName") String nodeName);

    StudyFlow getFlowInfoByStudentId(@Param("studentId") Long studentId);

    StudyFlow getLimitOneDataByFlowName(String flowName);
}
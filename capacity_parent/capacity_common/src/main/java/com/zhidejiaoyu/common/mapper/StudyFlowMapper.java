package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudyFlow;
import com.zhidejiaoyu.common.pojo.StudyFlowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyFlowMapper extends BaseMapper<StudyFlow> {
    int countByExample(StudyFlowExample example);

    int deleteByExample(StudyFlowExample example);

    int deleteByPrimaryKey(Long id);

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

    /**
     * 获取学生流程节点
     *
     * @param studentId
     * @param presentFlow   1：当前节点；2：不是当前节点
     * @return
     */
    StudyFlow selectStudentCurrentFlow(@Param("studentId") Long studentId, @Param("presentFlow") int presentFlow);

    /**
     * 获取学生当前正在学习的流程信息
     *
     * @param studentId
     * @return
     */
    StudyFlow selectCurrentFlowByStudentId(@Param("studentId") Long studentId);
}

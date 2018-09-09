package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.OpenUnitLog;
import com.zhidejiaoyu.common.pojo.OpenUnitLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenUnitLogMapper {
    int countByExample(OpenUnitLogExample example);

    int deleteByExample(OpenUnitLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OpenUnitLog record);

    int insertSelective(OpenUnitLog record);

    List<OpenUnitLog> selectByExample(OpenUnitLogExample example);

    OpenUnitLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OpenUnitLog record, @Param("example") OpenUnitLogExample example);

    int updateByExample(@Param("record") OpenUnitLog record, @Param("example") OpenUnitLogExample example);

    int updateByPrimaryKeySelective(OpenUnitLog record);

    int updateByPrimaryKey(OpenUnitLog record);

    /**
     * 查询学生今日开启单元个数
     *
     * @param studentId
     * @return
     */
    int countTodayOpenCount(@Param("studentId") Long studentId);
}
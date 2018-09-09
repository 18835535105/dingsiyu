package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.PayLog;
import com.zhidejiaoyu.common.pojo.PayLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PayLogMapper {
    int countByExample(PayLogExample example);

    int deleteByExample(PayLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogExample example);

    PayLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);

    List<Map> selectPayLogDataByStudentId(@Param("studentId") long studentId);
}
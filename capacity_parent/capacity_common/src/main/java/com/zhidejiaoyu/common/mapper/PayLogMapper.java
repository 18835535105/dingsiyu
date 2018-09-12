package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PayLog;
import com.zhidejiaoyu.common.pojo.PayLogExample;
import org.apache.ibatis.annotations.Param;

import javax.validation.Payload;
import java.util.List;
import java.util.Map;

public interface PayLogMapper extends BaseMapper<PayLog> {
    int countByExample(PayLogExample example);

    int deleteByExample(PayLogExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogExample example);

    PayLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);

    List<Map> selectPayLogDataByStudentId(@Param("studentId") long studentId);
}
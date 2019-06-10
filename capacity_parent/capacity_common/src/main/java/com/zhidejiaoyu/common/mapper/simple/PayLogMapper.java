package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PayLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PayLogMapper extends BaseMapper<PayLog> {

    List<Map> selectPayLogDataByStudentId(@Param("studentId") long studentId);
}

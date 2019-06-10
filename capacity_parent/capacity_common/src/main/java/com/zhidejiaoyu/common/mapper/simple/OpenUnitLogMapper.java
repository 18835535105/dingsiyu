package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.OpenUnitLog;
import org.apache.ibatis.annotations.Param;

public interface OpenUnitLogMapper extends BaseMapper<OpenUnitLog> {
    /**
     * 查询学生今日开启单元个数
     *
     * @param studentId
     * @return
     */
    int countTodayOpenCount(@Param("studentId") Long studentId);
}

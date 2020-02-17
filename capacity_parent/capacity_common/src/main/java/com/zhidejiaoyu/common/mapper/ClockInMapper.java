package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ClockIn;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface ClockInMapper extends BaseMapper<ClockIn> {

    /**
     * 查询学生当月的签到信息
     *
     * @param studentId
     * @param currentMonth
     * @return
     */
    List<ClockIn> selectByStudentIdWithCurrentMonth(@Param("studentId") Long studentId, @Param("currentMonth") String currentMonth);
}

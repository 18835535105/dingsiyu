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

    /**
     * 查询连续打卡天数
     *
     * @param studentId
     * @return
     */
    Integer selectLaseCardDays(@Param("studentId") Long studentId);

    /**
     * 查询指定日期学生打卡次数
     *
     * @param studentId
     * @param date      指定日期 yyyy-MM-dd
     * @return
     */
    int countByStudentIdAndCardTime(@Param("studentId") Long studentId, @Param("date") String date);
}

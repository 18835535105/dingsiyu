package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.WeekHistoryPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学生每周解锁时常 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-11
 */
public interface WeekHistoryPlanMapper extends BaseMapper<WeekHistoryPlan> {

    List<WeekHistoryPlan> selectAllByTime(@Param("time") String times);

    WeekHistoryPlan selectByTimeAndStudentId(@Param("date") String date,@Param("studentId") long studentId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.TotalHistoryPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 解锁总时常 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-11
 */
public interface TotalHistoryPlanMapper extends BaseMapper<TotalHistoryPlan> {

    TotalHistoryPlan selectByStudentId(@Param("studentId") long studentId);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIds);
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.BossHurt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * <p>
 * boss伤害累积值 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-27
 */
public interface BossHurtMapper extends BaseMapper<BossHurt> {

    /**
     * 统计指定日期范围内学生对boss造成的伤害值
     *
     * @param studentId
     * @param beginDate
     * @param endDate
     * @return
     */
    Integer selectHurtNumByBeginDateAndEndDate(@Param("studentId") Long studentId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}

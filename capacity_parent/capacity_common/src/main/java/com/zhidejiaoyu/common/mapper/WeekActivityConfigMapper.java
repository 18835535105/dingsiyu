package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.WeekActivityConfig;

/**
 * <p>
 * 运营每周活动配置 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-27
 */
public interface WeekActivityConfigMapper extends BaseMapper<WeekActivityConfig> {

    /**
     * 获取本周的活动配置信息
     *
     * @return
     */
    WeekActivityConfig selectCurrentWeekConfig();
}

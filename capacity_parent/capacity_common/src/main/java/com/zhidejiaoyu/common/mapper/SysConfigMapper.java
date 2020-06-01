package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统配置文件 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-06-01
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    SysConfig selectByExplain(@Param("explain") String explain);
}

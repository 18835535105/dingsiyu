package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SysConfig;

/**
 * <p>
 * 系统配置文件 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-26
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 通过说明查询指定内容
     *
     * @param explain
     * @return
     */
    SysConfig selectByExplain(String explain);
}

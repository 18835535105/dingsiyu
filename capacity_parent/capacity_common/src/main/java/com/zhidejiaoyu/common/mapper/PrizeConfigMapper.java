package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface PrizeConfigMapper extends BaseMapper<PrizeConfig> {

    List<PrizeConfig> selectByAdminId(@Param("amdinId") Long adminId);
}

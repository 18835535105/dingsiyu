package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.GoldLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 金币变化日志 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-10-23
 */
public interface SimpleGoldLogMapper extends BaseMapper<GoldLog> {

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}

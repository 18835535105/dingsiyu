package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.GoldLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 金币变化日志 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-10-23
 */
public interface GoldLogMapper extends BaseMapper<GoldLog> {

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);

    Integer selectGoldByStudentIdAndDate(@Param("studentId") Long studentId,@Param("date") Date date,@Param("type") int type);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhidejiaoyu.common.pojo.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 操作日志 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2017-07-11
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 获取操作日志
     */
    List<Map<String, Object>> getOperationLogs(@Param("page") Page<OperationLog> page, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("logName") String logName, @Param("logType") String logType, @Param("orderByField") String orderByField, @Param("isAsc") boolean isAsc);

    /**
     * 获取校区动态列表内容
     *
     * @param pagination
     * @param userId
     * @param campus
     * @param teacherName
     * @return
     */
//    List<CampusLogListVo> selectCampusLogListVo(@Param("pagination") Page pagination, @Param("userId") Integer userId, @Param("campus") String campus, @Param("teacherName") String teacherName);
//
//    /**
//     * 查询日志详情
//     *
//     * @param logId
//     * @return
//     */
//    CampusLogListVo selectByLogId(Integer logId);
}

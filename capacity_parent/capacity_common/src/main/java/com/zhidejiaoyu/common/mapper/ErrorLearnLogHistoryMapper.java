package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ErrorLearnLog;
import com.zhidejiaoyu.common.pojo.ErrorLearnLogHistory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface ErrorLearnLogHistoryMapper extends BaseMapper<ErrorLearnLogHistory> {

    void insertListByErrorLearnLogs(@Param("logs") List<ErrorLearnLog> errorLearnLogs);
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SchoolGoldFactory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-04-07
 */
public interface SchoolGoldFactoryMapper extends BaseMapper<SchoolGoldFactory> {

    SchoolGoldFactory selectByAdminId(@Param("adminId") Long adminId);
}

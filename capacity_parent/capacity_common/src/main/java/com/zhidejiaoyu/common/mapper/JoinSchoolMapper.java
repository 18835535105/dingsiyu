package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2018-11-07
 */
public interface JoinSchoolMapper extends BaseMapper<JoinSchool> {

    /**
     * 查询加盟校信息
     *
     * @param schoolAdminId
     * @return
     */
    JoinSchool selectByUserId(@Param("schoolAdminId") Integer schoolAdminId);
}






















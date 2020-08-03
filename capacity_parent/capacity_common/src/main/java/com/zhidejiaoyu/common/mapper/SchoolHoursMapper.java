package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SchoolHours;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-09-27
 */
public interface SchoolHoursMapper extends BaseMapper<SchoolHours> {

    /**
     * 根据校管id查询课时拥有信息
     * @param adminId
     * @return
     */
    SchoolHours selectByAdminId(@Param("adminId") Integer adminId);

    /**
     * 根据校管id修改课时信息
     * @param type
     * @param adminId
     * @return
     */
    int updateByAdminId(@Param("type") String type, @Param("adminId") Integer adminId);

    List<SchoolHours> selectAll();
}

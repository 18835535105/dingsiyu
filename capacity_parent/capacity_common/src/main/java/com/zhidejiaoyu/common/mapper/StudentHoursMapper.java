package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.zhidejiaoyu.common.pojo.StudentHours;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-09-27
 */
public interface StudentHoursMapper extends BaseMapper<StudentHours> {

    List<Map<String, Object>> selByAdminId(@Param("pagination") Pagination pagination, @Param("adminId") Integer adminId);

    List<StudentHours> selectDeatilsByAdminId(@Param("adminId") Long adminId, @Param("time") Date time);

    List<Map<String, Object>> selectCountByDayTime(@Param("startDay") Date startDay, @Param("endTime") Date time, @Param("adminId") Long adminId);
}

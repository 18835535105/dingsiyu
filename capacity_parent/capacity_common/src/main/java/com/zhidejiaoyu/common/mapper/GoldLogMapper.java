package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.GoldLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 金币变化日志 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-10-23
 */
@Repository
public interface GoldLogMapper extends BaseMapper<GoldLog> {

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);

    /**
     * 统计学生今日获取的金币数
     *
     * @param studentId
     * @return
     */
    @Select("select sum(gold_add) from gold_log where student_id = #{studentId} and type = 1 and TO_DAYS(create_time) = TO_DAYS(now()) ")
    Integer sumTodayAddGold(@Param("studentId") Long studentId);

    Integer selectGoldByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") Date date, @Param("type") int type);

    /**
     * 根据校管id获取学生每日增加的金币
     *
     * @param adminId
     * @param date
     */
    Integer selectGoldByAdminIdAndDate(@Param("adminId") Long adminId, @Param("date") Date date);

    /**
     * 获取指定日期内校管下学生添加金币信息
     *
     * @param schoolAdminId
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> selectGoldByAdminIdAndStartDateAndEndTime(@Param("adminId") Integer schoolAdminId,
                                                                        @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取指定日期内校管下学生添加金币数量
     *
     * @param schoolAdminId
     * @param startTime
     * @param endTime
     * @return
     */
    int countByAdminIdAndStartDateAndEndTime(@Param("adminId") Integer schoolAdminId,
                                             @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查询指定日期范围内学生贡献到金币工厂的金币数量
     *
     * @param studentId
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer selectGoldContributeByBeginTimeAndEndTime(@Param("studentId") Long studentId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
}

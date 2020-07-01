package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ClockIn;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DailyStateVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface ClockInMapper extends BaseMapper<ClockIn> {

    /**
     * 查询学生当月的签到信息
     *
     * @param studentId
     * @param currentMonth
     * @return
     */
    List<ClockIn> selectByStudentIdWithCurrentMonth(@Param("studentId") Long studentId, @Param("currentMonth") String currentMonth);

    /**
     * 查询学生全部的签到信息
     *
     * @param studentId
     * @return
     */
    List<ClockIn> selectByStudentId(@Param("studentId") Long studentId);


    /**
     * 查询连续打卡天数
     *
     * @param studentId
     * @return
     */
    Integer selectLaseCardDays(@Param("studentId") Long studentId);

    /**
     * 查询指定日期学生打卡次数
     *
     * @param studentId
     * @param date      指定日期 yyyy-MM-dd
     * @return
     */
    int countByStudentIdAndCardTime(@Param("studentId") Long studentId, @Param("date") String date);


    @MapKey("studentIds")
    Map<Long, Map<String, Object>> selectByStudentIds(@Param("studentIds") List<Long> studentIds, @Param("date") Date date);

    /**
     * 查询指定日期指定学生的打卡记录
     *
     * @param account 学生账号
     * @param date    打卡日期
     * @return
     */
    List<DailyStateVO> selectByStudentAccount(@Param("account") String[] account, @Param("date") Date date);

    /**
     * 查询当前学生指定日期的连续打卡天数
     *
     * @param studentId
     * @param date
     * @return
     */
    @Select("select card_days from clock_in where student_id = #{studentId} and to_days(card_time) = to_days(#{date}) order by card_time desc limit 1")
    Integer selectCardDaysByStudentIdAndCardTime(@Param("studentId") Long studentId, @Param("date") Date date);

    /**
     * 统计今天学生是否已经打卡
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from clock_in where student_id = #{studentId} and to_days(now()) = to_days(card_time)")
    int countTodayInfoByStudentId(@Param("studentId") Long studentId);

    /**
     * 学生打卡天数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from clock_in where student_id = #{studentId}")
    int countByStudentId(@Param("studentId") Long studentId);
}

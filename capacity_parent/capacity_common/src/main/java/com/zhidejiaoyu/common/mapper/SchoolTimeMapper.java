package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SchoolTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface SchoolTimeMapper extends BaseMapper<SchoolTime> {

    /**
     * 查询指定用户的校区时间表
     *
     * @param userId
     * @param type
     * @param month
     * @param week
     * @return
     */
    SchoolTime selectByUserIdAndTypeAndMonthAndWeek(@Param("userId") Long userId, @Param("type") int type,
                                                    @Param("month") Integer month, @Param("week") Integer week);

    /**
     * 获取当前周的数据
     *
     * @param month
     * @param week
     * @return
     */
    List<Map<String, Object>> selectByMonthAndWeek(@Param("month") int month, @Param("week") int week);

    /**
     * 获取当前周的学生数据
     *
     * @param month
     * @param week
     * @return
     */
    Map<String, Object> selectByMonthAndWeekAndStudentId(@Param("month") int month, @Param("week") int week, @Param("studentId") Long studentId);

    Integer selectCountByStudentId(@Param("studentId") Long studentId);

    int selectByGradeAndLabel(@Param("grade") String grade,@Param("label") String label,@Param("type") int type,@Param("userId") Long userId);
}

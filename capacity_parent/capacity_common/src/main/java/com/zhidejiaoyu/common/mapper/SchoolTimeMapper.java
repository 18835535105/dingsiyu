package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SchoolTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @param gradeList
     * @return
     */
    SchoolTime selectByUserIdAndTypeAndMonthAndWeek(@Param("userId") Long userId, @Param("type") int type,
                                                    @Param("month") Integer month, @Param("week") Integer week,
                                                    @Param("gradeList") List<String> gradeList);


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

    int selectByGradeAndLabel(@Param("grade") String grade, @Param("label") String label, @Param("type") int type, @Param("userId") Long userId);

    /**
     * 查询半年后可以学习的所有课程
     *
     * @param userId
     * @param grade
     * @param month
     * @return
     */
    List<SchoolTime> selectAfterSixMonth(@Param("userId") Integer userId, @Param("grade") String grade, @Param("month") int month);

    /**
     * 查询当前计划的下一个计划
     *
     * @param userId
     * @param id     当前校区时间id
     * @return
     */
    SchoolTime selectNextByUserIdAndId(@Param("userId") Integer userId, @Param("id") Long id);

    /**
     * 查看是否配置了当前年级的课程
     *
     * @param grade
     * @return
     */
    @Select("select count(id) from school_time where grade = #{grade}")
    int countByGrade(@Param("grade") String grade);

    /**
     * 查询指定年级的课程计划
     *
     * @param userId
     * @param gradeList
     * @return
     */
    List<SchoolTime> selectSmallThanCurrentGrade(@Param("userId") Integer userId, @Param("gradeList") List<String> gradeList);

    SchoolTime selectByUserIdAndGrade(@Param("userId") Integer userId,@Param("grade") String grade);
}

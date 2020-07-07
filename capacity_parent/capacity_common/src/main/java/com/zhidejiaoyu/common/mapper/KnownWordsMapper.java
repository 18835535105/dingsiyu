package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.KnownWords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * <p>
 * 记录学生的熟词信息，用于每周活动统计学生熟词数量 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-28
 */
public interface KnownWordsMapper extends BaseMapper<KnownWords> {

    /**
     * 查询学生指定日期的熟词数
     *
     * @param studentId
     * @param activityDateBegin
     * @param activityDateEnd
     * @return
     */
    @Select("select count(distinct word_id) from known_words where student_id = #{studentId} and to_days(now()) >= to_days(#{activityDateBegin}) and to_days(now()) <= to_days(#{activityDateEnd})")
    int countByStudentIdThisWeek(@Param("studentId") Long studentId, @Param("activityDateBegin") Date activityDateBegin, @Param("activityDateEnd") Date activityDateEnd);

    /**
     * 查询学生的熟词数
     *
     * @param studentId
     * @return
     */
    @Select("select count(distinct word_id) from known_words where student_id = #{studentId}")
    Integer countByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生指定日期的熟词数
     *
     * @param studentId
     * @return
     */
    Integer countByStudentIdAndCreateTime(@Param("studentId") Long studentId, @Param("date") String date);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
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
 * @since 2020-06-02
 */
public interface CurrentDayOfStudyMapper extends BaseMapper<CurrentDayOfStudy> {


    List<CurrentDayOfStudy> selectByDate(@Param("date") String date);

    /**
     * 查询学生指定二维码序号的信息
     *
     * @param studentId
     * @param num
     * @return
     */
    CurrentDayOfStudy selectByStudentIdAndQrCodeNum(@Param("studentId") Long studentId, @Param("num") Integer num);

    /**
     * 查询指定日期学生信息上传次数
     *
     * @param studentId
     * @param date
     * @return
     */
    int countByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") Date date);

    /**
     * 查询学生指定日期上传次数
     *
     * @param studentIds
     * @param date
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Long>> countByStudentIdsAndDate(@Param("studentIds") List<Long> studentIds, @Param("date") Date date);

    /**
     * 查询指定月份有飞行记录的日期
     *
     * @param studentId
     * @param month
     * @return
     */
    List<String> selectCreateTimeByMonth(@Param("studentId") Long studentId, @Param("month") String month);

    /**
     * 查询学生指定日期的飞行记录
     *
     * @param studentId
     * @param date
     * @return
     */
    CurrentDayOfStudy selectByStudentIdAndCreateTime(@Param("studentId") Long studentId, @Param("date") String date);

    /**
     * 查询学生当前二维码是否已经使用过
     *
     * @param studentId
     * @param num       二维码编号
     * @return
     */
    @Select("select count(id) from current_day_of_study where student_id = #{studentId} and qr_code_num = #{num}")
    int countByStudentIdAndQrCodeNum(@Param("studentId") Long studentId, @Param("num") Integer num);

    /**
     * 查询学生今天的智慧笔记记录
     * @param studentId
     * @return
     */
    CurrentDayOfStudy selectTodayByStudentId(@Param("studentId") Long studentId);
}

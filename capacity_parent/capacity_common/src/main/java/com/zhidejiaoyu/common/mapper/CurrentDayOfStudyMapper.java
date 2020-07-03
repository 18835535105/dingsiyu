package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import org.apache.ibatis.annotations.MapKey;
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
}

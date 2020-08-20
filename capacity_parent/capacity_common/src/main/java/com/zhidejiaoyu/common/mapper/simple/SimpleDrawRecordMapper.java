package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.DrawRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖记录表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface SimpleDrawRecordMapper extends BaseMapper<DrawRecord> {

    /**
     * 根据id查询抽奖数据
     * @param studentId   studentId学生id
     * @return
     */
    List<DrawRecord> selByStudentId(@Param("studentId") Long studentId);



    /**
     * 查询当前日期抽奖数量
     * @param map
     * @return
     */
    Integer selAwardNow(Map<String, Object> map);


    /**
     *数量
     * @return
     */
    @Select("select count(id) from draw_record where student_id=#{studentId}")
    Integer selNumber(Integer studentId);

    /**
     * 查看当天抽奖数量
     * @param name
     * @param date
     * @return
     */
    Integer selDrawSize(@Param("name") String name, @Param("time") String date);

    /**
     * 查看奖品抽奖人数量
     * @param names
     * @param date
     * @return
     */
    Integer selDrawSizes(@Param("names") List<String> names, @Param("time") Date date);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}

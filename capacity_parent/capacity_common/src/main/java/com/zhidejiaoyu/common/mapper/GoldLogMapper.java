package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.GoldLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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

    Integer selectGoldByStudentIdAndDate(@Param("studentId") Long studentId,@Param("date") Date date,@Param("type") int type);
}

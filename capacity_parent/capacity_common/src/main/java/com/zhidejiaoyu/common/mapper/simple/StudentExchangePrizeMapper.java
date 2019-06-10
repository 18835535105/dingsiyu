package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentExchangePrize;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
public interface StudentExchangePrizeMapper extends BaseMapper<StudentExchangePrize> {


    List<Map<String,Object>> getAll(@Param("start") int start, @Param("number") int number, @Param("studentId") Long studentId);

    @Select("select count(id) from student_exchange_prize where student_id=#{studentId}")
    Integer getAllNumber(Long studentId);
}

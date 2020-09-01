package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhidejiaoyu.common.pojo.StudentExchangePrize;
import com.zhidejiaoyu.common.vo.studentExchangPrize.StudentExchangePrizeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
public interface SimpleStudentExchangePrizeMapper extends BaseMapper<StudentExchangePrize> {


    List<Map<String,Object>> getAll(@Param("start") Integer start, @Param("number") Integer number, @Param("studentId") Long studentId);

    @Select("select count(id) from student_exchange_prize where student_id=#{studentId}")
    Integer getAllNumber(Long studentId);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);

    List<StudentExchangePrizeVo> selectListByAccountAndName(@Param("page") Page<StudentExchangePrizeVo> page,@Param("studentName") String name, @Param("adminId") Integer adminId,
                                                            @Param("type")Integer type);

    @Update("update student_exchange_prize set state = #{state} where id = #{prizeId}")
    Integer updateByPrizeId(@Param("prizeId") Long prizeId ,@Param("state") Integer state);
}

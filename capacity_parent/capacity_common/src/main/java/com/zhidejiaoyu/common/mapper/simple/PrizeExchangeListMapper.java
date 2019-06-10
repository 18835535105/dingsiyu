package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 奖品兑换表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-21
 */
public interface PrizeExchangeListMapper{

    PrizeExchangeList  selById(Long prizeId);

    @Select("select count(id) from prize_exchange_list where school_id = #{sysAdminId} and state = 1")
    Integer getSize(Long sysAdminId);


    List<PrizeExchangeList> getAll(@Param("start") int start, @Param("number") int number, @Param("schoolId") Long sysAdminId, @Param("teacherId") Long teacherId, @Param("type") int type);

    @Update("update prize_exchange_list set surplus_number = #{sulperNumber} where id = #{prizeId} ")
    Integer updSulpersNumber(@Param("prizeId") Long prizeId, @Param("sulperNumber") int sulperNumber);

    List<PrizeExchangeList> getAllByType(@Param("teacherId") Long teacherId, @Param("adminId") Integer schoolAdminById, @Param("type") int type);

    Integer getCountByType(@Param("teacherId") Long teacherId, @Param("adminId") Integer schoolAdminById, @Param("type") int type);
}

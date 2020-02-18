package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import org.apache.ibatis.annotations.Param;
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
public interface PrizeExchangeListMapper {

    PrizeExchangeList selById(Long prizeId);

    List<PrizeExchangeList> getAll(@Param("schoolId") Long sysAdminId, @Param("teacherId") Long teacherId, @Param("type") int type);

    @Update("update prize_exchange_list set surplus_number = #{sulperNumber} where id = #{prizeId} ")
    Integer updSulpersNumber(@Param("prizeId") Long prizeId, @Param("sulperNumber") int sulperNumber);

    List<PrizeExchangeList> getAllByType(@Param("teacherId") Long teacherId, @Param("adminId") Integer schoolAdminById, @Param("type") int type);

    Integer getCountByType(@Param("teacherId") Long teacherId, @Param("adminId") Integer schoolAdminById, @Param("type") int type);

    /**
     * 查询校区下的奖品
     *
     * @param schoolAdminId
     * @param orderBy       排序规则
     * @param orderField    排序字段
     * @return
     */
    List<PrizeExchangeList> selectBySchoolId(@Param("schoolAdminId") Integer schoolAdminId, @Param("orderField") String orderField,
                                             @Param("orderBy") String orderBy);
}

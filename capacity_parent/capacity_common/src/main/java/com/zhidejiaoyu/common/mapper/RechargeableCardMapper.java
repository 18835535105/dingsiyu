package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.zhidejiaoyu.common.pojo.RechargeableCard;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RechargeableCardMapper extends BaseMapper<RechargeableCard> {


    List<RechargeableCard> selList(@Param("page") Pagination page);

    @Update("update rechargeable_card set name=#{card.name} , time=#{card.time} ,update_time=#{card.updateTime}  where id =#{card.id}")
    Integer updById(@Param("card") RechargeableCard card);

    @Update("update rechargeable_card set state=#{state} ,update_time=#{date}  where id =#{cardId}")
    Integer updRechargeableCard(@Param("cardId") Long rechargeableCardId, @Param("state") Integer type, @Param("date") Date date);

    List<RechargeableCard> getAddRechargeableCard();

    List<RechargeableCard> selAllRechargeableCard();

    @MapKey("id")
    Map<Integer,Object> selAllRechargeableCardMap();

    List<Map<String,Object>> selSchoolRechargeableCard(@Param("userId") Integer userId);

    @Delete("DELETE FROM rechargeable_card WHERE id=#{cardId} ")
    void deleteCardById(Integer cardId);

}

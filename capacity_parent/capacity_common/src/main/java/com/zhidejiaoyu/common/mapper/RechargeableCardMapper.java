package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.RechargeableCard;
import org.apache.ibatis.annotations.MapKey;

import java.util.Map;

public interface RechargeableCardMapper extends BaseMapper<RechargeableCard> {




    @MapKey("id")
    Map<Integer,Map<String,Object>> selAllRechargeableCardMap();



}

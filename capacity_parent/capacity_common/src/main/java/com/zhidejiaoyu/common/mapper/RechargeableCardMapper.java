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




    @MapKey("id")
    Map<Integer,Map<String,Object>> selAllRechargeableCardMap();



}

package com.dfdz.teacher.business.prizeExchangeList.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.util.List;

public interface PrizeExchangeListService extends IService<PrizeExchangeList> {

    ServerResponse<Object> getAllList(PrizeExchangeListDto prizeExchangeListDto);

    Object addPrizeExchangeList(AddPrizeExchangeListDto dto);

    Object updatePrizeExchangeList(AddPrizeExchangeListDto dto);

    Object getSchoolName(String openId);

    Object selectByPirzeId(AddPrizeExchangeListDto dto);

    Object deletePrizes(String openId, List<Integer> prizeIds);
}

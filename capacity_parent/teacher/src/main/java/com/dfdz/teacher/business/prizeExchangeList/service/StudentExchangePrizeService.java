package com.dfdz.teacher.business.prizeExchangeList.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import com.zhidejiaoyu.common.pojo.StudentExchangePrize;

public interface StudentExchangePrizeService extends IService<StudentExchangePrize> {
    Object getStudentList(StudentExchangePrizeListDto dto);

    Object updateByPrizeId(Long prizeId, String openId);

    Object updateByPrizeIdAndState(Long prizeId, Integer state, String openId);
}

package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejiaoyu.student.business.service.BaseService;

public interface PrizeConfigService extends BaseService<PrizeConfig> {
    Object getPrizeConfig(String openId, Long adminId, Long studentId,String weChatimgUrl,String weChatName);

    Object getAdmin(String openId);
}

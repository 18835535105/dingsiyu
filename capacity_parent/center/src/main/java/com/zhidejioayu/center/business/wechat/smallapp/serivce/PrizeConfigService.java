package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PrizeConfig;

public interface PrizeConfigService extends IService<PrizeConfig> {
    Object getPrizeConfig(String openId, Long adminId, Long studentId,String weChatimgUrl,String weChatName);

    Object getAdmin(String openId);
}

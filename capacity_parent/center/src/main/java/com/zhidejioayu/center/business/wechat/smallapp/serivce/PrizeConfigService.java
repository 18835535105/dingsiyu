package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeConfigDTO;

public interface PrizeConfigService extends IService<PrizeConfig> {
    Object getPrizeConfig(PrizeConfigDTO dto);

    Object getAdmin(String openId);
}

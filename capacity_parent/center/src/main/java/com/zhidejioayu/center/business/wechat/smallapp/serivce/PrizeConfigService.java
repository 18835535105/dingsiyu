package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeConfigDTO;
import com.zhidejioayu.center.business.wechat.smallapp.vo.ReturnAdminVo;

public interface PrizeConfigService extends IService<PrizeConfig> {
    Object getPrizeConfig(PrizeConfigDTO dto);

    ServerResponse<ReturnAdminVo> getAdmin(String openId);
}

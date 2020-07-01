package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.ReturnAdminVo;

public interface PrizeConfigService extends BaseService<PrizeConfig> {
    Object getPrizeConfig(String openId, Long adminId, Long studentId,String weChatimgUrl,String weChatName);

    ServerResponse<ReturnAdminVo> getAdmin(String openId);
}

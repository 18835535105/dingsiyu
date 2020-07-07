package com.zhidejioayu.center.business.wechat.robot.service.impl;

import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DailyStateVO;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.feignclient.wxrobot.BaseWxRobotFeignClient;
import com.zhidejioayu.center.business.wechat.robot.service.WxRobotService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/7/7 10:26:26
 */
@Slf4j
@Service
public class WxRobotServiceImpl implements WxRobotService {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Override
    public ServerResponse<Object> getDailyState(String account) {
        String[] accountArray = account.split(",");

        List<Map<String, String>> accountAndServerNames = businessUserInfoMapper.selectAccountAndServerName(accountArray);
        if (CollectionUtils.isEmpty(accountAndServerNames)) {
            log.info("未查询到账号所属服务器信息！");
            return ServerResponse.createByError(400, "未查询到账号所属服务器信息！");
        }

        Map<String, String> accountAndServerNameMap = new HashMap<>(16);
        accountAndServerNames.forEach(accountAndServerName -> {
            String serverName = accountAndServerName.get("serverName");
            String account1 = accountAndServerName.get("account");
            if (accountAndServerNameMap.containsKey(serverName)) {
                String accountStr = accountAndServerNameMap.get(serverName) + "," + account1;
                accountAndServerNameMap.put(serverName, accountStr);
            } else {
                accountAndServerNameMap.put(serverName, account1);
            }
        });

        List<DailyStateVO> vos = new ArrayList<>();
        accountAndServerNameMap.forEach((serverName, accountStr) -> {
            BaseWxRobotFeignClient wxRobotFeignClient = FeignClientUtil.getWxRobotFeignClient(serverName);
            vos.addAll(wxRobotFeignClient.getDailyState(accountStr));
        });

        return ServerResponse.createBySuccess(vos);
    }
}

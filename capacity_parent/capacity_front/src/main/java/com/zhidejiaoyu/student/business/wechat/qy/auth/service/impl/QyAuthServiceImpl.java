package com.zhidejiaoyu.student.business.wechat.qy.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.auth.service.QyAuthService;
import com.zhidejiaoyu.student.business.wechat.qy.auth.vo.UserIdToOpenidVO;
import com.zhidejiaoyu.student.business.wechat.qy.auth.vo.UserInfoVO;
import com.zhidejiaoyu.student.business.wechat.qy.constant.QyApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/4 14:18:18
 */
@Slf4j
@Service
public class QyAuthServiceImpl implements QyAuthService {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public ServerResponse<Object> getUserInfo() {
        String code = HttpUtil.getHttpServletRequest().getParameter("code");
        String userInfoApi = QyApiConstant.getUserInfoApi(code);
        String forObject = restTemplate.getForObject(userInfoApi, String.class);

        UserInfoVO userInfoVO = JSONObject.parseObject(forObject, UserInfoVO.class);
        if (userInfoVO == null || userInfoVO.getErrcode() != 0) {
            log.error("获取企业微信授权信息失败！msg={}", forObject);
            throw new ServiceException("获取企业微信授权信息失败!msg=" + forObject);
        }

        if (StringUtil.isNotEmpty(userInfoVO.getUserId())) {
            // 将userId转换为openid
            String userIdToOpenIdApi = QyApiConstant.getUserIdToOpenIdApi();
            Map<String, String> params = new HashMap<>(16);
            params.put("userid", userInfoVO.getUserId());
            String s = restTemplate.postForObject(userIdToOpenIdApi, params, String.class);
            UserIdToOpenidVO userIdToOpenidVO = JSONObject.parseObject(s, UserIdToOpenidVO.class);

            if (userIdToOpenidVO == null || userIdToOpenidVO.getErrcode() != 0) {
                log.error("企业微信userId转换openid失败！msg={}", s);
                throw new ServiceException("企业微信userId转换openid失败!msg=" + s);
            }
            String  openid = userIdToOpenidVO.getOpenid();
            userInfoVO.setOpenId(openid);
        }

        return ServerResponse.createBySuccess(userInfoVO);
    }
}

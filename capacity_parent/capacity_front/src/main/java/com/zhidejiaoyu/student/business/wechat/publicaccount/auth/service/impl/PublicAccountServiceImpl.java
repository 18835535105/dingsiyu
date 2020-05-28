package com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.MacIpUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.ConfigVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.UserInfoVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ApiConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ConfigConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.util.UserInfoUtil;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.AccessTokenVO;
import com.zhidejiaoyu.student.business.wechat.util.AccessTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: wuchenxi
 * @date: 2020/4/28 10:33:33
 */
@Slf4j
@Service
public class PublicAccountServiceImpl implements PublicAccountService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private LocationUtil locationUtil;

    @Override
    public ServerResponse<Object> getOpenId(String code) {
        String publicAccountOpenId = UserInfoUtil.getPublicAccountOpenId(code);
        return ServerResponse.createBySuccess(publicAccountOpenId);
    }

    @Override
    public ServerResponse<Object> getCard(String cardName) {
        try {
            String ip = MacIpUtil.getIpAddr(HttpUtil.getHttpServletRequest());
            LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude(ip);

            // todo:获取地址名，拼接 地址/cadName返回给前端图片路径，如果没有找到指定的地址，返回共有的海报

        } catch (Exception e) {
            log.error("获取学生登录IP地址出错，error=[{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public ServerResponse<Object> getUserInfo(String code) {
        AccessTokenVO publicAccountAuthAccessTokenVO = UserInfoUtil.getPublicAccountAuthAccessTokenVO(code);
        String userInfoApiUrl = ApiConstant.getUserInfoApi(publicAccountAuthAccessTokenVO.getAccess_token(), publicAccountAuthAccessTokenVO.getOpenid());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(userInfoApiUrl, String.class);
        UserInfoVO userInfoVO = JSON.parseObject(forEntity.getBody(), UserInfoVO.class);

        log.info("公众号授权获取用户信息结果：{}", forEntity.getBody());
        if (userInfoVO == null) {
            log.error("获取公众号用户信息失败, body = null！");
            throw new ServiceException("获取公众号用户信息失败！");
        }

        return ServerResponse.createBySuccess(userInfoVO);
    }

    @Override
    public ServerResponse<Object> getConfig(String currentUrl) {

        String publicAccountAccessToken = AccessTokenUtil.getPublicAccountAccessToken();
        String url = ApiConstant.getJSAPITicket(publicAccountAccessToken);
        String response = restTemplate.getForObject(url, String.class);

        JSONObject jsonObject = JSON.parseObject(response);

        if (jsonObject.getInteger("errcode") != 0) {
            log.error("获取ticket失败！");
            return ServerResponse.createByErrorMessage(response);
        }

        String ticket = jsonObject.getString("ticket");

        long timeStamp = System.currentTimeMillis();
        String nonceStr = UUID.randomUUID().toString();

        Map<String, Object> sortMap = new HashMap<>(16);
        sortMap.put("jsapi_ticket", ticket);
        sortMap.put("timestamp", timeStamp);
        sortMap.put("url", currentUrl);
        StringBuilder sb = new StringBuilder();
        sortMap.forEach((key, value) -> {
            sb.append(key).append("=").append(value);
        });
        String signature = DigestUtils.sha1Hex(sb.toString());

        ConfigVO configVO = new ConfigVO();
        configVO.setAppId(ConfigConstant.APP_ID);
        configVO.setTimestamp(timeStamp);
        configVO.setNonceStr(nonceStr);
        configVO.setSignature(signature);

        return ServerResponse.createBySuccess(configVO);
    }
}

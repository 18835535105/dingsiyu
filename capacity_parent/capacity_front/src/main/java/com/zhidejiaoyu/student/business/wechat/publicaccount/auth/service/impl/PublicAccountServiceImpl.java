package com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.MacIpUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.UserInfoVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ApiConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.util.UserInfoUtil;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.AccessTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

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
}

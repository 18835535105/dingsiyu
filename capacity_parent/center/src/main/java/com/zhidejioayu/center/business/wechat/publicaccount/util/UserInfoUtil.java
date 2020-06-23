package com.zhidejioayu.center.business.wechat.publicaccount.util;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejioayu.center.business.wechat.common.AccessTokenVO;
import com.zhidejioayu.center.business.wechat.publicaccount.constant.ApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 微信公众号授权用户信息
 *
 * @author: wuchenxi
 * @date: 2020/5/14 15:43:43
 */
@Slf4j
@Component
public class UserInfoUtil {


    private static RestTemplate restTemplate;

    @Resource
    private RestTemplate restTemplateTmp;

    @PostConstruct
    public void init() {
        restTemplate = this.restTemplateTmp;
    }


    /**
     * 获取微信公众号授权 access_token
     *
     * @return
     */
    public static AccessTokenVO getPublicAccountAuthAccessTokenVO(String code) {
        String forObject = restTemplate.getForObject(ApiConstant.getAuthAccessTokenApi(code), String.class);
        AccessTokenVO accessTokenVo = JSON.parseObject(forObject, AccessTokenVO.class);
        log.info("code={}, 公众号授权结果：{}", code, forObject);
        if (accessTokenVo == null) {
            throw new ServiceException("获取微信公众号 access_token 失败！");
        }
        return accessTokenVo;
    }

    /**
     * 获取微信公众号授权access_token
     *
     * @param code
     * @return
     */
    public static String getPublicAccountAuthAccessToken(String code) {
        AccessTokenVO publicAccountAuthAccessTokenVO = getPublicAccountAuthAccessTokenVO(code);
        return publicAccountAuthAccessTokenVO.getAccess_token();
    }

    /**
     * 获取微信公众号openid
     *
     * @param code
     * @return
     */
    public static String getPublicAccountOpenId(String code) {
        AccessTokenVO publicAccountAuthAccessTokenVO = getPublicAccountAuthAccessTokenVO(code);
        return publicAccountAuthAccessTokenVO.getOpenid();
    }
}

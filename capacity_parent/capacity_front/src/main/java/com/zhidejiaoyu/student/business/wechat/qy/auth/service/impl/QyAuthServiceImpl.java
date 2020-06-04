package com.zhidejiaoyu.student.business.wechat.qy.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.constant.CookieConstant;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();

        ServerResponse<Object> fromCookie = getAuthInfoFromCookie(httpServletRequest);
        if (fromCookie != null) {
            return fromCookie;
        }

        String code = httpServletRequest.getParameter("code");
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
            String openid = userIdToOpenidVO.getOpenid();
            userInfoVO.setOpenId(openid);
        }

        addAuthCookie(userInfoVO);

        return ServerResponse.createBySuccess(userInfoVO);
    }

    /**
     * 从cookie中获取授权信息，如果已有授权信息，不再进行网页授权验证；
     * 如果没有授权信息，进行网页授权验证
     *
     * @param httpServletRequest
     * @return
     */
    public ServerResponse<Object> getAuthInfoFromCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        try {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), CookieConstant.QY_WX_USER_INFO)) {
                    String value = cookie.getValue();
                    UserInfoVO userInfoVO = JSONObject.parseObject(URLDecoder.decode(value, "utf-8"), UserInfoVO.class);
                    return ServerResponse.createBySuccess(userInfoVO);
                }
            }
        } catch (Exception e) {
            log.error("从cookie获取企业微信网页授权信息失败!", e);
        }
        return null;
    }

    /**
     * 将授权信息保存到cookie中，提升下次授权的速度
     *
     * @param userInfoVO
     */
    public void addAuthCookie(UserInfoVO userInfoVO) {
        try {
            Cookie cookie = new Cookie(CookieConstant.QY_WX_USER_INFO, URLEncoder.encode(JSONObject.toJSONString(userInfoVO), "utf-8"));
            // todo:测试的时候先设为0秒
            cookie.setMaxAge(0);
            HttpUtil.getResponse().addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            log.error("保存企业微信网页授权数据到cookie失败！", e);
        }

    }
}

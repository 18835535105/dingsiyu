package com.zhidejioayu.center.business.wechat.qy.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.constant.CookieConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.QyAuthMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.QyAuth;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejioayu.center.business.wechat.qy.auth.service.QyAuthService;
import com.zhidejioayu.center.business.wechat.qy.auth.vo.UserIdToOpenidVO;
import com.zhidejioayu.center.business.wechat.qy.auth.vo.UserInfoVO;
import com.zhidejioayu.center.business.wechat.qy.constant.QyApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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

    @Value("${qywx.redirect.login}")
    private String loginUrl;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private QyAuthMapper qyAuthMapper;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Override
    public void auth() {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();

        UserInfoVO userInfoVO = this.getUserInfoVO(httpServletRequest);

        String userId;
        String openId;
        if (StringUtil.isNotEmpty(userInfoVO.getUserId())) {
            userId = userInfoVO.getUserId();
            // 将userId转换为openid
            openId = userIdToOpenId(userInfoVO);
        } else {
            openId = userInfoVO.getOpenId();
            String userIdToOpenIdApi = QyApiConstant.getOpenidToUserIdApi();
            Map<String, String> params = new HashMap<>(16);
            params.put("openid", userInfoVO.getOpenId());
            String s = restTemplate.postForObject(userIdToOpenIdApi, params, String.class);
            UserIdToOpenidVO userIdToOpenidVO = JSONObject.parseObject(s, UserIdToOpenidVO.class);
            if (userIdToOpenidVO == null || userIdToOpenidVO.getErrcode() != 0) {
                log.error("企业微信openid转换userid失败！msg={}", s);
                throw new ServiceException("企业微信openid转换userid失败!msg=" + s);
            }
            userId = userIdToOpenidVO.getUserid();
        }

        String name = getName(userId);
        QyAuth qyAuth = qyAuthMapper.selectByOpenId(openId);
        if (qyAuth == null) {
            qyAuth = new QyAuth();
            qyAuth.setId(IdUtil.getId());
            qyAuth.setCreateTime(new Date());
            qyAuth.setName(name);
            qyAuth.setOpenid(openId);
            qyAuthMapper.insert(qyAuth);
        }
    }

    public UserInfoVO getUserInfoVO(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter("code");
        String userInfoApi = QyApiConstant.getUserInfoApi(code);
        String forObject = restTemplate.getForObject(userInfoApi, String.class);
        UserInfoVO userInfoVO = JSONObject.parseObject(forObject, UserInfoVO.class);
        if (userInfoVO == null || userInfoVO.getErrcode() != 0) {
            log.error("获取企业微信授权信息失败！msg={}", forObject);
            throw new ServiceException("获取企业微信授权信息失败!msg=" + forObject);
        }
        return userInfoVO;
    }

    @Override
    public String getRedirectUrl() {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        String openId = getOpenId(httpServletRequest);

        String url = httpServletRequest.getParameter("url");
        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectTeacherInfoByOpenid(openId);
        if (businessUserInfo == null) {
            QyAuth qyAuth = qyAuthMapper.selectByOpenId(openId);
            if (qyAuth == null) {
                // 未授权
                return loginUrl + "/#/?state=2";
            } else {
                // 待授权
                return loginUrl + "/#/?state=1";
            }
        }
        return url + "/#/?openId=" + openId;
    }

    public String getOpenId(HttpServletRequest httpServletRequest) {
        UserInfoVO userInfoVO = this.getUserInfoVO(httpServletRequest);

        String openId;
        if (StringUtil.isNotEmpty(userInfoVO.getUserId())) {
            // 将userId转换为openid
            openId = userIdToOpenId(userInfoVO);
        } else {
            openId = userInfoVO.getOpenId();
        }
        return openId;
    }

    @Override
    public int authState() {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), CookieConstant.QY_WX_USER_INFO)) {
                    // 已授权
                    return 0;
                }
            }
        }

        String openId = this.getOpenId(httpServletRequest);

        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectTeacherInfoByOpenid(openId);
        if (businessUserInfo != null) {
            Cookie cookie = new Cookie(CookieConstant.QY_WX_USER_INFO, "true");
            // todo：设置为0秒，便于调试，开发完成后修改为86400秒
            cookie.setMaxAge(0);
            HttpServletResponse response = HttpUtil.getResponse();
            response.addCookie(cookie);
            // 已授权
            return 0;
        }

        QyAuth qyAuth = qyAuthMapper.selectByOpenId(openId);
        if (qyAuth != null) {
            // 待授权
            return 1;
        }

        // 未授权
        return 2;
    }

    public String userIdToOpenId(UserInfoVO userInfoVO) {
        String userIdToOpenIdApi = QyApiConstant.getUserIdToOpenIdApi();
        Map<String, String> params = new HashMap<>(16);
        params.put("userid", userInfoVO.getUserId());
        String s = restTemplate.postForObject(userIdToOpenIdApi, params, String.class);
        UserIdToOpenidVO userIdToOpenidVO = JSONObject.parseObject(s, UserIdToOpenidVO.class);

        if (userIdToOpenidVO == null || userIdToOpenidVO.getErrcode() != 0) {
            log.error("企业微信userId转换openid失败！msg={}", s);
            throw new ServiceException("企业微信userId转换openid失败!msg=" + s);
        }
        return userIdToOpenidVO.getOpenid();
    }

    /**
     * 获取企业用户姓名
     *
     * @param userId
     * @return
     */
    public String getName(String userId) {
        String userByUserIdApi = QyApiConstant.getUserByUserIdApi(userId);
        String userInfoStr = restTemplate.getForObject(userByUserIdApi, String.class);
        JSONObject jsonObject = JSONObject.parseObject(userInfoStr);
        String name;
        if (jsonObject != null && jsonObject.getInteger("errcode") == 0) {
            name = jsonObject.getString("name");
        } else {
            log.error("企业微信读取成员失败！msg={}", userInfoStr);
            throw new ServiceException("企业微信读取成员失败！msg=" + userInfoStr);
        }
        return name;
    }
}

package com.zhidejiaoyu.student.business.wechat.publicaccount.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.MacIpUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.PublicAccountConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.service.PublicAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public ServerResponse<Object> authorization(HttpServletRequest request) {
        String code = request.getParameter("code");

        String forObject = restTemplate.getForObject(PublicAccountConstant.AUTHORIZATION_API_URL +
                        "appid=" + PublicAccountConstant.APP_ID +
                        "&secret=" + PublicAccountConstant.SECRET +
                        "&code=" + code +
                        "&grant_type=authorization_code",
                String.class);
        Object parseObject = JSON.parse(forObject);
        Map<String, Object> parseMap = parseObject == null ? null :  (Map<String, Object>) parseObject;
        if (parseMap == null || parseMap.get("openid") == null) {
            log.error("授权失败，授权响应信息：[{}]", parseMap);
            throw new ServiceException("微信公众号授权失败！");
        }
        return ServerResponse.createBySuccess(parseMap.get("openid"));
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
}

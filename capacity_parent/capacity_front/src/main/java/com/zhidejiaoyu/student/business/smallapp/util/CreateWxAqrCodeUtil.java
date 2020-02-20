package com.zhidejiaoyu.student.business.smallapp.util;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.student.business.smallapp.constant.SmallAppApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成微信小程序码工具类
 *
 * @author: wuchenxi
 * @date: 2020/2/20 09:48:48
 */
@Slf4j
@Component
public class CreateWxAqrCodeUtil {


    private static RestTemplate restTemplate;

    @Resource
    private RestTemplate restTemplateTmp;

    @PostConstruct
    public void init() {
        restTemplate = this.restTemplateTmp;
    }

    /**
     * 创建小程序码
     *
     * @param path  扫码进入的小程序页面路径
     * @param width 图片宽度
     * @return
     */
    public static String create(String path, Integer width) {
        String url = SmallAppApiConstant.CREATE_AQR_CODE + AccessTokenUtil.getAccessToken();

        try {
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("path", path);
            paramMap.put("width", width);
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, JSON.toJSONString(paramMap), String.class);
            return stringResponseEntity.getBody();
        } catch (RestClientException e) {
            log.error("生成小程序码失败！", e);
            throw new ServiceException("生成小程序码失败！");
        }
    }

}

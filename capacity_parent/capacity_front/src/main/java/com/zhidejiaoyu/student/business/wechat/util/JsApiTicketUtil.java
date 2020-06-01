package com.zhidejiaoyu.student.business.wechat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 获取jsapi_ticket工具类
 *
 * @author: wuchenxi
 * @date: 2020/5/29 10:58:58
 */
@Slf4j
@Component
public class JsApiTicketUtil {

    private static RedisTemplate<String, Object> redisTemplate;

    private static RestTemplate restTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplateTmp;

    @Resource
    private RestTemplate restTemplateTmp;

    @PostConstruct
    public void init() {
        redisTemplate = this.redisTemplateTmp;
        restTemplate = this.restTemplateTmp;
    }

    /**
     * 获取微信公众号的凭证
     *
     * @return
     */
    public static String getJsApiTicket() {
        String key = RedisKeysConst.PUBLIC_JS_API_TICKET;
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) {
            String publicAccountAccessToken = AccessTokenUtil.getPublicAccountAccessToken();
            String url = ApiConstant.getJSAPITicket(publicAccountAccessToken);
            String response = restTemplate.getForObject(url, String.class);

            JSONObject jsonObject = JSON.parseObject(response);

            if (jsonObject.getInteger("errcode") != 0) {
                log.error("获取ticket失败！");
                return "";
            }

            String ticket = jsonObject.getString("ticket");
            redisTemplate.opsForValue().set(key, ticket);
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
            return ticket;
        }

        return String.valueOf(o);
    }
}
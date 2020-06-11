package com.zhidejiaoyu.student.business.wechat.util;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ApiConstant;
import com.zhidejiaoyu.student.business.wechat.qy.constant.QyApiConstant;
import com.zhidejiaoyu.student.business.wechat.smallapp.constant.SmallAppApiConstant;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.AccessTokenVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: wuchenxi
 * @date: 2020/2/20 09:41:41
 */
@Component
public class AccessTokenUtil {

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
     * 获取小程序的 access_token
     *
     * @return
     */
    public static String getSmallAppAccessToken() {
        String key = RedisKeysConst.SMALL_APP_WECHAT_ACCESS_TOKEN;
        String accessToken = getAccessTokenFromRedis(key);
        if (accessToken == null) {
            AccessTokenVO accessTokenVo = restTemplate.getForObject(SmallAppApiConstant.GET_ACCESS_TOKEN_API, AccessTokenVO.class);
            if (accessTokenVo == null) {
                throw new ServiceException("获取微信小程序 access_token 失败！");
            }
            String token = accessTokenVo.getAccess_token();
            saveAccessTokenToRedis(token, key);
            return token;
        }
        return accessToken;
    }

    /**
     * 获取微信公众号基础支持的 access_token
     *
     * @return
     */
    public static String getPublicAccountAccessToken() {
        String key = RedisKeysConst.PUBLIC_ACCOUNT_WECHAT_ACCESS_TOKEN;
        String accessToken = getAccessTokenFromRedis(key);
        if (accessToken == null) {
            AccessTokenVO accessTokenVo = restTemplate.getForObject(ApiConstant.getAccessTokenApi(), AccessTokenVO.class);
            if (accessTokenVo == null) {
                throw new ServiceException("获取微信公众号 access_token 失败！");
            }
            String token = accessTokenVo.getAccess_token();
            saveAccessTokenToRedis(token, key);
            return token;
        }
        return accessToken;
    }

    /**
     * 获取企业微信 access_token
     *
     * @return
     */
    public static String getQyAccessToken() {
        String key = RedisKeysConst.QY_WECHAT_ACCESS_TOKEN;
        String accessToken = getAccessTokenFromRedis(key);
        if (accessToken == null) {
            AccessTokenVO accessTokenVo = restTemplate.getForObject(QyApiConstant.getAccessTokenApi(), AccessTokenVO.class);
            if (accessTokenVo == null) {
                throw new ServiceException("获取企业微信 access_token 失败！");
            }
            String token = accessTokenVo.getAccess_token();
            saveAccessTokenToRedis(token, key);
            return token;
        }
        return accessToken;
    }

    private static void saveAccessTokenToRedis(String accessToken, String key) {
        redisTemplate.opsForValue().set(key, accessToken);
        redisTemplate.expire(key, 2, TimeUnit.HOURS);
    }

    private static String getAccessTokenFromRedis(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o == null ? null : o.toString();
    }
}

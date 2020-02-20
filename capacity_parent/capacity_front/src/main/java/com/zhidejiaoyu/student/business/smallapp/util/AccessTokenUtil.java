package com.zhidejiaoyu.student.business.smallapp.util;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.student.business.smallapp.constant.SmallAppApiConstant;
import com.zhidejiaoyu.student.business.smallapp.vo.AccessTokenVO;
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
     * 获取公众号的 access_token
     *
     * @return
     */
    public static String getAccessToken() {
        String accessToken = getAccessTokenFromRedis();
        if (accessToken == null) {
            AccessTokenVO accessTokenVo = restTemplate.getForObject(SmallAppApiConstant.GET_ACCESS_TOKEN_API, AccessTokenVO.class);
            if (accessTokenVo == null) {
                throw new ServiceException("获取微信公众号 access_token 失败！");
            }
            saveAccessTokenToRedis(accessTokenVo.getAccess_token());
            return accessTokenVo.getAccess_token();
        }
        return accessToken;
    }

    private static void saveAccessTokenToRedis(String accessToken) {
        redisTemplate.opsForValue().set(RedisKeysConst.WECHAT_ACCESS_TOKEN, accessToken);
        redisTemplate.expire(RedisKeysConst.WECHAT_ACCESS_TOKEN, 2, TimeUnit.HOURS);
    }

    private static String getAccessTokenFromRedis() {
        Object o = redisTemplate.opsForValue().get(RedisKeysConst.WECHAT_ACCESS_TOKEN);
        return o == null ? null : o.toString();
    }
}

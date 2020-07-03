package com.zhidejioayu.center.business.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息缓存
 *
 * @author: wuchenxi
 * @date: 2020/6/29 09:55:55
 */
@Slf4j
@Component
public class UserInfoRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    /**
     * 判断用户信息是否存在中台服务器
     *
     * @param uuid
     * @return <ul>
     * <li>true:已在服务器中存在</li>
     * <li>false：在服务器中不存在</li>
     * </ul>
     */
    public Boolean userInfoIsExist(String uuid) {
        String key = RedisKeysConst.EXIST_IN_CENTER_SERVER + uuid;
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            return true;
        }

        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectByUserUuid(uuid);
        if (businessUserInfo == null) {
            return false;
        }

        this.saveUuidToServer(uuid);
        return true;
    }

    /**
     * 将用户信息缓存到redis中
     *
     * @param uuid
     */
    public void saveUserInfoToCenterServer(String uuid) {
        if (this.userInfoIsExist(uuid)) {
            return;
        }

        this.saveUuidToServer(uuid);
    }

    public void saveUuidToServer(String uuid) {
        String key = RedisKeysConst.EXIST_IN_CENTER_SERVER + uuid;
        redisTemplate.opsForValue().set(key, uuid);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }
}

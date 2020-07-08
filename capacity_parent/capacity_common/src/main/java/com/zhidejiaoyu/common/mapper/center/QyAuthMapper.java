package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.QyAuth;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 保存企业微信授权用户信息 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-08
 */
public interface QyAuthMapper extends BaseMapper<QyAuth> {

    /**
     * 根据openid查询用户信息
     *
     * @param openId
     * @return
     */
    QyAuth selectByOpenId(@Param("openId") String openId);
}

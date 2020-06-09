package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据openid查询用户信息
     *
     * @param openId
     * @return
     */
    SysUser selectByOpenId(@Param("openId") String openId);
}

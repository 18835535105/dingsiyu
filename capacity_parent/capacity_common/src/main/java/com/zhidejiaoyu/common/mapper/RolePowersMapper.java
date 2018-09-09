package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.RolePowersExample;
import com.zhidejiaoyu.common.pojo.RolePowersKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface RolePowersMapper {
    int countByExample(RolePowersExample example);

    int deleteByExample(RolePowersExample example);

    int deleteByPrimaryKey(RolePowersKey key);

    int insert(RolePowersKey record);

    int insertSelective(RolePowersKey record);

    List<RolePowersKey> selectByExample(RolePowersExample example);

    int updateByExampleSelective(@Param("record") RolePowersKey record, @Param("example") RolePowersExample example);

    int updateByExample(@Param("record") RolePowersKey record, @Param("example") RolePowersExample example);

    /**
     * 查询管理人员拥有的权限信息
     *
     * @param username
     * @return
     */
    Set<String> selectPowersByUsername(String username);
}
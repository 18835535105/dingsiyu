package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.AdminRolesExample;
import com.zhidejiaoyu.common.pojo.AdminRolesKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface AdminRolesMapper {
    int countByExample(AdminRolesExample example);

    int deleteByExample(AdminRolesExample example);

    int deleteByPrimaryKey(AdminRolesKey key);

    int insert(AdminRolesKey record);

    int insertSelective(AdminRolesKey record);

    List<AdminRolesKey> selectByExample(AdminRolesExample example);

    int updateByExampleSelective(@Param("record") AdminRolesKey record, @Param("example") AdminRolesExample example);

    int updateByExample(@Param("record") AdminRolesKey record, @Param("example") AdminRolesExample example);

    /**
     * 根据管理人员账号查找其对应的角色信息
     *
     * @param username
     * @return
     */
    Set<String> selectRoleNamesByUsername(String username);
}
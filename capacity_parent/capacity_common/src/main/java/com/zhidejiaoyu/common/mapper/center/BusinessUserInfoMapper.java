package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 中台存放学生、校管信息
 *
 * @author wuchenxi
 */
public interface BusinessUserInfoMapper extends BaseMapper<BusinessUserInfo> {

    /**
     * 通过openid查询学生信息
     *
     * @param openid
     * @return
     */
    BusinessUserInfo selectStudentInfoByOpenId(@Param("openid") String openid);

    /**
     * 通过account查询学生信息
     *
     * @param account
     * @return
     */
    BusinessUserInfo selectByAccount(@Param("account") String account);

    /**
     * 通过openid查询校管或者教师信息
     *
     * @param openId
     * @return
     */
    BusinessUserInfo selectTeacherInfoByOpenid(@Param("openId") String openId);

    /**
     * 根据用户uuid查询用户信息
     *
     * @param uuid
     * @return
     */
    BusinessUserInfo selectByUserUuid(@Param("uuid") String uuid);

    /**
     * 查询账号所属服务器名
     *
     * @param accountArray
     * @return <ul>
     * <li>account:学生账号</li>
     * <li>serverName:账号所属服务器名称</li>
     * </ul>
     */
    List<Map<String, String>> selectAccountAndServerName(@Param("accountArray") String[] accountArray);
}

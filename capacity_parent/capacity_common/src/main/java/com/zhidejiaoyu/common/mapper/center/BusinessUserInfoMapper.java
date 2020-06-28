package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import org.apache.ibatis.annotations.Param;

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
}
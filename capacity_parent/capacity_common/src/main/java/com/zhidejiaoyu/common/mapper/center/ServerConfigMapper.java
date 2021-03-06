package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ServerConfigMapper extends BaseMapper<ServerConfig> {

    /**
     * 通过openid查询学生服务器信息
     *
     * @param openid
     * @return
     */
    ServerConfig selectStudentServerByOpenid(@Param("openid") String openid);

    /**
     * 查询账号所属服务器信息
     *
     * @param account
     * @return
     */
    ServerConfig selectByAccount(@Param("account") String account);

    /**
     * 通过openid查询校长或教师服务器信息
     *
     * @param openid
     * @return
     */
    ServerConfig selectTeacherServerByOpenId(@Param("openid") String openid);

    /**
     * 通过uuid查询用户所在服务器信息
     *
     * @param uuid
     * @return
     */
    ServerConfig selectByUUID(@Param("uuid") String uuid);

    /**
     * 根据serverNo查询服务器信息
     *
     * @param serverNo
     * @return
     */
    ServerConfig selectByServerNo(String serverNo);

    /**
     * 查询所有服务器名称
     *
     * @return
     */
    @Select("select server_name from server_config")
    List<String> selectAllServerName();
}

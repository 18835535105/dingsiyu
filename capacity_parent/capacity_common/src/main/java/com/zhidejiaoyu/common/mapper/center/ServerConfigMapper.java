package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import org.apache.ibatis.annotations.Param;

public interface ServerConfigMapper extends BaseMapper<ServerConfig> {

    /**
     * 通过openid查询学生服务器信息
     *
     * @param openid
     * @return
     */
    ServerConfig selectStudentServerByOpenid(@Param("openid") String openid);
}

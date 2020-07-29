package com.dfdz.teacher.business.teacher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * @author: 76339
 * @date: 2020/7/29 10:41:41
 */
public interface TeacherService extends IService<SysUser> {
    /**
     * 根据uuid查询学管信息
     *
     * @param uuid
     * @return
     */
    ServerResponse<SysUser> getByUuid(String uuid);
}

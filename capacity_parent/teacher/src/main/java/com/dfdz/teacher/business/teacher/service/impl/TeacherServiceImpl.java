package com.dfdz.teacher.business.teacher.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.teacher.service.TeacherService;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wuchenxi
 * @date 2020-07-29 10:42:07
 */
@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements TeacherService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public ServerResponse<SysUser> getByUuid(String uuid) {
        SysUser sysUser = sysUserMapper.selectByUuid(uuid);
        if (sysUser == null) {
            log.warn("未查询到uuid={}人员信息", uuid);
            return ServerResponse.createByError(400, "未查询到数据！");
        }
        return ServerResponse.createBySuccess(sysUser);
    }
}

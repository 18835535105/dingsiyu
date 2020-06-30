package com.zhidejiaoyu.student.business.sysuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.student.business.sysuser.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 10:04:04
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Override
    public SysUser getUserByAccount(String account) {
        SysUser sysUser = sysUserMapper.selectByAccount(account);
        if (sysUser == null) {
            return new SysUser();
        }
        String password;
        if (account.contains("xg")) {
            password = teacherMapper.selectPasswordBySchoolAdminId(sysUser.getId());
        } else {
            password = teacherMapper.selectPasswordByTeacherId(sysUser.getId());
        }

        sysUser.setPassword(password);

        return sysUser;
    }
}

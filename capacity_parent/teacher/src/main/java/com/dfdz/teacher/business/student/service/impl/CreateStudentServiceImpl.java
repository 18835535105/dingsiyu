package com.dfdz.teacher.business.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.student.service.CreateStudentService;
import com.zhidejiaoyu.common.mapper.CanCreateStudentCountMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.pojo.CanCreateStudentCount;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @Date: 2019/11/26 09:57
 */
@Slf4j
@Service
public class CreateStudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements CreateStudentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private CanCreateStudentCountMapper canCreateStudentCountMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Object canCreateCount(String amdinUUID) {
        return getCanCreateCount(canCreateStudentCountMapper, studentMapper, sysUserMapper, amdinUUID);
    }

    /**
     * 获取当前校区还可生成的体验账号个数和配置中最大可生成体验账号个数
     *
     * @param canCreateStudentCountMapper
     * @param studentMapper
     * @return
     */
    public static CreateCount getCanCreateCount(CanCreateStudentCountMapper canCreateStudentCountMapper, StudentMapper studentMapper, SysUserMapper sysUserMapper, String amdinUUID) {
        Integer userId = sysUserMapper.selectByUuid(amdinUUID).getId();
        CanCreateStudentCount canCreateStudentCount = canCreateStudentCountMapper.selectBySchoolAdminId(userId);

        int configCount = canCreateStudentCount == null ? 30 : canCreateStudentCount.getCanCreateCount();
        int hadCount = studentMapper.countCanCreate(userId);
        int canCreateCount = configCount - hadCount;

        return CreateCount.builder().canCreateCount(Math.max(0, canCreateCount)).configCount(configCount).build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCount {
        /**
         * 配置的可生成个数
         */
        private Integer configCount;

        /**
         * 当前校区还可生成个数
         */
        private Integer canCreateCount;
    }
}

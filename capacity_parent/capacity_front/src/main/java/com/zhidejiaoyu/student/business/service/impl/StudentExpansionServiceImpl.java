package com.zhidejiaoyu.student.business.service.impl;


import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentExpansionMapper;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.student.business.service.StudentExpansionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 学生扩展内容表； 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Service
public class StudentExpansionServiceImpl extends BaseServiceImpl<SimpleStudentExpansionMapper, StudentExpansion> implements StudentExpansionService {

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Override
    public StudentExpansion getByStudentId(Long studentId) {
        return studentExpansionMapper.selectByStudentId(studentId);
    }
}

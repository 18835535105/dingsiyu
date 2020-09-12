package com.dfdz.teacher.business.joinSchool.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.joinSchool.service.JoinSchoolService;
import com.zhidejiaoyu.common.dto.joinSchool.JoinSchoolDto;
import com.zhidejiaoyu.common.mapper.JoinSchoolMapper;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JoinSchoolServiceImpl extends ServiceImpl<JoinSchoolMapper, JoinSchool> implements JoinSchoolService {

    @Resource
    private JoinSchoolMapper joinSchoolMapper;

    @Override
    public Object getList(JoinSchoolDto dto) {

        return null;
    }
}

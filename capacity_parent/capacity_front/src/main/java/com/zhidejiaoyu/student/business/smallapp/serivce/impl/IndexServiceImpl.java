package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.dto.BindAccountDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallAppIndexService")
public class IndexServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements IndexService {

    @Resource
    private StudentMapper studentMapper;

    @Override
    public ServerResponse<Object> bind(BindAccountDTO dto) {

        Student student = studentMapper.selectByAccount(dto.getAccount().trim());
        if (student == null || !Objects.equals(student.getPassword(), dto.getPassword().trim())) {
            return ServerResponse.createByError(400, "账号或密码输入错误！");
        }

        String openid = student.getOpenid();
        String splitCode = ",";
        if (StringUtils.isNotEmpty(openid) && openid.split(splitCode).length >= 3) {
            // 已绑定三个小程序，不能绑定
            return ServerResponse.createByError(400, "英雄学员你好，外部绑定已经超过限额，请联系基地导师解除原有绑定。");
        }

        if (StringUtils.isEmpty(openid)) {
            student.setOpenid(dto.getOpenid());
            studentMapper.updateById(student);
            return ServerResponse.createBySuccess();
        }

        if (openid.contains(dto.getOpenid())) {
            return ServerResponse.createBySuccess();
        }

        if (openid.endsWith(splitCode)) {
            openid = openid + dto.getOpenid();
        } else {
            openid = openid + splitCode + dto.getOpenid();
        }

        student.setOpenid(openid);
        studentMapper.updateById(student);

        return ServerResponse.createBySuccess();
    }
}

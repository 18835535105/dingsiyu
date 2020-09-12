package com.dfdz.teacher.business.joinSchool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.joinSchool.JoinSchoolDto;
import com.zhidejiaoyu.common.pojo.JoinSchool;

public interface JoinSchoolService extends IService<JoinSchool> {
    Object getList(JoinSchoolDto dto);
}

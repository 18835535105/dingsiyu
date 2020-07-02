package com.zhidejioayu.center.business.joinSchool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import com.zhidejiaoyu.common.vo.joinSchool.JoinSchoolDto;

import java.util.Map;

public interface JoinSchoolService extends IService<JoinSchool> {

    Map<String, Object> selSchoolByAddress(String address);

    /**
     * 添加学校信息
     * @param joinSchool
     * @return
     */
    Map<String,Object> addService(JoinSchool joinSchool);

    JoinSchool selectById(String joinSchoolId);

    Map<String,Object> selListJoinSchool(JoinSchoolDto joinSchoolDto);

    Object updateJoinSchool(String uuid, String joinSchoolId);

    JoinSchoolDto updateJoinSchoolState(String joinSchoolId);
}

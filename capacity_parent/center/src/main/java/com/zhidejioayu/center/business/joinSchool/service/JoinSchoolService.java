package com.zhidejioayu.center.business.joinSchool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.JoinSchool;

import java.util.Map;

public interface JoinSchoolService extends IService<JoinSchool> {

    Map<String, Object> selSchoolByAddress(String address);

    /**
     * 添加学校信息
     * @param joinSchool
     * @return
     */
    public Map<String,Object> addService(JoinSchool joinSchool);
}

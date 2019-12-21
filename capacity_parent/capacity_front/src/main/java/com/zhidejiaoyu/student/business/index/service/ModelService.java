package com.zhidejiaoyu.student.business.index.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2019/12/21 14:03:03
 */
public interface ModelService extends BaseService<Student> {

    /**
     * 获取各个模块开启情况
     *
     * @param session
     * @param type
     * @return
     */
    ServerResponse<Map<String, Boolean>> getModelStatus(HttpSession session, Integer type);
}

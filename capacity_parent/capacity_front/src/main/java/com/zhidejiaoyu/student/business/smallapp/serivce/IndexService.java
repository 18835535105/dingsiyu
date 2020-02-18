package com.zhidejiaoyu.student.business.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface IndexService extends BaseService<Student> {

    /**
     * 首页数据
     *
     * @return
     */
    ServerResponse<Object> index();

    /**
     * 补签
     *
     * @param date 补签日期
     * @return
     */
    ServerResponse<Object> replenish(String date);

    /**
     * 飞行记录（学习记录）
     *
     * @return
     */
    ServerResponse<Object> record();

    /**
     * 飞行状态
     *
     * @return
     */
    ServerResponse<Object> myState();

}

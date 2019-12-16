package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.Unit;

/**
 * @author: wuchenxi
 * @Date: 2019/10/18 09:38
 */
public interface UnitService extends BaseService<Unit> {

    /**
     * 通过 单元 id 查询课程 id
     *
     * @param unitId
     * @return
     */
    Long selectCourseIdById(Long unitId);
}

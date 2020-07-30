package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.StudentExpansion;

/**
 * <p>
 * 学生扩展内容表； 服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
public interface StudentExpansionService extends BaseService<StudentExpansion> {

    /**
     * 获取学生扩展信息
     *
     * @param studentId
     * @return
     */
    StudentExpansion getByStudentId(Long studentId);
}

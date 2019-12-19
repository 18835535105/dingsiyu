package com.zhidejiaoyu.student.business.test.service;

import com.zhidejiaoyu.common.pojo.SchoolTime;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.service.BaseService;

import java.util.List;

/**
 * 学前测试
 *
 * @author wuchenxi
 * @date 2019-12-19
 */
public interface BeforeStudyTestService extends BaseService<SchoolTime> {

    /**
     * 获取学前测试测试题目
     *
     * @return
     */
    ServerResponse<List<SubjectsVO>> getSubjects();
}

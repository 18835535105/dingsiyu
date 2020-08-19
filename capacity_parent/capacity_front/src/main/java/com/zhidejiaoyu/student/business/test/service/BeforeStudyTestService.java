package com.zhidejiaoyu.student.business.test.service;

import com.zhidejiaoyu.common.dto.testbeforestudy.SaveSubjectsDTO;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.service.BaseService;

import java.util.List;

/**
 * 摸底测试
 *
 * @author wuchenxi
 * @date 2019-12-19
 */
public interface BeforeStudyTestService extends BaseService<StudentStudyPlanNew> {

    /**
     * 获取学前测试测试题目
     *
     * @return
     */
    ServerResponse<List<SubjectsVO>> getSubjects();

    /**
     * 保存摸底测试记录
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> saveSubjects(SaveSubjectsDTO dto);

    ServerResponse<Object> fix();
}

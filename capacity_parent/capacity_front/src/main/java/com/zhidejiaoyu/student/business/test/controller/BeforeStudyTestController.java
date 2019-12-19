package com.zhidejiaoyu.student.business.test.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.test.service.BeforeStudyTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 学前测试
 *
 * @author wuchenxi
 * @date 2019-12-19
 */
@RestController
@RequestMapping("/beforeStudyTest")
public class BeforeStudyTestController extends BaseController {

    @Resource
    private BeforeStudyTestService beforeStudyTestService;

    /**
     * 获取学前测试测试题目
     *
     * @return
     */
    @GetMapping("/getSubjects")
    public ServerResponse<List<SubjectsVO>> getSubjects() {
        return beforeStudyTestService.getSubjects();
    }
}

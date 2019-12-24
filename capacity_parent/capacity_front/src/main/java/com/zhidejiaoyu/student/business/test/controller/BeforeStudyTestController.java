package com.zhidejiaoyu.student.business.test.controller;

import com.zhidejiaoyu.common.dto.testbeforestudy.SaveSubjectsDTO;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.test.service.BeforeStudyTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摸底测试
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

    /**
     * 保存摸底测试记录
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveSubjects")
    public ServerResponse<Object> saveSubjects(SaveSubjectsDTO dto) {
        this.transformToDto(dto);
        return beforeStudyTestService.saveSubjects(dto);
    }

    private void transformToDto(SaveSubjectsDTO dto) {
        String[] unitAndErrorCount = dto.getResult().split(";");

        dto.setResultList(Arrays.stream(unitAndErrorCount).map(value -> {
            SaveSubjectsDTO.Result result = new SaveSubjectsDTO.Result();

            String[] split = value.split(":");
            result.setUnitId(Long.parseLong(split[0]));
            result.setErrorCount(Integer.parseInt(split[1]));
            return result;
        }).collect(Collectors.toList()));
    }
}

package com.zhidejiaoyu.student.business.test.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.test.dto.SaveGoldTestDTO;
import com.zhidejiaoyu.student.business.test.service.GoldTestService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 金币试卷
 *
 * @author: wuchenxi
 * @date: 2020/4/17 09:08:08
 */
@Validated
@RestController
@RequestMapping("/goldTest")
public class GoldTestController {

    @Resource
    private GoldTestService goldTestService;

    /**
     * 获取金币试卷试题
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getTest")
    public ServerResponse<Object> getTest(Long unitId) {
        if (unitId == null) {
            throw new IllegalArgumentException("unitId can't be null!");
        }
        return goldTestService.getTest(unitId);
    }

    /**
     * 保存测试记录
     *
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/saveTest")
    public ServerResponse<Object> saveTest(@Valid SaveGoldTestDTO dto, BindingResult result) {
        return goldTestService.saveTest(dto);
    }
}

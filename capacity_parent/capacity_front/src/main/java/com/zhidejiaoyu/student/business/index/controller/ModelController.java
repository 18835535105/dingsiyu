package com.zhidejiaoyu.student.business.index.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.ModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 各学习模块开启情况
 *
 * @author: wuchenxi
 * @date: 2019/12/21 14:02:02
 */
@RestController
@RequestMapping("/login")
public class ModelController {

    @Resource
    private ModelService modelService;

    @GetMapping("/getModelStatus")
    public ServerResponse<Map<String, Boolean>> getModelStatus(HttpSession session, Integer type) {
        return modelService.getModelStatus(session, type);
    }
}

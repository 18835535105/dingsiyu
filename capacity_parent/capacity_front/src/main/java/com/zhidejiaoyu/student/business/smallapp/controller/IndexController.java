package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.smallapp.dto.BindAccountDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 首页数据接口
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Validated
@RequestMapping("/smallApp/index")
@RestController("smallAppController")
public class IndexController extends BaseController {

    @Resource
    private IndexService smallAppIndexService;

    /**
     * 首页数据
     *
     * @return
     */
    @GetMapping("/index")
    public ServerResponse<Object> index() {
        return smallAppIndexService.index();
    }

}

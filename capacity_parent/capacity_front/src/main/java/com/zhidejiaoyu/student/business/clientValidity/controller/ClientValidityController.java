package com.zhidejiaoyu.student.business.clientValidity.controller;

import com.zhidejiaoyu.student.business.clientValidity.service.ClientValidityService;
import com.zhidejiaoyu.student.business.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/clientValidity")
public class ClientValidityController extends BaseController {

    @Resource
    private ClientValidityService clientValidityService;

    /**
     * 获取客户端时间
     *
     * @return
     */
    @RequestMapping("/getClientValidity")
    public Object getClientValidity() {
        return clientValidityService.getClientValidity();
    }


}

package com.zhidejiaoyu.student.business.wechat.partner.controller;

import com.zhidejiaoyu.student.business.wechat.partner.service.PartnerService;
import com.zhidejiaoyu.student.business.wechat.partner.vo.SavePartnerVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/smallApp/partner")
public class PartnerController {

    @Resource
    private PartnerService partnerService;

    @RequestMapping("/savePartner")
    public Object savePartner(SavePartnerVo savePartnerVo){
        return  partnerService.savePartner(savePartnerVo);
    }




}

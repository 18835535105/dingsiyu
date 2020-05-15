package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.controller;

import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.PartnerService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.vo.SavePartnerDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/smallApp/partner")
public class PartnerController {

    @Resource
    private PartnerService partnerService;

    @RequestMapping("/savePartner")
    public Object savePartner(SavePartnerDto savePartnerDto){
        return  partnerService.savePartner(savePartnerDto);
    }




}

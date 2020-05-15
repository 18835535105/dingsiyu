package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.controller;

import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service.PartnerService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.dto.SavePartnerDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/smallApp/partner")
public class PartnerController {

    @Resource
    private PartnerService partnerService;

    @RequestMapping("/savePartner")
    public Object savePartner(SavePartnerDTO savePartnerDto) {
        return partnerService.savePartner(savePartnerDto);
    }


}

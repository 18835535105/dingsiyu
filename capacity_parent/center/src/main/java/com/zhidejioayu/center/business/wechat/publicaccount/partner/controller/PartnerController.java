package com.zhidejioayu.center.business.wechat.publicaccount.partner.controller;

import com.zhidejioayu.center.business.wechat.publicaccount.partner.dto.SavePartnerDTO;
import com.zhidejioayu.center.business.wechat.publicaccount.partner.service.PartnerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 微信公众号合伙人测试
 */
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

package com.zhidejioayu.center.business.wechat.publicaccount.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Partner;
import com.zhidejioayu.center.business.wechat.publicaccount.partner.dto.SavePartnerDTO;

public interface PartnerService extends IService<Partner> {

    Object savePartner(SavePartnerDTO savePartnerDto);
}

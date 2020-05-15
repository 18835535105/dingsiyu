package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service;

import com.zhidejiaoyu.common.pojo.Partner;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.dto.SavePartnerDTO;

public interface PartnerService extends BaseService<Partner> {
    Object savePartner(SavePartnerDTO savePartnerDto);
}

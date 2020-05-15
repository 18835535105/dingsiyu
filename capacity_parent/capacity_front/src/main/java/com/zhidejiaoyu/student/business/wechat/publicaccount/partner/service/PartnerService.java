package com.zhidejiaoyu.student.business.wechat.publicaccount.partner.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.partner.vo.SavePartnerDto;

public interface PartnerService extends BaseService<Student> {
    Object savePartner(SavePartnerDto savePartnerDto);
}

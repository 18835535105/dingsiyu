package com.zhidejiaoyu.student.business.wechat.partner.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.partner.vo.SavePartnerVo;

public interface PartnerService extends BaseService<Student> {
    Object savePartner(SavePartnerVo savePartnerVo);
}

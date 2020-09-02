package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.smallapp.dto.BindAccountDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: wuchenxi
 * @date: 2020/2/17 09:41:41
 */
public interface AuthorizationService extends BaseService<Student> {

    /**
     * 绑定队长账号
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> bind(BindAccountDTO dto);

    /**
     * 微信授权
     *
     * @return
     * @param request
     */
    ServerResponse<Object> authorization(HttpServletRequest request);

    ServerResponse unbundling(String openId);
}

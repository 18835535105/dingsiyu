package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: wuchenxi
 * @date: 2020/2/17 09:41:41
 */
public interface AuthorizationService extends IService<Student> {

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

    ServerResponse<Object> unbundling(String openId);
}

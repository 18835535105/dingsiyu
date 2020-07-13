package com.zhidejioayu.center.filter;

import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 企业微信过滤器，没有授权的用户引导到授权页面
 *
 * @author: wuchenxi
 * @date: 2020/7/9 10:35:35
 */
@WebFilter(urlPatterns = {"/wechat/qy/*"})
public class QyFilter implements Filter {

    @Value("${qywx.redirect.login}")
    private String loginUrl;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String openId = request.getParameter("openId");
        if (StringUtil.isEmpty(openId)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectTeacherInfoByOpenid(openId);
            if (businessUserInfo != null) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendRedirect(loginUrl);
            }
        }
    }

    @Override
    public void destroy() {

    }
}

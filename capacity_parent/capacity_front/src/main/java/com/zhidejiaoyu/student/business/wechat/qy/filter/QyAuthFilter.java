package com.zhidejiaoyu.student.business.wechat.qy.filter;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 14:42:42
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/qy/**", initParams = {@WebInitParam(name = "exclusions", value = "/qy/auth")})
public class QyAuthFilter implements Filter {

    @Resource
    private SysUserMapper sysUserMapper;

    @Value("${qywx.authLink}")
    private String authLink;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String openId = request.getParameter("openId");

        if (log.isDebugEnabled()) {
            log.debug("url={}", request.getRequestURI());
            log.debug("openId={}", openId);
        }

        if (StringUtil.isNotEmpty(openId)) {
            SysUser sysUser = sysUserMapper.selectByOpenId(openId);
            if (sysUser == null) {
                throw new ServiceException("当前用户还未绑定企业微信，即将前往登录页绑定！");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

package com.zhidejiaoyu.common.config;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author wuchenxi
 * @date 2018-12-06
 */

@WebServlet(urlPatterns="/druid/*", initParams={ @WebInitParam(name="allow",value=""),
@WebInitParam(name="loginUsername",value="admin"),
@WebInitParam(name="loginPassword",value="zhide2018"),
@WebInitParam(name="resetEnable",value="false")})
public class DruidStatViewServlet {
}

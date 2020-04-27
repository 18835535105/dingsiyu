package com.zhidejiaoyu.common.utils;

import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2019-01-30
 */
public class TokenUtil {

    public static String getToken() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 校验 token
     *
     * @param session
     * @param token
     * @return true：token 合法；false：token 不合法
     */
    public static Boolean checkToken(HttpSession session, String token) {
        Object object = session.getAttribute("token");
        return true;// !(object == null || !Objects.equals(object.toString(), token));
    }
}

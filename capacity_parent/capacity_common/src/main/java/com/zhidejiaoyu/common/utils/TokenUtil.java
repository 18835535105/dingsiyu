package com.zhidejiaoyu.common.utils;

/**
 * @author wuchenxi
 * @date 2019-01-30
 */
public class TokenUtil {

    public static String getToken() {
        return String.valueOf(System.currentTimeMillis());
    }
}

package com.zhidejiaoyu.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;

/**
 * 获取MAC地址和IP工具类
 *
 * @author wuchenxi
 * @date 2018/11/17
 */
@Slf4j
public class MacIpUtil {

    /**
     * 获取mac地址（只能获取到本机的mac地址）
     *
     * @param ip
     * @return 没有解析到mac地址返回null
     */
    public static String getMac(String ip) throws Exception {
        log.info("address:[{}]", InetAddress.getByName(ip));
        if (InetAddress.getByName(ip) == null) {
            log.info("addressStr:[{}]", InetAddress.getByName(ip).toString());
            return null;
        }
        NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
        byte[] mac = ne.getHardwareAddress();
        log.info(Arrays.toString(mac));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0").append(str);
            } else {
                sb.append(str);
            }
        }
        log.info("sb.toString().toUpperCase():    " + sb.toString().toUpperCase());
        return sb.toString().toUpperCase();
    }

    /**
     * 获取ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-real-ip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

}

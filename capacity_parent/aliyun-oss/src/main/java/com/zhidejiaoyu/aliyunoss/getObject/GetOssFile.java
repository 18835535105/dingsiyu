package com.zhidejiaoyu.aliyunoss.getObject;

import com.aliyun.oss.OSS;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;

/**
 * 获取文件 url
 *
 * @author wuchenxi
 * @date 2019-06-18
 */
@Slf4j
@Component
public class GetOssFile {

    private static OSS client;

    /**
     * 用于判断当前请求是不是客户端发起的请求（session 中含有该字段说明是桌面客户端发起的请求，否则说明是浏览器发起的请求）
     */
    public static final String DESKTOP = "desktop";

    @Resource
    private OSS ossClient;

    @PostConstruct
    public void init() {
        client = this.ossClient;
    }


    /**
     * 获取文件 url 地址,公共权限
     *
     * @param objectName 文件路径+文件名
     * @return
     */
    public static String getPublicObjectUrl(String objectName) {
        if (objectName != null && objectName.contains(AliyunInfoConst.host)) {
            objectName = objectName.replace(AliyunInfoConst.host, "");
        }
        return AliyunInfoConst.host + objectName;
    }

    /**
     * 获取文件 url 地址,私有权限
     */
    public static String getPrivateObjectUrl(String objectName) {

        // 设置URL过期时间为1小时。
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = client.generatePresignedUrl(AliyunInfoConst.bucketName, objectName, expiration);

        return url.toString();
    }
}

package com.zhidejiaoyu.aliyunoss;

import com.aliyun.oss.OSS;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 判断oss文件是否存在
 *
 * @author: wuchenxi
 * @date: 2020/6/3 14:12:12
 */
@Slf4j
@Component
public class DoesObjectExist {

    private static OSS client;

    @Resource
    private OSS ossClient;

    @PostConstruct
    public void init() {
        client = this.ossClient;
    }

    /**
     * 判断oss中是否有指定文件
     *
     * @param objectName 文件路径，不能以/开头
     * @return
     */
    public static boolean doesObjectExist(String objectName) {
        if (StringUtils.isEmpty(objectName)) {
            return false;
        }
        if (objectName.contains(AliyunInfoConst.host)) {
            objectName = objectName.replace(AliyunInfoConst.host, "");
        }
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return client.doesObjectExist(AliyunInfoConst.bucketName, objectName);
    }

}

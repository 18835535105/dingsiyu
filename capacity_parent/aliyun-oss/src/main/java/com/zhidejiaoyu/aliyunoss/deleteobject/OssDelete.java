package com.zhidejiaoyu.aliyunoss.deleteobject;

import com.aliyun.oss.OSS;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/2 09:36:36
 */
@Slf4j
@Component
public class OssDelete {

    private static OSS client;

    @Resource
    private OSS ossClient;

    @PostConstruct
    public void init() {
        client = this.ossClient;
    }

    /**
     * 删除单个文件
     *
     * @param objectName 文件路径（文件夹+文件名， imgs/qr-code/1111.png）
     */
    public static void deleteObject(String objectName) {
        client.deleteObject(AliyunInfoConst.bucketName, objectName);
        log.info("删除oss文件：{}成功！", objectName);
    }
}

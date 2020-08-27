package com.zhidejiaoyu.aliyunoss.deleteobject;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 批量删除文件
     *
     * @param fileNames
     */
    public static void deleteObjects(List<String> fileNames) {
        try {
            client.deleteObjects(new DeleteObjectsRequest(AliyunInfoConst.bucketName).withKeys(fileNames));
        } catch (OSSException | ClientException e) {
            log.error("批量删除 oss 文件[{}]失败！", fileNames.toString(), e);
            e.printStackTrace();
        }
    }
}

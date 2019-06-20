package com.zhidejiaoyu.aliyunoss.putObject;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.aliyun.oss.OSS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件简单上传
 *
 * @author wuchenxi
 * @date 2019-06-18
 */
@Slf4j
@Component
public class OssUpload {

    private static OSS client;

    @Resource
    private OSS ossClient;

    @PostConstruct
    public void init() {
        client = this.ossClient;
    }

    /**
     * 单文件上传
     *
     * @param file
     * @param dir   oss 存放文件的路径
     * @param fileName
     * @return
     */
    public static String upload(MultipartFile file, String dir, String fileName) {
        fileName = getFileName(file, fileName);

        try {
            client.putObject(AliyunInfoConst.bucketName, dir + fileName, file.getInputStream());
        } catch (Exception e) {
            log.error("文件上传到 OSS 失败！", e);
            return null;
        }
        return dir + fileName;
    }

    /**
     * 批量上传
     *
     * @param files
     * @param dir
     * @return
     */
    public static List<String> uploadFiles(MultipartFile[] files, String dir, String fileName) {
        List<String> fileNameList = new ArrayList<>(files.length);
        try {
            for (MultipartFile file : files) {
                fileName = getFileName(file, fileName);
                client.putObject(AliyunInfoConst.bucketName, dir + fileName, file.getInputStream());
                fileNameList.add(dir + fileName);
            }
        } catch (Exception e) {
            log.error("文件上传到 OSS 失败！", e);
            return null;
        }
        return fileNameList;
    }

    /**
     * 获取文件名
     *
     * @param file
     * @param fileName
     * @return
     */
    private static String getFileName(MultipartFile file, String fileName) {
        // 文件后缀名
        String suffix = "";
        if (Objects.requireNonNull(file.getOriginalFilename()).length() > 0) {
            suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        }

        if(fileName != null && fileName.trim().length() > 0) {
            fileName = fileName + suffix;
        }else {
            fileName = UUID.randomUUID() + suffix;
        }
        return fileName;
    }


}

package aliyunoss.putObject;

import aliyunoss.common.AliyunInfoConst;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
public class OssUpload {

    private static OSS client;

    static {
        // 创建ClientConfiguration实例，按照您的需要修改默认参数。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 开启支持CNAME。CNAME是指将自定义域名绑定到存储空间上。
        clientBuilderConfiguration.setSupportCname(true);
        client = new OSSClientBuilder().build(AliyunInfoConst.endpoint, AliyunInfoConst.accessKeyId, AliyunInfoConst.accessKeySecret, clientBuilderConfiguration);
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
        } catch (IOException e) {
            log.error("文件上传到 OSS 失败！", e);
            return null;
        } finally {
            if (client != null) {
                client.shutdown();
            }
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
        for (MultipartFile file : files) {
            fileNameList.add(OssUpload.upload(file, dir, fileName));
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

package aliyunoss.getObject;

import aliyunoss.common.AliyunInfoConst;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Date;

/**
 * 获取文件 url
 *
 * @author wuchenxi
 * @date 2019-06-18
 */
@Slf4j
public class GetOssFile {

    /**
     * 获取文件 url 地址
     *
     * @param objectName    文件路径+文件名
     * @return
     */
    public static String getUrl(String objectName) {

        // 创建ClientConfiguration实例，按照您的需要修改默认参数。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 开启支持CNAME。CNAME是指将自定义域名绑定到存储空间上。
        clientBuilderConfiguration.setSupportCname(true);

        // 创建OSSClient实例。
        OSS oss = new OSSClientBuilder().build(AliyunInfoConst.endpoint, AliyunInfoConst.accessKeyId, AliyunInfoConst.accessKeySecret, clientBuilderConfiguration);

        // 设置URL过期时间为1小时。
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = oss.generatePresignedUrl(AliyunInfoConst.bucketName, objectName, expiration);

        // 关闭OSSClient。
        oss.shutdown();
        return url.toString();
    }
}

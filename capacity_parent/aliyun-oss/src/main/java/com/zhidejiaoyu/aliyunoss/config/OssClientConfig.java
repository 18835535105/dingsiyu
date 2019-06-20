package com.zhidejiaoyu.aliyunoss.config;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 oss 配置文件
 * <br>
 * 参考 <a href="https://help.aliyun.com/document_detail/32010.html?spm=a2c4g.11186623.6.734.426628baLhAMmb">初始化</a>
 *
 * @author wuchenxi
 * @date 2019-06-20
 */
@Configuration
public class OssClientConfig {

    @Bean(name = "ossClient")
    public OSS ossClient() {
        // 创建ClientConfiguration实例，按照您的需要修改默认参数。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 开启支持CNAME。CNAME是指将自定义域名绑定到存储空间上。
        clientBuilderConfiguration.setSupportCname(true);
        clientBuilderConfiguration.setProtocol(Protocol.HTTPS);
        clientBuilderConfiguration.setMaxConnections(200);
        return new OSSClientBuilder().build(AliyunInfoConst.endpoint, AliyunInfoConst.accessKeyId, AliyunInfoConst.accessKeySecret, clientBuilderConfiguration);
    }
}

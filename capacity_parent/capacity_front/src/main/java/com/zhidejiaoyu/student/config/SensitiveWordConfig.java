package com.zhidejiaoyu.student.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 将自定义的properties文件在springboot启动时交由容器管理
 *
 * @author wuchenxi
 * @date 2018/8/14
 */
@Component
@PropertySource("classpath:properties/sensitive-word.properties")
@Data
public class SensitiveWordConfig {

    @Value("${word}")
    private String word;

}

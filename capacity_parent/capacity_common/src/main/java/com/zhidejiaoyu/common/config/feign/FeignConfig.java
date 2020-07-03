package com.zhidejiaoyu.common.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 08:36:36
 */
@Component
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 在header中增加feign字符，请求其他微服务放行
        requestTemplate.header("feign", "true");
    }
}

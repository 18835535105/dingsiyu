package com.zhidejiaoyu.common.config.feign;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 18:00:00
 */
//@Configuration
public class FeignFormEncoderConfig {

//    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}

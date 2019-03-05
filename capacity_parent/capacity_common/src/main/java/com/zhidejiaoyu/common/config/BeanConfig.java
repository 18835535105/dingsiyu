package com.zhidejiaoyu.common.config;

import com.zhidejiaoyu.common.utils.http.FtpUtil;
import com.zhidejiaoyu.common.utils.http.HttpClientUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * bean管理工具类
 * 
 * @author wuchenxi
 * @date 2018年4月24日 上午10:35:09
 *
 */
@Configuration
public class BeanConfig {

	/**
	 * 获取百度语音合成api链接地址
	 * 
	 * @return
	 */
	@Bean
	public BaiduSpeak baiduSpeak() {
		return new BaiduSpeak();
	}

	/**
	 * 获取有道翻译工具类
	 * 
	 * @return
	 */
	@Bean
	public YouDaoTranslate youDaoTranslate() {
		return new YouDaoTranslate();
	}

	/**
	 * 获取httpClient工具类
	 * 
	 * @return
	 */
	@Bean
	public HttpClientUtil httpClientUtil() {
		return new HttpClientUtil();
	}

	/**
	 * 获取FtpUtil工具类
	 */
	@Bean
	public FtpUtil ftpUtil() {
		return new FtpUtil();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

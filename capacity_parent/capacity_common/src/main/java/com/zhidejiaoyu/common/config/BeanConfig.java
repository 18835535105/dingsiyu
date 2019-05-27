package com.zhidejiaoyu.common.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhidejiaoyu.common.utils.http.FtpUtil;
import com.zhidejiaoyu.common.utils.http.HttpClientUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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

	@Bean
	public ExecutorService executorService() {
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
				.setNameFormat("zdjy-pool-%d").build();
		return new ThreadPoolExecutor(4, 10,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(50), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
	}

	@Bean
	public ServletRegistrationBean druidServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setServlet(new StatViewServlet());
		servletRegistrationBean.addUrlMappings("/druid/*");
		Map<String, String> initParameters = new HashMap<>(16);
		initParameters.put("resetEnable", "false");
		initParameters.put("allow", "");
		initParameters.put("loginUsername", "admin");
		initParameters.put("loginPassword", "zhide2018");
		initParameters.put("deny", "");
		//如果某个ip同时存在，deny优先于allow
		servletRegistrationBean.setInitParameters(initParameters);
		return servletRegistrationBean;
	}

	/**
	 * druid 为druidStatPointcut添加拦截
	 *
	 * @return
	 */
	@Bean
	public Advisor druidStatAdvisor() {
		return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
	}

	/**
	 * druid数据库连接池监控
	 */
	@Bean
	public DruidStatInterceptor druidStatInterceptor() {
		return new DruidStatInterceptor();
	}

	@Bean
	public JdkRegexpMethodPointcut druidStatPointcut() {
		JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
		String patterns = "com.zhidejiaoyu.student.*.service.*";
		//可以set多个
		druidStatPointcut.setPatterns(patterns);
		return druidStatPointcut;
	}

}

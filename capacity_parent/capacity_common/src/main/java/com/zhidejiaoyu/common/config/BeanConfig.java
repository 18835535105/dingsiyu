package com.zhidejiaoyu.common.config;

import com.alibaba.csp.ahas.shaded.com.alibaba.acm.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
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

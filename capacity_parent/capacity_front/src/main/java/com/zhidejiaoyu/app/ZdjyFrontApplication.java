package com.zhidejiaoyu.app;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import static org.springframework.boot.SpringApplication.run;

/**
 * 启动
 *
 * @EnableScheduling 开启定时任务
 * @EnableRedisHttpSession(redisNamespace = "spring:session:student") redis中存储学生session的命名空间
 */
@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@ServletComponentScan
@EnableAutoConfiguration
@ComponentScan("com.zhidejiaoyu")
@MapperScan(basePackages = {"com.zhidejiaoyu.common.mapper"})
@EnableRedisHttpSession(redisNamespace = "spring:session:student")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class ZdjyFrontApplication {


    public static void main(String[] args) {

        run(ZdjyFrontApplication.class, args);
        log.info("application is started");
    }
}
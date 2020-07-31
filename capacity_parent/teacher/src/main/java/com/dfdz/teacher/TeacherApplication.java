package com.dfdz.teacher;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@ServletComponentScan
@MapperScan(basePackages = {"com.zhidejiaoyu.common.mapper"})
@EnableRedisHttpSession(redisNamespace = "spring:session:teacher")
@ComponentScan(basePackages = {"com.zhidejiaoyu.common.config"}, basePackageClasses = {ControllerLogAop.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class TeacherApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeacherApplication.class, args);
        log.info("teacher application started!");
    }

}

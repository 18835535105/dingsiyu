package com.dfdz.teacher;

import com.zhidejiaoyu.common.constant.ServerNoConstant;
import com.zhidejiaoyu.common.mapper.SysConfigMapper;
import com.zhidejiaoyu.common.pojo.SysConfig;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@ServletComponentScan
@MapperScan(basePackages = {"com.zhidejiaoyu.common.mapper"})
@EnableRedisHttpSession(redisNamespace = "spring:session:teacher")
@ComponentScan(basePackages = {"com.zhidejiaoyu.common.config", "com.zhidejiaoyu.aliyunoss", "com.zhidejiaoyu.common.rank"}, basePackageClasses = {ControllerLogAop.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class TeacherApplication {

    @Resource
    private SysConfigMapper sysConfigMapper;

    public static void main(String[] args) {
        SpringApplication.run(TeacherApplication.class, args);
        log.info("teacher application started!");
    }

    @PostConstruct
    public void initServerNo() {
        if (StringUtil.isEmpty(ServerNoConstant.SERVER_NO)) {
            SysConfig sysConfig = sysConfigMapper.selectByExplain("服务器编号");
            if (sysConfig == null) {
                log.info("当前服务器还没有编号，正在生成编号...");
                sysConfig = new SysConfig();
                sysConfig.setContent(IdUtil.getId());
                sysConfig.setExplain("当前服务器编号，用于区分各个不同服务器，禁止手动变更！");
                sysConfig.setUpdateTime(new Date());
                sysConfigMapper.insert(sysConfig);
                log.info("服务器编号生成成功，serverNo={}", sysConfig.getContent());
            } else {
                ServerNoConstant.SERVER_NO = sysConfig.getContent();
                log.info("当前服务器已有编号，serverNo={}", sysConfig.getContent());
            }
        }
    }

}

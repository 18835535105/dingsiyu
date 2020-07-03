package com.dfdz.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author: wuchenxi
 * @date: 2020/7/3 16:48:48
 */
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPassword("GYPEORBAFFDLKQYW");
        javaMailSender.setHost("smtp.163.com");
        javaMailSender.setPort(465);
        javaMailSender.setUsername("duofenduizhang@163.com");
        javaMailSender.setDefaultEncoding("UTF-8");

        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        javaMailSender.setJavaMailProperties(javaMailProperties);

        return javaMailSender;
    }
}

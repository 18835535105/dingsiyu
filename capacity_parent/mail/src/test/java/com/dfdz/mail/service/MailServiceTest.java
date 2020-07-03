package com.dfdz.mail.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/7/3 17:01:01
 */
@SpringBootTest
public class MailServiceTest {

    @Resource
    private JavaMailSender javaMailSender;

    @Test
    void sendSimpleMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("duofenduizhang@163.com");
        message.setTo(new String[]{"763396567@qq.com"});
        message.setSubject("hello");
        message.setText("hello");

        javaMailSender.send(message);
    }

    @Test
    void sendAttachmentsMail() {
    }
}

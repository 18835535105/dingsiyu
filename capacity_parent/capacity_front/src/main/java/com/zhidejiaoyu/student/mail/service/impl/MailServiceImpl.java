package com.zhidejiaoyu.student.mail.service.impl;

import com.zhidejiaoyu.student.mail.Mail;
import com.zhidejiaoyu.student.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Arrays;

/**
 * @author: wuchenxi
 * @Date: 2019/11/6 16:31
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.fromMail.addr}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    //后加的防止题目过长并且进行全局定义
    static {
        System.setProperty("mail.mime.splitlongparameters","false");
        System.setProperty("mail.mime.charset","UTF-8");
    }

    @Override
    public void sendSimpleMail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(mail.getTo());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());

        try {
            javaMailSender.send(message);
            log.info("向 {} 发送邮件成功！", Arrays.toString(mail.getTo()));
        } catch (Exception e) {
            log.error("向 {} 发送邮件失败！", mail.getTo(), e);
        }
    }

    @Override
    public void sendAttachmentsMail(Mail mail) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);

            String fileName = mail.getFilePath().substring(mail.getFilePath().lastIndexOf(File.separator) + 1);
            helper.addAttachment(fileName, new File(mail.getFilePath()));

            javaMailSender.send(message);
            log.info("向 {} 发送邮件成功！", Arrays.toString(mail.getTo()));
        } catch (MessagingException e) {
            log.error("向 {} 发送邮件失败！", mail.getTo(), e);
        }
    }
}

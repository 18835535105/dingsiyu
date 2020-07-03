package com.dfdz.mail.controller;

import com.dfdz.mail.Mail;
import com.dfdz.mail.service.MailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/7/3 16:42:42
 */
@RestController
@RequestMapping("/mail")
public class MailController {

    @Resource
    private MailService mailService;

    /**
     * 发送普通文本邮件
     *
     * @param mail
     */
    public void sendSimpleMail(Mail mail) {
        mailService.sendSimpleMail(mail);
    }

    /**
     * 发送带有附件的邮件
     *
     * @param mail
     */
    void sendAttachmentsMail(Mail mail) {
        mailService.sendAttachmentsMail(mail);
    }
}

package com.zhidejiaoyu.student.business.mail.service;

import com.zhidejiaoyu.student.business.mail.Mail;

/**
 * @author: wuchenxi
 * @Date: 2019/11/6 16:31
 */
public interface MailService {
    /**
     * 发送普通文本邮件
     *
     * @param mail
     */
    void sendSimpleMail(Mail mail);

    /**
     * 发送带有附件的邮件
     *
     * @param mail
     */
    void sendAttachmentsMail(Mail mail);
}

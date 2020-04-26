package com.zhidejiaoyu.student.business.mail.service;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.student.business.mail.Mail;
import org.junit.Test;

import javax.annotation.Resource;

public class MailServiceTest extends BaseTest {

    @Resource
    private MailService mailService;

    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail(Mail.builder()
                .to(new String[]{"763396567@qq.com"})
                .filePath(FileConstant.TMP_EXCEL + "充课卡详情表1573024991962.xlsx")
                .subject("测试")
                .content("测试")
                .build());
    }

    @Test
    public void sendAttachmentsMail() {
        mailService.sendAttachmentsMail(Mail.builder()
                .to(new String[]{"763396567@qq.com"})
                .filePath(FileConstant.TMP_EXCEL + "充课卡详情表1573024991962.xlsx")
                .subject("测试")
                .content("测试")
                .build());
    }
}

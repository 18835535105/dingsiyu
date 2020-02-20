package com.zhidejiaoyu.student.business.smallapp.util;

import com.zhidejiaoyu.student.business.smallapp.dto.GetUnlimitedQRCodeDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/2/20 11:18:18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateWxQrCodeUtilTest {

    @Test
    public void create() {
        CreateWxQrCodeUtil.createQRCode("111", 120);
    }

    @Test
    public void getUnlimited() {
        CreateWxQrCodeUtil.getUnlimited(GetUnlimitedQRCodeDTO.builder()
                .scene("?code=111")
                .build());
    }
}

package com.zhidejiaoyu.student.business.wechat.smallapp.util;

import com.zhidejiaoyu.student.business.wechat.smallapp.dto.GetUnlimitedQRCodeDTO;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.SmallProgramTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/2/20 11:18:18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateWxQrCodeUtilTest {


    @Resource
    private SmallProgramTestService smallProgramTestService;

    @Test
    public void getWriteSpace(){
        Map<String,Object> map=new HashMap<>();
        smallProgramTestService.getWriteSpace(map,"");
        System.out.println(map);
    }

    @Test
    public void create() {
//        CreateWxQrCodeUtil.createQRCode("111", 120);
    }

    @Test
    public void getUnlimited() {
        CreateWxQrCodeUtil.getUnlimited(GetUnlimitedQRCodeDTO.builder()
                .scene("?code=111")
                .build());
    }
}

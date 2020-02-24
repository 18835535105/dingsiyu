package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.student.business.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import com.zhidejiaoyu.student.business.smallapp.util.CreateWxQrCodeUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/smallApp/test")
public class AppletTestController {

    @Resource
    private SmallProgramTestService smallProgramTestService;


    @RequestMapping("/getTest")
    public Object getTest(HttpSession session, String openId) {
        return smallProgramTestService.getTest(session, openId);
    }

    @RequestMapping("/saveTest")
    public Object saveTest(Integer point, HttpSession session, String openId) {
        return smallProgramTestService.saveTest(point, session, openId);
    }

    @RequestMapping("/getQRCode")
    public Object getQRCode(String openId) {
        byte[] qrCode = CreateWxQrCodeUtil.createQRCode(GetLimitQRCodeDTO.builder()
                .path("openid=" + openId)
                .build());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
    }


}

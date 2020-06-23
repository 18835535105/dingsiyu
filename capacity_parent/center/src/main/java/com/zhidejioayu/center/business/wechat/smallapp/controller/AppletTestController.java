package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.deleteobject.OssDelete;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.SmallProgramTestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/wechat/smallApp/test")
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
    public Object getQRCode(String openId, String weChatName, String weChatImgUrl) {
        return smallProgramTestService.getQRCode(openId, weChatName, weChatImgUrl);
    }

    /**
     * 删除小程序码
     *
     * @param fileUrl 小程序码url路径
     * @return
     */
    @PostMapping("/deleteQRCode")
    public Object deleteQrCode(String fileUrl) {
        String objectName = fileUrl.replace(AliyunInfoConst.host, "");
        OssDelete.deleteObject(objectName);
        return ServerResponse.createBySuccess();
    }

}

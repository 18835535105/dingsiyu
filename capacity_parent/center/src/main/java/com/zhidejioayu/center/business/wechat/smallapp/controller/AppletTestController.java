package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.deleteobject.OssDelete;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.config.RedisConfig;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.SmallProgramTestService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/wechat/smallApp/test")
public class AppletTestController {

    @Resource
    private SmallProgramTestService smallProgramTestService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
     * 保存图片
     * @return
     */
    @PostMapping("/saveCodeImg")
    public Object getCodeImg(String openId, MultipartFile file){
        String upload =null;
        try {
            upload = OssUpload.upload(file, FileConstant.CODE_IMG, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(upload!=null){
            redisTemplate.opsForHash().put(RedisKeysConst.CODE_IMG, openId, upload);
        }else{
           return ServerResponse.createByError(400,"图片保存失败");
        }
        return ServerResponse.createBySuccess();
    }

    @PostMapping("/getCodeImg")
    public Object getCodeImg(String openId){
        Object o = redisTemplate.opsForHash().get(RedisKeysConst.CODE_IMG, openId);
        String img= GetOssFile.getPublicObjectUrl(o.toString());
        return ServerResponse.createBySuccess(img);
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

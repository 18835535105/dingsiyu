package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.SmallProgramTestService;
import com.zhidejioayu.center.business.wechat.smallapp.util.CreateWxQrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Slf4j
@Service("smallProgramTestService")
public class SmallProgramTestServiceImpl extends ServiceImpl<StudentMapper, Student> implements SmallProgramTestService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private WeChatMapper weChatMapper;

    @Override
    public Object getTest(HttpSession session, String openId) {
        ServerConfig serverConfig = getServerConfig(openId);

        String forObject = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/test/getTest?openId=" + openId, String.class);
        return JSONObject.parseObject(forObject, ServerResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GoldChangeAnnotation
    public Object saveTest(Integer point, HttpSession session, String openId) {

        ServerConfig serverConfig = getServerConfig(openId);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>(16);
        params.add("point", point);
        params.add("openId", openId);

        String forObject = restTemplate.postForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/test/saveTest", params, String.class);
        return JSONObject.parseObject(forObject, ServerResponse.class);
    }

    @Override
    public Object getQRCode(String openId, String weChatName, String weChatImgUrl) {
        WeChat weChat = weChatMapper.selectByOpenId(openId);
        if (weChat == null) {
            weChat = new WeChat();
            weChat.setOpenId(openId);
            weChat.setWeChatImgUrl(weChatImgUrl);
            weChat.setWeChatName(weChatName);
            weChatMapper.insert(weChat);
        } else {
            weChat.setWeChatName(weChatName);
            weChat.setWeChatImgUrl(weChatImgUrl);
            weChatMapper.updateById(weChat);
        }

        byte[] qrCode = CreateWxQrCodeUtil.createQRCode(GetLimitQRCodeDTO.builder()
                .path("pages/support2/support?scene=" + URLEncoder.encode("openid=" + openId))
                .build());
        String fileName = System.currentTimeMillis() + ".png";
        String pathname = FileConstant.QR_CODE + fileName;
        File file = new File(pathname);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(qrCode);
            outputStream.flush();
        } catch (Exception e) {
            log.error("生成小程序码出错！", e);
            throw new ServiceException("生成小程序码出错！");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            boolean b = OssUpload.uploadWithInputStream(inputStream, FileConstant.QR_CODE_OSS, fileName);
            if (b) {
                log.info("小程序码生成成功！");
                file.delete();
                return ServerResponse.createBySuccess(GetOssFile.getPublicObjectUrl(FileConstant.QR_CODE_OSS + fileName));
            }
        } catch (Exception e) {
            log.error("小程序码上传OSS失败！", e);
            throw new ServiceException("小程序码上传OSS失败");
        }

        return ServerResponse.createByError(500, "生成小程序码失败！");
    }

    public ServerConfig getServerConfig(String openId) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到openid=" + openId + "的学生或者校管信息！");
        }
        return serverConfig;
    }


}

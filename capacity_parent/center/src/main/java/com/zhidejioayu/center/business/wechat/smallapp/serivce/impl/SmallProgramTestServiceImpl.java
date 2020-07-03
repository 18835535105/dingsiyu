package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.dto.GetLimitQRCodeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.SmallProgramTestService;
import com.zhidejioayu.center.business.wechat.smallapp.util.CreateWxQrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private WeChatMapper weChatMapper;

    @Override
    public Object getTest(HttpSession session, String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.getTest(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GoldChangeAnnotation
    public Object saveTest(Integer point, HttpSession session, String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.saveTest(point, openId);
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
       return ServerConfigUtil.getServerInfoByStudentOpenid(openId);
    }


}

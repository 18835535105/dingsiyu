package com.zhidejioayu.center.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.UploadFlyRecordDTO;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.BaseFeignClientUtil;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 企业微信智慧飞行
 *
 * @author: wuchenxi
 * @date: 2020/6/16 13:52:52
 */
@Validated
@RestController
@RequestMapping("/wechat/qy/fly")
public class QyFlyController {

    @Resource
    private QyFlyService qyFlyService;

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;


    /**
     * 上传飞行记录
     *
     * @param file
     * @param uploadFlyRecordDTO
     * @return
     */
    @PostMapping("/uploadFlyRecord")
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, @Valid UploadFlyRecordDTO uploadFlyRecordDTO, BindingResult result) {
        if (file == null) {
            return ServerResponse.createByError(400, "上传的图片不能为空！");
        }

        // 文件大小M
        long size = file.getSize() / 1024 / 1024;
        if (size > 1) {
            return ServerResponse.createByError(400, "上传的图片不能大于1M！");
        }

        try {
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                return ServerResponse.createByError(400, "上传的文件不是图片类型，请重新上传！");
            }
        } catch (IOException e) {
            return ServerResponse.createByError(400, "上传的文件不是图片类型，请重新上传！");
        }

        return qyFlyService.uploadFlyRecord(file, uploadFlyRecordDTO);
    }

    /**
     * 获取当前教师下的所有学生
     *
     * @param dto 查询条件
     * @return
     */
    @GetMapping("/getStudents")
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {
        String openId = dto.getOpenId();

        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }

        BaseQyFeignClient baseQyFeignClient = getBaseQyFeignClient(openId);

        return baseQyFeignClient.getStudents(dto);
    }

    public BaseQyFeignClient getBaseQyFeignClient(String openId) {
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByTeacherOpenid(openId);
        return qyServerFeignClient.get(serverConfig.getServerName());
    }

    @GetMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy(String openId) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }

        BaseQyFeignClient baseQyFeignClient = BaseFeignClientUtil.getBaseQyFeignClient(openId);
        return baseQyFeignClient.getCurrentDayOfStudy(openId);
    }

}

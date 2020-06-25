package com.zhidejioayu.center.business.wechat.qy.fly.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import com.zhidejioayu.center.business.wechat.util.CenterUserInfoUtil;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 企业微信智慧飞行
 *
 * @author: wuchenxi
 * @date: 2020/6/16 13:52:52
 */
@RestController
@RequestMapping("/wechat/qy/fly")
public class QyFlyController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private QyFlyService qyFlyService;


    /**
     * 上传飞行记录
     *
     * @param file
     * @param studentUuid
     * @param num         作业本中二维码序号
     * @return
     */
    @PostMapping("/uploadFlyRecord")
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, String studentUuid, Integer num) {
        if (file == null) {
            return ServerResponse.createByError(400, "上传的图片不能为空！");
        }
        if (studentUuid == null) {
            return ServerResponse.createByError(400, "studentId can't be null!");
        }
        if (num == null) {
            return ServerResponse.createByError(400, "二维码序号不能为空！");
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

        ServerConfig serverConfig = CenterUserInfoUtil.getByUuid(studentUuid);

        LinkedMultiValueMap<Object, Object> params = new LinkedMultiValueMap<>();
        params.add("file", file);
        params.add("studentUuid", studentUuid);
        params.add("num", num);

        String forObject = restTemplate.postForObject(serverConfig.getStudentServerUrl() + "/ec/qy/fly/getCurrentDayOfStudy", params, String.class);
        return JSONObject.parseObject(forObject, ServerResponse.class);
    }

    /**
     * 获取当前教师下的所有学生
     *
     * @param dto 查询条件
     * @return
     */
    @GetMapping("/getStudents")
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {
        String uuid = dto.getStudentUuid();

        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "studentUuid can't be null!");
        }

        ServerConfig serverConfig = CenterUserInfoUtil.getByUuid(uuid);
        String forObject = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/qy/fly/getStudents", String.class, dto);
        return JSONObject.parseObject(forObject, ServerResponse.class);
    }

    @GetMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy(String studentUuid) {
        if (studentUuid == null) {
            return ServerResponse.createByError(400, "studentUUID can't be null!");
        }

        return qyFlyService.getCurrentDayOfStudy(studentUuid);
    }

}

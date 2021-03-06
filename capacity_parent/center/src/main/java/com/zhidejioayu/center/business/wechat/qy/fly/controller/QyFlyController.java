package com.zhidejioayu.center.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.dto.student.StudentStudyPlanListDto;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.UploadFlyRecordDTO;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
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
        if (size > 5) {
            return ServerResponse.createByError(400, "上传的图片不能大于5M！");
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
     * 检查当前学生二维码序号是否已经上传
     *
     * @param uuid      学生uuid
     * @param studentId
     * @param num       二维码序号
     * @return
     */
    @GetMapping("/checkScanQrCode")
    public ServerResponse<Object> checkScanQrCode(String uuid, Long studentId, Integer num) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        if (studentId == null) {
            return ServerResponse.createByError(400, "studentId can't be null!");
        }
        if (num == null) {
            return ServerResponse.createByError(400, "num can't be null!");
        }
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);

        BaseQyFeignClient qyFeignClient = FeignClientUtil.getQyFeignClient(serverConfig.getServerName());

        return qyFeignClient.checkScanQrCode(studentId, num);
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
        return FeignClientUtil.getQyFeignClient(serverConfig.getServerName());
    }

    /**
     * 获取学生智慧飞行记录日历
     *
     * @param uuid
     * @param month 指定月份
     * @return
     */
    @GetMapping("/getFlyCalendar")
    public ServerResponse<Object> getFlyCalendar(String uuid, String month) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        if (StringUtil.isEmpty(month)) {
            return ServerResponse.createByError(400, "month can't be null!");
        }

        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        BaseQyFeignClient baseQyFeignClient = FeignClientUtil.getQyFeignClient(serverConfig.getServerName());
        List<String> flyCalendar = baseQyFeignClient.getFlyCalendar(uuid, month);
        return ServerResponse.createBySuccess(flyCalendar);
    }

    /**
     * 获取学生当日的智慧飞行记录
     *
     * @param uuid 学生uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy(String uuid, String date) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        if (StringUtil.isEmpty(date)) {
            return ServerResponse.createByError(400, "date can't be null!");
        }

        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        BaseQyFeignClient baseQyFeignClient = FeignClientUtil.getQyFeignClient(serverConfig.getServerName());
        return baseQyFeignClient.getCurrentDayOfStudy(uuid, date);
    }

    /**
     * 获取学生学习进度信息
     *
     * @param dto
     * @return
     */
    @GetMapping("/getStudentStudyPlan")
    public ServerResponse<Map<String, Object>> getStudentStudyPlan(@SpringQueryMap StudentStudyPlanListDto dto) {
        if (StringUtil.isEmpty(dto.getUuid())) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(dto.getUuid());
        BaseQyFeignClient baseQyFeignClient = FeignClientUtil.getQyFeignClient(serverConfig.getServerName());
        return baseQyFeignClient.getStudentStudyPlan(dto);
    }

    /**
     * 智慧飞行记录学习总览
     *
     * @param uuid 学生uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/recordOverview")
    public ServerResponse<Object> recordOverview(@RequestParam String uuid, @RequestParam(required = false) String date) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        BaseQyFeignClient qyFeignClient = FeignClientUtil.getQyFeignClient(serverConfig.getServerName());
        return qyFeignClient.recordOverview(uuid, date);
    }
}

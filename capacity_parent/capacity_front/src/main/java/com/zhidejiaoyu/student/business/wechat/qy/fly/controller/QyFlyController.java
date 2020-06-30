package com.zhidejiaoyu.student.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import com.zhidejiaoyu.student.business.service.StudentInfoService;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/qy/fly")
public class QyFlyController {

    @Resource
    private QyFlyService qyFlyService;

    @Resource
    private CurrentDayOfStudyService currentDayOfStudyService;

    @Resource
    private StudentInfoService studentInfoService;

    /**
     * 上传飞行记录
     *
     * @param file
     * @param studentId
     * @param num       作业本中二维码序号
     * @return
     */
    @PostMapping("/uploadFlyRecord")
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num) {
        if (file == null) {
            return ServerResponse.createByError(400, "上传的图片不能为空！");
        }
        if (studentId == null) {
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


        return qyFlyService.uploadFlyRecord(file, studentId, num);
    }

    /**
     * 校验学生今天智慧飞行记录是否已经上传
     *
     * @param studentUuid
     * @return <ul>
     * <li>true:未上传</li>
     * <li>false:已上传</li>
     * </ul>
     */
    @GetMapping("/checkUpload")
    public boolean checkUpload(@RequestParam String studentUuid) {
        return qyFlyService.checkUpload(studentUuid);
    }

    /**
     * 获取当前教师下的所有学生
     *
     * @param dto 查询条件
     * @return
     */
    @GetMapping("/getStudents")
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {
        if (StringUtil.isEmpty(dto.getTeacherUuid())) {
            return ServerResponse.createByError(400, "teacherUuid can't be null!");
        }
        return qyFlyService.getStudents(dto);
    }

    @GetMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy(@RequestParam String studentUuid) {
        if (studentUuid == null) {
            return ServerResponse.createByError(400, "studentUuid can't be null!");
        }
        Student student = studentInfoService.getByUuid(studentUuid);
        if (student == null) {
            throw new ServiceException(400, "未查询到uuid为" + studentUuid + "的学生信息！");
        }
        return currentDayOfStudyService.getCurrentDayOfStudy(student.getId());
    }

}

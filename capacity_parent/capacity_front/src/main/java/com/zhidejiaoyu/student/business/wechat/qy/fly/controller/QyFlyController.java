package com.zhidejiaoyu.student.business.wechat.qy.fly.controller;

import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import com.zhidejiaoyu.student.business.service.StudentInfoService;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.FlyOfStudyService;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 企业微信智慧飞行
 *
 * @author: wuchenxi
 * @date: 2020/6/16 13:52:52
 */
@Slf4j
@RestController
@RequestMapping("/qy/fly")
public class QyFlyController {

    @Resource
    private QyFlyService qyFlyService;

    @Resource
    private CurrentDayOfStudyService currentDayOfStudyService;

    @Resource
    private StudentInfoService studentInfoService;

    @Resource
    private IndexService smallAppIndexService;

    @Resource
    private FlyOfStudyService flyOfStudyService;

    /**
     * 检查当前学生二维码序号是否已经上传
     *
     * @param studentId
     * @param num       二维码序号
     * @return
     */
    @GetMapping("/checkScanQrCode")
    public ServerResponse<Object> checkScanQrCode(@RequestParam Long studentId, @RequestParam Integer num) {
        return qyFlyService.checkScanQrCode(studentId, num);
    }

    /**
     * 校验学生今天智慧飞行记录是否已经上传
     *
     * @param uuid
     * @return <ul>
     * <li>true:未上传</li>
     * <li>false:已上传</li>
     * </ul>
     */
    @GetMapping("/checkUpload")
    public boolean checkUpload(@RequestParam String uuid) {
        return qyFlyService.checkUpload(uuid);
    }

    /**
     * 获取当前教师下的所有学生
     *
     * @param dto 查询条件
     * @return
     */
    @GetMapping("/getStudents")
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        return qyFlyService.getStudents(dto);
    }


    /**
     * 飞行记录学习总览
     *
     * @param uuid 学生uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/recordOverview")
    public ServerResponse<Object> recordOverviewByUuid(@RequestParam String uuid, @RequestParam(required = false) String date) {
        if (StringUtils.isEmpty(uuid)) {
            throw new ServiceException("uuid can't be null");
        }
        if (StringUtil.isEmpty(date)) {
            // 查询总学习记录
            Student student = studentInfoService.getByUuid(uuid);
            return flyOfStudyService.getTotalStudyInfo(student);
        }

        // 查询指定日期的学习记录
        return smallAppIndexService.recordOverviewByUuid(uuid, date);
    }

    /**
     * 获取学生智慧飞行记录日历
     *
     * @param uuid
     * @param month 指定月份
     * @return
     */
    @GetMapping("/getFlyCalendar")
    public List<String> getFlyCalendar(@RequestParam String uuid, @RequestParam String month) {
        return qyFlyService.getFlyCalendar(uuid, month);
    }

    /**
     * 获取指定日期的飞行记录
     *
     * @param uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy(@RequestParam String uuid, @RequestParam(required = false) String date) {
        Student student = studentInfoService.getByUuid(uuid);
        if (Objects.equals(DateUtil.formatYYYYMMDD(new Date()), date)) {
            // 查询当天的数据
            log.info("查询当天的数据，date={}", date);
            return currentDayOfStudyService.getCurrentDayOfStudy(student.getId());
        }
        log.info("查询指定日期的数据， date={}", date);
        return currentDayOfStudyService.getCurrentDayOfStudyWithDate(student.getId(), date);

    }

}

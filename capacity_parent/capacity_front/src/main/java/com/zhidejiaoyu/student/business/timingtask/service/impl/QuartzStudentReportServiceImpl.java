package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolDetail;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolSummary;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardCountModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolDetail;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolSummary;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentHours;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelWriterFactory;
import com.zhidejiaoyu.student.business.mail.Mail;
import com.zhidejiaoyu.student.business.mail.service.MailService;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @Date: 2019/11/5 10:54
 */
@Slf4j
@Service
public class QuartzStudentReportServiceImpl implements QuartzStudentReportService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private StudentHoursMapper studentHoursMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RechargeableCardMapper rechargeableCardMapper;

    @Resource
    private MailService mailService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void exportStudentWithSchool() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 统计各校区学生登录及在线时长信息开始。。。");
        // 昨天的日期
        String yesterday = DateUtil.formatYYYYMMDD(DateTime.now().minusDays(1).toDate());
        String fileName = "校区学生每日信息" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();

        // 汇总sheet页
        ExcelWriterFactory excelWriterFactory = this.exportStudentOnlineTimeWithSchoolSummary(yesterday, fileName);

        // 明细页数据
        this.exportStudentWithSchoolDetail(yesterday, excelWriterFactory);

        // 将文件上传到oss
        this.uploadToOss(fileName);

        // 发送邮件
        this.sendEmail(fileName);

        log.info("定时任务 -> 统计各校区学生登录及在线时长信息完成。");
    }

    private void sendEmail(String fileName) {
        mailService.sendAttachmentsMail(Mail.builder()
                .to(new String[]{"763396567@qq.com", "18515530997@163.com"})
                .filePath(FileConstant.TMP_EXCEL + fileName)
                .subject(fileName)
                .content(fileName)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 15 1 * * ?")
    public void exportStudentPay() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 统计各校区学生充课信息开始。。。");
        // excel文件名
        String fileName = "充课卡详情表" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();

        Long adminId = 1L;
        Date time = DateTime.now().minusDays(1).toDate();
        //根据校管id获取学校下的充课学生
        List<StudentHours> studentHours = studentHoursMapper.selectDeatilsByAdminId(adminId, time);
        //导出充课数据
        ExcelWriterFactory excelWriterFactory = getSizePayCardModel(time, adminId, fileName);
        if (studentHours.size() > 0) {
            //导出充课详细数据
            getRechargePayCardModel(excelWriterFactory, studentHours, time);
        }

        this.uploadToOss(fileName);

        // 发送邮件
        this.sendEmail(fileName);

        log.info("定时任务 -> 统计各校区学生充课信息完成。");
    }

    private ExcelWriterFactory getSizePayCardModel(Date time, Long adminId, String fileName) {
        Date startDay = DateTime.now().minusDays(31).toDate();
        String startTime = DateUtil.formatYYYYMMDD(startDay);
        Date endTime = DateTime.now().minusDays(1).toDate();
        String endTimeStr = DateUtil.formatYYYYMMDD(time);
        List<Map<String, Object>> maps = studentHoursMapper.selectCountByDayTime(startDay, endTime, adminId);
        List<ExportRechargePayCardCountModel> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            ExportRechargePayCardCountModel model = new ExportRechargePayCardCountModel();
            model.setSchool(map.get("schoolName").toString());
            if (map.get("count") == null) {
                model.setCount("0");
            } else {
                model.setCount(map.get("count").toString());
            }
            model.setStartTime(startTime);
            model.setEndTime(endTimeStr);
            list.add(model);
        }
        // excel文件名
        return ExcelUtil.writeExcelWithSheetsAndDownload(list, fileName, "学校充课详情", ExportRechargePayCardCountModel.class);
    }

    private ExcelWriterFactory exportStudentOnlineTimeWithSchoolSummary(String yesterday, String fileName) {
        List<StudentInfoSchoolSummary> studentInfoSchoolSummaries = runLogMapper.selectStudentInfoSchoolSummary(yesterday);
        studentInfoSchoolSummaries.sort(Comparator.comparing(StudentInfoSchoolSummary::getSchoolName));

        List<ExportStudentOnlineTimeWithSchoolSummary> models = studentInfoSchoolSummaries.stream().map(studentInfoSchoolSummary -> {
            ExportStudentOnlineTimeWithSchoolSummary exportStudentOnlineTimeWithSchoolSummary = new ExportStudentOnlineTimeWithSchoolSummary();
            BeanUtils.copyProperties(studentInfoSchoolSummary, exportStudentOnlineTimeWithSchoolSummary);
            return exportStudentOnlineTimeWithSchoolSummary;
        }).collect(Collectors.toList());

        return ExcelUtil.writeExcelWithSheetsAndDownload(models, fileName, "汇总", ExportStudentOnlineTimeWithSchoolSummary.class);
    }

    /**
     * 导出校区每日学生登录情况及有效时长明细
     *
     * @param yesterday
     * @param excelWriterFactory
     * @return
     */
    private void exportStudentWithSchoolDetail(String yesterday, ExcelWriterFactory excelWriterFactory) {
        List<StudentInfoSchoolDetail> studentInfoSchoolDetails = durationMapper.selectExportStudentOnlineTimeWithSchoolDetail(yesterday);
        studentInfoSchoolDetails.sort(Comparator.comparing(StudentInfoSchoolDetail::getSchoolName));

        List<ExportStudentOnlineTimeWithSchoolDetail> models = studentInfoSchoolDetails.stream().map(studentInfoSchoolDetail -> {
            ExportStudentOnlineTimeWithSchoolDetail exportStudentOnlineTimeWithSchoolDetail = new ExportStudentOnlineTimeWithSchoolDetail();
            BeanUtils.copyProperties(studentInfoSchoolDetail, exportStudentOnlineTimeWithSchoolDetail);
            return exportStudentOnlineTimeWithSchoolDetail;
        }).collect(Collectors.toList());

        excelWriterFactory.write(models, ExcelUtil.getWriteSheet(2, "明细", ExportStudentOnlineTimeWithSchoolDetail.class));
        excelWriterFactory.finish();
    }

    private void getRechargePayCardModel(ExcelWriterFactory excelWriterFactory, List<StudentHours> studentHours, Date time) {
        //获取充课卡信息
        Map<Integer, Map<String, Object>> cardMap = rechargeableCardMapper.selAllRechargeableCardMap();
        if (studentHours.size() > 0) {
            //进行每一个学生的分组
            Map<Long, List<StudentHours>> map = studentHours.stream().collect(Collectors.groupingBy(hours -> hours.getStudentId().longValue()));
            Set<Long> longs = map.keySet();
            List<ExportRechargePayCardModel> list = new ArrayList<>();
            for (Long studentId : longs) {
                ExportRechargePayCardModel model = new ExportRechargePayCardModel();
                Student student = studentMapper.selectById(studentId);
                List<StudentHours> studentHours1 = map.get(studentId);
                Map<Integer, Integer> studentCardMap = new HashMap<>(16);
                //将一个学生可能出现的多个数据添加到一起
                for (StudentHours hours : studentHours1) {
                    String type = hours.getType();
                    String[] split = type.split(",");
                    for (String str : split) {
                        if (str.contains(":")) {
                            String[] split1 = str.split(":");
                            Integer cardId = Integer.parseInt(split1[0]);
                            Integer number = Integer.parseInt(split1[1]);
                            Integer carNumber = studentCardMap.get(cardId);
                            if (carNumber != null) {
                                carNumber += number;
                            } else {
                                carNumber = number;
                            }
                            studentCardMap.put(cardId, carNumber);
                        }
                    }
                }
                //获取离当前日最大登入时间
                Date date = DateUtil.maxTime(time);
                Date loginTime = durationMapper.selectLoginTimeByDate(studentId, date);
                model.setStudentAccount(student.getAccount());
                model.setSchool(student.getSchoolName());
                model.setStudentName(StringUtils.isEmpty(student.getStudentName()) ? student.getStudentName() : "默认姓名");
                model.setCreateTime(DateUtil.formatYYYYMMDD(studentHours1.get(0).getCreateTime()));
                if (loginTime == null) {
                    model.setLoginTime("未登入");
                } else {
                    String loginTimeStr = DateUtil.formatYYYYMMDD(loginTime);
                    model.setLoginTime(loginTimeStr);
                }
                StringBuilder builder = new StringBuilder();
                Set<Integer> cardIds = cardMap.keySet();
                for (Integer cardId : cardIds) {
                    Map<String, Object> cardMap1 = cardMap.get(cardId);
                    if (studentCardMap.get(cardId) != null) {
                        if (studentCardMap.get(cardId) > 0) {
                            builder.append(cardMap1.get("name")).append(":").
                                    append(studentCardMap.get(cardId)).append(" ");
                        }
                    }
                }
                model.setLessonDetails(builder.toString());
                list.add(model);
            }
            excelWriterFactory.write(list, ExcelUtil.getWriteSheet(2, "学生信息", ExportRechargePayCardModel.class));
            excelWriterFactory.finish();
        }
    }

    private void uploadToOss(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(FileConstant.TMP_EXCEL + fileName);
            OssUpload.uploadWithInputStream(fileInputStream, FileConstant.STUDENT_REPORT_EXCEL, fileName);
        } catch (FileNotFoundException e) {
            log.error("定时任务上传学生报表失败！", e);
        }
    }

}

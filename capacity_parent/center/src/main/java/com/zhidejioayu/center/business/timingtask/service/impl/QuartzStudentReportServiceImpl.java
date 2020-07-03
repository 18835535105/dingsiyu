package com.zhidejioayu.center.business.timingtask.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.dfdz.mail.Mail;
import com.dfdz.mail.service.MailService;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardCountModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolDetail;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolSummary;
import com.zhidejiaoyu.common.mapper.center.ReceiveEmailMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.ReceiveEmail;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelWriterFactory;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.ExportRechargePayResultVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolDetail;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolResult;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolSummary;
import com.zhidejioayu.center.business.feignclient.student.BaseStudentFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.timingtask.service.QuartzStudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @Date: 2019/11/5 10:54
 */
@Slf4j
@Service
public class QuartzStudentReportServiceImpl implements QuartzStudentReportService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Resource
    private ReceiveEmailMapper receiveEmailMapper;

    @Resource
    private MailService mailService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void exportStudentWithSchool() {
        log.info("定时任务 -> 统计各校区学生登录及在线时长信息开始。。。");

        List<String> serverNames = serverConfigMapper.selectAllServerName();
        List<StudentInfoSchoolResult> studentInfoSchoolResults = new ArrayList<>();
        serverNames.forEach(serverName -> {
            try {
                BaseStudentFeignClient studentFeignClient = FeignClientUtil.getStudentFeignClient(serverName);
                ServerResponse<StudentInfoSchoolResult> studentInfoSchoolResultServerResponse = studentFeignClient.getStudentLoginAndTimeInfo();
                studentInfoSchoolResults.add(studentInfoSchoolResultServerResponse.getData());
            } catch (Exception e) {
                log.error("查询学生需要导出的信息出错！serverName={}", serverName, e);
            }
        });

        if (CollectionUtils.isEmpty(studentInfoSchoolResults)) {
            log.info("未查询到需要导出的信息！");
            return;
        }

        List<StudentInfoSchoolDetail> studentInfoSchoolDetailList = new ArrayList<>();
        List<StudentInfoSchoolSummary> studentInfoSchoolSummaries = new ArrayList<>();
        studentInfoSchoolResults.forEach(studentInfoSchoolResult -> {
            studentInfoSchoolDetailList.addAll(studentInfoSchoolResult.getStudentInfoSchoolDetailList());
            studentInfoSchoolSummaries.addAll(studentInfoSchoolResult.getStudentInfoSchoolSummaries());
        });

        studentInfoSchoolDetailList.sort(Comparator.comparing(StudentInfoSchoolDetail::getSchoolName));
        studentInfoSchoolSummaries.sort(Comparator.comparing(StudentInfoSchoolSummary::getSchoolName));


        String fileName = "校区学生每日信息" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();

        // 汇总sheet页
        ExcelWriterFactory excelWriterFactory = this.exportStudentOnlineTimeWithSchoolSummary(fileName, studentInfoSchoolSummaries);

        // 明细页数据
        this.exportStudentWithSchoolDetail(excelWriterFactory, studentInfoSchoolDetailList);

        // 将文件上传到oss
        this.uploadToOss(fileName);

        // 发送邮件
        this.sendEmail(fileName);

        log.info("定时任务 -> 统计各校区学生登录及在线时长信息完成。");
    }

    private ExcelWriterFactory exportStudentOnlineTimeWithSchoolSummary(String fileName, List<StudentInfoSchoolSummary> studentInfoSchoolSummaries) {
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
     * @param excelWriterFactory
     * @return
     */
    private void exportStudentWithSchoolDetail(ExcelWriterFactory excelWriterFactory, List<StudentInfoSchoolDetail> studentInfoSchoolDetails) {
        studentInfoSchoolDetails.sort(Comparator.comparing(StudentInfoSchoolDetail::getSchoolName));

        List<ExportStudentOnlineTimeWithSchoolDetail> models = studentInfoSchoolDetails.stream().map(studentInfoSchoolDetail -> {
            ExportStudentOnlineTimeWithSchoolDetail exportStudentOnlineTimeWithSchoolDetail = new ExportStudentOnlineTimeWithSchoolDetail();
            studentInfoSchoolDetail.setPaid(Objects.equals("0", studentInfoSchoolDetail.getPaid()) ? "未充课" : "已充课");
            BeanUtils.copyProperties(studentInfoSchoolDetail, exportStudentOnlineTimeWithSchoolDetail);
            return exportStudentOnlineTimeWithSchoolDetail;
        }).collect(Collectors.toList());

        excelWriterFactory.write(models, ExcelUtil.getWriteSheet(2, "明细", ExportStudentOnlineTimeWithSchoolDetail.class));
        excelWriterFactory.finish();
    }


    private void sendEmail(String fileName) {
        List<ReceiveEmail> receiveEmails = receiveEmailMapper.selectByType(1);
        if (CollectionUtils.isNotEmpty(receiveEmails)) {
            String[] receivers = receiveEmails.stream().map(ReceiveEmail::getEmail).toArray(String[]::new);
            mailService.sendAttachmentsMail(Mail.builder()
                    .to(receivers)
                    .filePath(FileConstant.TMP_EXCEL + fileName)
                    .subject(fileName)
                    .content(fileName)
                    .build());
        }
        this.deleteTmpFile(fileName);
    }

    private void deleteTmpFile(String fileName) {
        File file = new File(FileConstant.TMP_EXCEL + fileName);
        if (file.exists()) {
            boolean delete = file.delete();
            if (delete) {
                log.info("临时文件：{}已被成功删除！", fileName);
            } else {
                log.info("临时文件：{}删除失败！", fileName);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportStudentPay() {
        log.info("定时任务 -> 统计各校区学生充课信息开始。。。");

        List<String> serverNames = serverConfigMapper.selectAllServerName();
        List<ExportRechargePayResultVO> exportRechargePayResultVos = new ArrayList<>();
        serverNames.forEach(serverName -> {
            try {
                BaseStudentFeignClient studentFeignClient = FeignClientUtil.getStudentFeignClient(serverName);
                ServerResponse<ExportRechargePayResultVO> exportRechargePayResultVoServerResponse = studentFeignClient.getStudentPayInfo();
                exportRechargePayResultVos.add(exportRechargePayResultVoServerResponse.getData());
            } catch (Exception e) {
                log.error("查询学生需要导出的信息出错！serverName={}", serverName, e);
            }
        });

        if (CollectionUtils.isEmpty(exportRechargePayResultVos)) {
            log.info("未查询到学生充值信息！");
            return;
        }

        List<ExportRechargePayCardCountModel> exportRechargePayCardCountModels = new ArrayList<>();
        List<ExportRechargePayCardModel> exportRechargePayCardModels = new ArrayList<>();
        exportRechargePayResultVos.forEach(exportRechargePayResultVO -> {
            exportRechargePayCardCountModels.addAll(exportRechargePayResultVO.getExportRechargePayCardCountModelList());
            exportRechargePayCardModels.addAll(exportRechargePayResultVO.getExportRechargePayCardModelList());
        });


        // excel文件名
        String fileName = "充课卡详情表" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();

        ExcelWriterFactory excelWriterFactory = ExcelUtil.writeExcelWithSheetsAndDownload(exportRechargePayCardCountModels, fileName, "学校充课详情", ExportRechargePayCardCountModel.class);

        if (CollectionUtils.isNotEmpty(exportRechargePayCardModels)) {
            excelWriterFactory.write(exportRechargePayCardModels, ExcelUtil.getWriteSheet(2, "学生信息", ExportRechargePayCardModel.class));
            excelWriterFactory.finish();
        }

        this.uploadToOss(fileName);

        // 发送邮件
        this.sendEmail(fileName);

        log.info("定时任务 -> 统计各校区学生充课信息完成。");
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

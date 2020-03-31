package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolDetail;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolSummary;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardCountModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolDetail;
import com.zhidejiaoyu.common.excelmodel.student.ExportStudentOnlineTimeWithSchoolSummary;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelWriterFactory;
import com.zhidejiaoyu.student.business.mail.Mail;
import com.zhidejiaoyu.student.business.mail.service.MailService;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
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

    @Resource
    private ReceiveEmailMapper receiveEmailMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private WorshipMapper worshipMapper;

    @Resource
    private GoldRecordMapper goldRecordMapper;

    @Resource
    private StudentDailyLearningMapper studentDailyLearningMapper;
    @Resource
    private ClockInMapper clockInMapper;
    @Resource
    private PunchRecordMapper punchRecordMapper;

    //    @Scheduled(cron = "0 0 1 * * ?")
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
//    @Scheduled(cron = "0 15 1 * * ?")
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

    /**
     * 添加学生详情和学生打卡信息
     */
    @Override
    @Scheduled(cron = "0 15 3 * * ?")
    public void getStudentDailyLearning() {
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        //获取昨日登入的学生
        log.info("定时任务 -> 统计学生详情开始。");
        List<Long> studentIds = runLogMapper.selectLoginStudentId(beforeDaysDate);
        if (studentIds != null && studentIds.size() > 0) {
            //获取学生今日学习时常
            Map<String, Map<String, Object>> studentLoginMap = durationMapper.selectValidTimeByStudentIds(studentIds, beforeDaysDate);
            Map<Long, Map<String, Object>> longMapMap = clockInMapper.selectByStudentIds(studentIds, beforeDaysDate);
            Map<Long, Map<String, Object>> longMapMap1 = worshipMapper.selectByStudentIdsAndDate(studentIds, beforeDaysDate);
            studentIds.forEach(studentId -> {
                StudentDailyLearning studentDailyLearning = new StudentDailyLearning();
                studentDailyLearning.setStudentId(studentId);
                //创建时间
                studentDailyLearning.setCreateTime(LocalDateTime.now());
                //获取学生登入时间
                Date date = durationMapper.selectLoginTimeByStudentIdAndDate(studentId, beforeDaysDate);
                //获取学习时间
                studentDailyLearning.setStudyTime(DateUtil.getLocalDateTime(date));
                //获取金币数据
                studentDailyLearning.setGoldAdd(getGoldAdd(studentId, beforeDaysDate, 4));
                studentDailyLearning.setGoldConsumption(getGoldAdd(studentId, beforeDaysDate, 5));
                //获取学习有效时常
                studentDailyLearning.setValidTime(
                        Integer.parseInt(studentLoginMap.get(studentId).get("validTime").toString()));
                Map<String, Object> map = longMapMap.get(studentId);
                studentDailyLearning.setClockIn(map != null && map.size() > 0 ? 1 : 2);
                Map<String, Object> map1 = longMapMap1.get(studentId);
                if (map1 != null) {
                    Object count = map1.get("count");
                    studentDailyLearning.setOiling(count != null ? Integer.parseInt(count.toString()) : 0);
                } else {
                    studentDailyLearning.setOiling(0);
                }

                studentDailyLearningMapper.insert(studentDailyLearning);
            });
        }
        log.info("定时任务 -> 统计学生详情结束。");
        log.info("定时任务 -> 统计学生点赞数量开始。");
        //统计签到学生
        Map<Long, Map<String, Object>> studentMap = testRecordMapper.selectByGenreAndDate(GenreConstant.SMALLAPP_GENRE, beforeDaysDate);
        Set<Long> longs = studentMap.keySet();
        if (longs.size() > 0) {
            List<Long> list = new ArrayList(longs);
            Map<Long, Map<String, Object>> longMapMap1 = worshipMapper.selectByStudentIdsAndDate(list, beforeDaysDate);
            list.forEach(studentId -> {
                PunchRecord punchRecord = new PunchRecord();
                punchRecord.setStudentId(studentId);
                Map<String, Object> map = studentMap.get(studentId);
                punchRecord.setCardTime((LocalDateTime) map.get("date"));
                Map<String, Object> map1 = longMapMap1.get(studentId);
                if (map1 != null) {
                    Object count = map1.get("count");
                    punchRecord.setOiling(count != null ? Integer.parseInt(count.toString()) : 0);
                } else {
                    punchRecord.setOiling(0);
                }
                punchRecord.setCreatTime(LocalDateTime.now());
                punchRecord.setPoint(Integer.parseInt(map.get("count").toString()));
                punchRecordMapper.insert(punchRecord);
            });
        }
        log.info("定时任务 -> 统计学生点赞数量结束。");
    }

    /**
     * 增加金币数据
     */
    @Override
    @Scheduled(cron = "0 15 4 * * ?")
    public void getGoldAdd() {
        log.info("定时任务 -> 统计学生每日增加减少金币开始。");
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        //获取增加金币学生信息
        List<RunLog> runLogs = runLogMapper.selectByDateAndType(beforeDaysDate, 4);
        runLogs.forEach(runlog -> {
            getGoldRecord(runlog, 1);
        });
        List<RunLog> runLogDowns = runLogMapper.selectByDateAndType(beforeDaysDate, 5);
        runLogDowns.forEach(runlog -> {
            getGoldRecord(runlog, 2);
        });
        log.info("定时任务 -> 统计学生每日增加减少金币完成。");
    }

    private void getGoldRecord(RunLog runlog, int type) {
        Integer goldRecordGold = getGoldRecordGold(runlog.getLogContent());
        if (goldRecordGold != null) {
            GoldRecord goldRecord = new GoldRecord();
            goldRecord.setStudentId(runlog.getOperateUserId());
            goldRecord.setContent(getGoldRecordContent(runlog.getLogContent(),type));
            if(type==1){
                goldRecord.setGold(goldRecordGold);
            }else{
                goldRecord.setGold(-goldRecordGold);
            }

            goldRecord.setCreateTime(LocalDateTime.now());
            goldRecord.setStudyTime(DateUtil.getLocalDateTime(runlog.getCreateTime()));
            goldRecordMapper.insert(goldRecord);
        }
    }

    private Integer getGoldRecordGold(String content) {
        int indexOf = content.indexOf("#");
        int indexOf1 = content.lastIndexOf("#");
        int gold = Integer.parseInt(content.substring(indexOf + 1, indexOf1));
        return gold > 0 ? gold : null;
    }

    private String getGoldRecordContent(String content,int type) {
        if(type==1){
            if (content.contains("登陆")) {
                return "学习奖励";
            }
            if (content.contains("单元测试")) {
                return "学习奖励";
            }
            if (content.contains("单元闯关")) {
                return "学习奖励";
            }
            if (content.contains("飞行测试")) {
                return "学习奖励";
            }
            if (content.contains("完善资料")) {
                return "学习奖励";
            }
            if (content.contains("单元前测")) {
                return "学习奖励";
            }
            if (content.contains("大于等于")) {
                return "学习奖励";
            }
            if (content.contains("能力值测试")) {
                return "学习奖励";
            }
            if (content.contains("任务奖励")) {
                return "学习奖励";
            }
            if (content.contains("学后测试")) {
                return "学习奖励";
            }
            if (content.contains("抽奖") || content.contains("抽獎")) {
                return "学习奖励";
            }
            if (content.contains("日奖励")) {
                return "学习奖励";
            }
            if (content.contains("意见")) {
                return "教师奖励";
            }
            if (content.contains("游戏")) {
                return "学习奖励";
            }
            if (content.contains("五维测试")) {
                return "学习奖励";
            }
            if (content.contains("已学测试")) {
                return "学习奖励";
            }
            if (content.contains("词") || content.contains("句")) {
                return "学习奖励";
            }
            if (content.contains("pk")) {
                return "学习奖励";
            }
            if (content.contains("修改密码")) {
                return "首次修改密码";
            }
            if (content.contains("记忆") || content.contains("眼脑训练")
                    || content.contains("火眼金睛") || content.contains("最强大脑")
                    || content.contains("乾坤挪移")) {
                return "学习奖励";
            }
            if (content.contains("副本挑战")) {
                return "学习奖励";
            }
        }else{
            if (content.contains("测试")) {
                return "学习奖励";
            }
            if (content.contains("pk")) {
                return "学习奖励";
            }
            if (content.contains("奖品兑换")) {
                return "奖品兑换";
            }
        }

        return null;
    }


    private Integer getGoldAdd(Long studentId, Date date, int type) {
        Integer gold = 0;
        List<String> goldMap = runLogMapper.selectGoldByStudentIdAndDate(studentId, date, type);
        for (String goldStr : goldMap) {
            int indexOf = goldStr.indexOf("#");
            int lastIndexOf = goldStr.lastIndexOf("#");
            gold += Integer.parseInt(goldStr.substring(indexOf + 1, lastIndexOf));
        }
        return gold;
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
            studentInfoSchoolDetail.setPaid(Objects.equals("0", studentInfoSchoolDetail.getPaid()) ? "未充课" : "已充课");
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
                            int number = Integer.parseInt(split1[1]);
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

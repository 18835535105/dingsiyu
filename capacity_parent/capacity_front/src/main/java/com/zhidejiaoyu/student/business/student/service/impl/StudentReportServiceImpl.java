package com.zhidejiaoyu.student.business.student.service.impl;

import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardCountModel;
import com.zhidejiaoyu.common.excelmodel.student.ExportRechargePayCardModel;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.SchoolGoldFactory;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentHours;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.ExportRechargePayResultVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolDetail;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolResult;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolSummary;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.student.service.StudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @Date: 2019/11/5 10:54
 */
@Slf4j
@Service
public class StudentReportServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements StudentReportService {

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
    private TeacherMapper teacherMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private SchoolGoldFactoryMapper schoolGoldFactoryMapper;

    @Override
    public ServerResponse<StudentInfoSchoolResult> getStudentLoginAndTimeInfo() {
        // 昨天的日期
        String yesterday = DateUtil.formatYYYYMMDD(DateTime.now().minusDays(1).toDate());

        List<StudentInfoSchoolDetail> studentInfoSchoolDetails = durationMapper.selectExportStudentOnlineTimeWithSchoolDetail(yesterday);
        List<StudentInfoSchoolSummary> studentInfoSchoolSummaries = runLogMapper.selectStudentInfoSchoolSummary(yesterday);

        StudentInfoSchoolResult studentInfoSchoolResult = new StudentInfoSchoolResult();
        studentInfoSchoolResult.setStudentInfoSchoolDetailList(studentInfoSchoolDetails);
        studentInfoSchoolResult.setStudentInfoSchoolSummaries(studentInfoSchoolSummaries);
        return ServerResponse.createBySuccess(studentInfoSchoolResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<ExportRechargePayResultVO> getStudentPayInfo() {
        Long adminId = 1L;
        Date time = DateTime.now().minusDays(1).toDate();
        //根据校管id获取学校下的充课学生
        List<StudentHours> studentHours = studentHoursMapper.selectDeatilsByAdminId(adminId, time);

        //导出充课数据
        List<ExportRechargePayCardCountModel> exportRechargePayCardCountModels = getSizePayCardModel(time, adminId);

        ExportRechargePayResultVO exportRechargePayResultVO = new ExportRechargePayResultVO();
        exportRechargePayResultVO.setExportRechargePayCardCountModelList(exportRechargePayCardCountModels);

        if (studentHours.size() > 0) {
            //导出充课详细数据
            List<ExportRechargePayCardModel> list = getRechargePayCardModel(studentHours, time);
            exportRechargePayResultVO.setExportRechargePayCardModelList(list);
        }

        return ServerResponse.createBySuccess(exportRechargePayResultVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportSchoolGold() {
        log.info("定时任务 -> 统计各校区每日增加金币数开始。");
        //获取多有校管id
        List<Long> adminIds = teacherMapper.selectAllAdminId();
        adminIds.forEach(adminId -> {
            Integer integer = goldLogMapper.selectGoldByAdminIdAndDate(adminId, DateUtil.getBeforeDaysDate(new Date(), 1));
            if (integer != null) {
                Double gold = integer * 0.1;
                SchoolGoldFactory schoolGoldFactory = schoolGoldFactoryMapper.selectByAdminId(adminId);
                if (schoolGoldFactory != null) {
                    schoolGoldFactory.setGold(schoolGoldFactory.getGold() + gold);
                    schoolGoldFactoryMapper.updateById(schoolGoldFactory);
                } else {
                    schoolGoldFactory = new SchoolGoldFactory();
                    schoolGoldFactory.setGold(gold);
                    schoolGoldFactory.setSchoolAdminId(adminId);
                    schoolGoldFactoryMapper.insert(schoolGoldFactory);
                }
            }

        });
        log.info("定时任务 -> 统计各校区每日增加金币数完成。");
    }

    private List<ExportRechargePayCardCountModel> getSizePayCardModel(Date time, Long adminId) {
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
        return list;
    }


    private List<ExportRechargePayCardModel> getRechargePayCardModel(List<StudentHours> studentHours, Date time) {
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
            return list;
        }
        return Collections.emptyList();
    }

}

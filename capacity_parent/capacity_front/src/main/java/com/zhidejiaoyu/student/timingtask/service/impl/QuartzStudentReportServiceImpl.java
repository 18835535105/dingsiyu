package com.zhidejiaoyu.student.timingtask.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelWriterFactory;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.timingtask.service.QuartzStudentReportService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author: wuchenxi
 * @Date: 2019/11/5 10:54
 */
@Service
public class QuartzStudentReportServiceImpl implements QuartzStudentReportService {

    @Override
    public ServerResponse statisticsStudentWithSchoolInfo() {
        // 昨天的日期
        String yesterday = DateUtil.formatYYYYMMDD(DateTime.now().minusDays(1).toDate());
        String fileName = "校区学生每日信息" + System.currentTimeMillis();


        // 汇总sheet页
//        ExcelWriterFactory excelWriterFactory = exportStudentOnlineTimeWithSchoolSummary(yesterday, fileName);
//
//        // 详情sheet页
//        if (exportStudentWithSchoolDetail(yesterday, excelWriterFactory)) {
//            return new ErrorTip(BizExceptionEnum.NO_DATA);
//        }
        return null;
    }

    @Override
    public Object exportStudentPay(HttpServletResponse response) {
        Long adminId = 1L;
        Date time = DateTime.now().minusDays(1).toDate();
        //根据校管id获取学校下的充课学生
        List<StudentHours> studentHours = studentHoursMapper.selectDeatilsByAdminId(adminId, time);
        //导出充课数据
        getSizePayCardModel(response, time, adminId);
        if (studentHours.size() > 0) {
            //导出充课详细数据
            getRechargePayCardModel(studentHours, time, response);
        }
        return new SuccessTip();
        return null;
    }

    private void getSizePayCardModel(HttpServletResponse response, Date time, Long adminId) {
        Date startDay = DateTime.now().minusDays(31).toDate();
        String startTime = DateUtil.getDay(startDay);
        Date endTime = DateTime.now().minusDays(1).toDate();
        String endTimeStr = DateUtil.getDay(time);
        List<Map<String, Object>> maps = studentHoursMapper.selectCountByDayTime(startDay, endTime, adminId);
        List<ExportRechargePayCardCountModel> list = new ArrayList();
        for (Map<String, Object> map : maps) {
            ExportRechargePayCardCountModel model = new ExportRechargePayCardCountModel();
            model.setSchool(map.get("schoolName").toString());
            if (map.get("count") == null) {
                model.setCount("0");
            } else {
                model.setCount(map.get("count").toString());
            }
            model.setSchool(startTime);
            model.setSchool(endTimeStr);
            list.add(model);
        }
        // excel文件名
        String fileName = "充课卡详情表" + System.currentTimeMillis() + ExcelTypeEnum.XLSX;
        ExcelUtil.writeExcelWithSheets(response, list, fileName, "学校充课详情", new ExportRechargePayCardCountModel());
    }

    private void getRechargePayCardModel(List<StudentHours> studentHours, Date time, HttpServletResponse response) {
        //获取充课卡信息
        Map<Integer, Object> cardMap = rechargeableCardMapper.selAllRechargeableCardMap();
        Map<Long, List<StudentHours>> map = new HashMap<>();
        if (studentHours.size() > 0) {
            //进行每一个学生的分组
            for (StudentHours hours : studentHours) {
                List<StudentHours> students = map.get(hours.getStudentId());
                if (students == null) {
                    students = new ArrayList<>();
                    students.add(hours);
                } else {
                    students.add(hours);
                }
                map.put(hours.getStudentId().longValue(), students);
            }
            Set<Long> longs = map.keySet();
            List<ExportRechargePayCardModel> list = new ArrayList<>();
            for (Long studentId : longs) {
                ExportRechargePayCardModel model = new ExportRechargePayCardModel();
                Student student = studentMapper.selectById(studentId);
                List<StudentHours> studentHours1 = map.get(studentId);
                Map<Integer, Integer> sutdentCardMap = new HashMap<>();
                //将一个学生可能出现的多个数据添加到一起
                for (StudentHours hours : studentHours1) {
                    String type = hours.getType();
                    String[] split = type.split(",");
                    for (String str : split) {
                        if (str.contains(":")) {
                            String[] split1 = str.split(":");
                            Integer cardId = Integer.parseInt(split1[0].toString());
                            Integer number = Integer.parseInt(split1[1].toString());
                            Integer carNumber = sutdentCardMap.get(cardId);
                            if (carNumber != null) {
                                carNumber += number;
                            } else {
                                carNumber = number;
                            }
                            sutdentCardMap.put(cardId, carNumber);
                        }
                    }
                }
                //获取离当前日最大登入时间
                Date date = DateUtil.maxTime(time);
                Date loginTime = durationMapper.selectLoginTimeByDate(studentId, date);
                model.setStudentAccount(student.getAccount());
                model.setSchool(student.getSchoolName());
                model.setCreateTime(DateUtil.getTime(studentHours1.get(0).getCreateTime()));
                if (loginTime == null) {
                    model.setLoginTime("未登入");
                } else {
                    String loginTimeStr = DateUtil.getTime(loginTime);
                    model.setLoginTime(loginTimeStr);
                }
                StringBuilder builder = new StringBuilder();
                Set<Integer> cardIds = cardMap.keySet();
                for (Integer cardId : cardIds) {
                    Map<String, Object> cardmap = (Map<String, Object>) cardMap.get(cardId);
                    if(sutdentCardMap.get(cardId)!=null){
                        if (sutdentCardMap.get(cardId) > 0) {
                            builder.append(cardmap.get("name")).append(":").
                                    append(sutdentCardMap.get(cardId)).append(" ");
                        }
                    }
                }
                model.setLessonDetails(builder.toString());
                list.add(model);
            }
            // excel文件名
            String fileName = "充课卡详情表" + System.currentTimeMillis() + ExcelTypeEnum.XLSX;
            ExcelUtil.writeExcelWithSheets(response, list, fileName, "学生信息", new ExportRechargePayCardModel());
        }
    }

}

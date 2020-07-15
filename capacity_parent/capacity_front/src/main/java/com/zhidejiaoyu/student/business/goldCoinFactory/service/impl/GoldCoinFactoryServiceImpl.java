package com.zhidejiaoyu.student.business.goldCoinFactory.service.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.mapper.SchoolGoldFactoryMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.SchoolGoldFactory;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.goldCoinFactory.service.GoldCoinFactoryService;
import com.zhidejiaoyu.student.business.goldCoinFactory.vo.GoldCoinFactoryGoldList;
import com.zhidejiaoyu.student.business.goldCoinFactory.vo.GoldCoinFactoryGoldVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoldCoinFactoryServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements GoldCoinFactoryService {

    @Resource
    private GoldLogMapper goldLogMapper;
    @Resource
    private SchoolGoldFactoryMapper schoolGoldFactoryMapper;

    @Override
    public Object getList(HttpSession session) {
        Date date = new Date();
        GoldCoinFactoryGoldList vo = new GoldCoinFactoryGoldList();
        //获取距离奖励公布时间
        Student student = getStudent(session);
        //获取校长id
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        getReturnList(schoolAdminId, date, vo);
        return vo;
    }

    @Override
    public Object getIndex(HttpSession session) {
        Date date = new Date();
        GoldCoinFactoryGoldVo vo = new GoldCoinFactoryGoldVo();
        //获取距离奖励公布时间
        vo.setTime(getDateLong(date) / 1000);
        Student student = getStudent(session);
        //获取校长id
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        //获取校区金币数
        SchoolGoldFactory schoolGoldFactory = schoolGoldFactoryMapper.selectByAdminId(schoolAdminId.longValue());
        if (schoolGoldFactory != null) {
            vo.setGold(schoolGoldFactory.getGold().intValue());
        } else {
            vo.setGold(0);
        }
        vo.setDeadline(DateUtil.formatYYYYMMDDHHMMSS(DateUtil.minTime(new Date())));
        return vo;
    }

    @Override
    public Object getSatelliteClass(HttpSession session) {
        Student student = getStudent(session);
        double gold = student.getOfflineGold() + student.getSystemGold();
        double satelliteClassGold = 10000;
        Integer satelliteClass = 1;
        if (gold >= satelliteClassGold) {
            for (int i = 1; i <= 4; i++) {
                satelliteClassGold *= 1.5;
                if (gold >= satelliteClassGold) {
                    satelliteClass += 1;
                } else {
                    continue;
                }
            }
            return ServerResponse.createBySuccess(satelliteClass);
        } else {
            return ServerResponse.createBySuccess(0);
        }
    }

    private void getReturnList(Integer schoolAdminId, Date date, GoldCoinFactoryGoldList vo) {
        //获取搜索数据的时间
        Date startDate;
        Date endDate;
        Date lastDate = DateUtil.getTheSpecifiedDate(date, 15);
        lastDate = DateUtil.minTime(lastDate);
        if (lastDate.getTime() >= System.currentTimeMillis()) {
            startDate = DateUtil.minTime(DateUtil.getTheSpecifiedDate(date, 1));
            endDate = DateUtil.maxTime(lastDate);
        } else {
            //获取当月最后日期
            Date lastDayToMonth = DateUtil.getLastDayToMonth(date);
            endDate = lastDayToMonth;
            if (lastDayToMonth.getTime() >= System.currentTimeMillis()) {
                startDate = DateUtil.minTime(lastDate);
                endDate = DateUtil.maxTime(lastDayToMonth);
            } else {
                Date lastDaysDate = DateUtil.getLastDaysDate(lastDayToMonth, 15);
                startDate = DateUtil.minTime(endDate);
                endDate = DateUtil.maxTime(lastDaysDate);
            }
        }
        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<Map<String, Object>> maps = goldLogMapper.selectGoldByAdminIdAndStartDateAndEndTime(schoolAdminId, DateUtil.formatYYYYMMDDHHMMSS(startDate), DateUtil.formatYYYYMMDDHHMMSS(endDate));
        int count = goldLogMapper.countByAdminIdAndStartDateAndEndTime(schoolAdminId, DateUtil.formatYYYYMMDDHHMMSS(startDate), DateUtil.formatYYYYMMDDHHMMSS(endDate));
        vo.setSize(count);
        vo.setPages(count % PageUtil.getPageSize() > 0 ? count / PageUtil.getPageSize() + 1 : count / PageUtil.getPageSize());
        List<GoldCoinFactoryGoldList.GoldList> returnList = new ArrayList<>();
        if (maps.size() > 0) {
            maps.forEach(map -> {
                returnList.add(
                        GoldCoinFactoryGoldList.GoldList.builder()
                                .model(map.get("model").toString())
                                .studentName(map.get("studentName").toString())
                                .getGold(String.format("%.1f", Integer.parseInt(map.get("gold").toString()) * 0.1))
                                .studyTime(DateUtil.formatYYYYMMDD((Date) map.get("createTime"))).build());

            });

        }
        vo.setReturnList(returnList);

    }

    private Long getDateLong(Date date) {
        Date theSpecifiedDate = DateUtil.getTheSpecifiedDate(date, 15);
        theSpecifiedDate = DateUtil.minTime(theSpecifiedDate);
        if (theSpecifiedDate.getTime() >= System.currentTimeMillis()) {
            return theSpecifiedDate.getTime() - System.currentTimeMillis();
        } else {
            //获取当月最后日期
            Date lastDayToMonth = DateUtil.getLastDayToMonth(date);
            lastDayToMonth = DateUtil.minTime(lastDayToMonth);
            if (lastDayToMonth.getTime() >= System.currentTimeMillis()) {
                return lastDayToMonth.getTime() - System.currentTimeMillis();
            } else {
                Date lastDaysDate = DateUtil.getLastDaysDate(lastDayToMonth, 15);
                return lastDaysDate.getTime() - System.currentTimeMillis();
            }
        }
    }
}

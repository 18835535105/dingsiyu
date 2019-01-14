package com.zhidejiaoyu.student.service.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author wuchenxi
 * @date 2018/8/29
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private DurationMapper durationMapper;

    @Override
    public Student getStudent(HttpSession session) {
        return (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
    }

    @Override
    public Long getStudentId(HttpSession session) {
        return getStudent(session).getId();
    }

    @Override
    public Integer getValidTime(Long studentId, String beginTime, String endTime) {
        return durationMapper.selectValidTime(studentId, beginTime, endTime);
    }

    @Override
    public Integer getOnLineTime(HttpSession session, String beginTime, String endTime) {
        Integer onlineTime = durationMapper.selectOnlineTime(getStudentId(session), beginTime, endTime);
        int loginTime = (int) (System.currentTimeMillis() - ((Date) session.getAttribute(TimeConstant.LOGIN_TIME)).getTime()) / 1000;
        if (onlineTime == null) {
            onlineTime = loginTime;
        } else {
            onlineTime += loginTime;
        }
        return onlineTime;
    }

    /**
     * 获取需要奖励的能量值
     *
     * @param student
     * @param point
     * @return
     */
    int getEnergy(Student student, Integer point) {
        int addEnergy = 0;
        if (student.getEnergy() == null) {
            if (point >= 80) {
                student.setEnergy(2);
                addEnergy = 2;
            } else if (point > 20) {
                student.setEnergy(1);
                addEnergy = 1;
            }
        } else {
            if (point >= 80) {
                student.setEnergy(student.getEnergy() + 2);
                addEnergy = 2;
            } else if (point > 20) {
                student.setEnergy(student.getEnergy() + 1);
                addEnergy = 1;
            }
        }
        return addEnergy;
    }

}

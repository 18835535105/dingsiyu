package com.zhidejiaoyu.student.service.impl;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.StudyFlowMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlow;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.student.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zhidejiaoyu.student.controller.BaseController.getParams;

/**
 * @author wuchenxi
 * @date 2018/8/29
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudyFlowMapper studyFlowMapper;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Student getStudent(HttpSession session) {
        Student student =(Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        return studentMapper.selectByPrimaryKey(student.getId());
    }

    @Override
    public Long getStudentId(HttpSession session) {
        return getStudent(session).getId();
    }

    @Override
    public Integer getValidTime(Long studentId, String beginTime, String endTime) {
        Integer time = durationMapper.selectValidTime(studentId, beginTime, endTime);
        return time == null ? 0 : time;
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
     * 计算今天的在线时长
     *
     * @param session
     * @return
     */
    Integer getTodayOnlineTime(HttpSession session) {
        String formatYYYYMMDD = DateUtil.formatYYYYMMDD(new Date());
        return this.getOnLineTime(session, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
    }

    /**
     * 计算今天的有效时长
     *
     * @param studentId
     * @return
     */
    Integer getTodayValidTime(Long studentId) {
        String formatYYYYMMDD = DateUtil.formatYYYYMMDD(new Date());
        return this.getValidTime(studentId, formatYYYYMMDD + " 00:00:00", formatYYYYMMDD + " 23:59:59");
    }

    @Override
    public String getParameters() {
        return getParams(request);
    }

    @Override
    public StudyFlow getCurrentStudyFlow(Long studentId) {
        return studyFlowMapper.selectCurrentFlowByStudentId(studentId);
    }

    /**
     * 学生需要单元测试提示信息
     *
     * @return
     */
    Map<String, Object> toUnitTest() {
        String token = TokenUtil.getToken();
        Map<String, Object> map = new HashMap<>(16);
        map.put("status", TestResponseCode.TO_UNIT_TEST.getCode());
        map.put("msg", TestResponseCode.TO_UNIT_TEST.getMsg());
        map.put("token", token);
        request.getSession().setAttribute("token", token);
        return map;
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

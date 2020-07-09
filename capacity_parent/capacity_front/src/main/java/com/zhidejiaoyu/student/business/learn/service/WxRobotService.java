package com.zhidejiaoyu.student.business.learn.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DailyStateVO;
import com.zhidejiaoyu.student.business.service.BaseService;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/4/7 17:56:56
 */
public interface WxRobotService extends BaseService<Student> {

    /**
     * 获取所有学生当天的飞行记录及打卡状态
     *
     * @param account 学生账号，多个账号间用英文,隔开
     * @return
     */
    List<DailyStateVO> getDailyState(String account);
}

package com.zhidejiaoyu.student.business.currentDayOfStudy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.currentdayofstudy.StudyTimeAndMileageVO;


public interface CurrentDayOfStudyService extends IService<CurrentDayOfStudy> {

    ServerResponse<Object> getCurrentDayOfStudy();

    /**
     * 获取学生当天的智慧飞行记录
     *
     * @param studentId
     * @return
     */
    ServerResponse<Object> getCurrentDayOfStudy(Long studentId);

    /**
     * 获取学生当天飞行时间及飞行历程信息
     *
     * @return
     */
    StudyTimeAndMileageVO getTodayInfo();
}

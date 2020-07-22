package com.zhidejiaoyu.student.business.currentDayOfStudy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.currentdayofstudy.StudyTimeAndMileageVO;

import java.util.List;
import java.util.Map;


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

    /**
     * 获取指定日期的智慧飞行记录
     *
     * @param studentId
     * @param date
     * @return
     */
    ServerResponse<Object> getCurrentDayOfStudyWithDate(Long studentId, String date);

    /**
     * 保存或修改记录
     *
     * @param currentDayOfStudy
     * @return
     */
    Boolean saveOrUpdate1(CurrentDayOfStudy currentDayOfStudy);

    /**
     * 获取课文句型测试中英文
     *
     * @param errorInfo
     * @return
     */
    List<Map<String, String>> getTestList(String errorInfo);

    /**
     * 获取语法模块数据
     *
     * @param errorInfo
     * @return
     */
    List<String> getReturnList(String errorInfo);
}

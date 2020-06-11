package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

/**
 * @author: wuchenxi
 * @date: 2020/6/2 15:21:21
 */
public interface FlyOfStudyService extends BaseService<CurrentDayOfStudy> {

    /**
     * 通过扫码获取学生总学习信息
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> getTotalStudyInfo(String openId);

    /**
     * 通过扫码获取学生指定学习序号的学习信息
     *
     * @param openId
     * @param num    学习序号
     * @return
     */
    ServerResponse<Object> getStudyInfo(String openId, Integer num);

    /**
     * 获取学生指定二维码对应的照片信息
     *
     * @param openId
     * @param num   查询指定序号对应的日期拍摄的照片
     * @return
     */
    ServerResponse<Object> getStudentInfo(String openId, Integer num);
}

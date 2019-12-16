package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.vo.student.StudyLocusVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * 学习轨迹
 *
 * @author wuchenxi
 * @date 2018/8/27
 */
public interface StudyLocusService {

    /**
     * 获取学习路径页面所需数据
     *
     * @param session
     * @param unitId 单元id 如果该字段为空默认初始化学生第一个课程第一单元的数据；如果该字段不为空，默认初始化当前单元的数据
     * @return
     */
    ServerResponse<StudyLocusVo> getStudyLocusInfo(HttpSession session, Long unitId);

    /**
     * 分页获取学习轨迹中的勋章图片
     *
     * @param session
     * @param unitId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<Object> getMedalPage(HttpSession session, Long unitId, Integer pageNum, Integer pageSize);

    /**
     * 分页获取当前课程下所有的单元信息
     *
     * @param session
     * @param courseId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<Object> getUnitPage(HttpSession session, Long courseId, Integer pageNum, Integer pageSize);
}

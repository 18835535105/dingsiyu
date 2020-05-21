package com.zhidejiaoyu.student.business.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.LearnNew;

import javax.servlet.http.HttpSession;

public interface IStudyService extends IService<LearnNew> {

    /**
     * 获取学习内容
     *
     * @param session
     * @param type    1：普通模式；2：暴走模式
     * @return
     */
    Object getStudy(HttpSession session, Long unitId, Integer type);

    /**
     * @param session
     * @param unitId
     * @param wordId
     * @param isTrue   是否答对
     *                 <ul>
     *                 <li>true：答对</li>
     *                 <li>false：答错</li>
     *                 </ul>
     * @param plan
     * @param total
     * @param courseId
     * @param flowId
     * @return
     */
    Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId);


}

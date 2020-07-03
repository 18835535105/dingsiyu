package com.zhidejiaoyu.student.business.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;

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
     * @return
     */
    Object saveStudy(HttpSession session, GetVo getVo);


}

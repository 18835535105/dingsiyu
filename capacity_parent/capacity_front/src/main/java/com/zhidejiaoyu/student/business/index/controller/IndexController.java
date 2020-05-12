package com.zhidejiaoyu.student.business.index.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.dto.UnitInfoDTO;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 单词首页面数据展示
 *
 * @author: wuchenxi
 * @date: 2019/12/21 14:25:25
 */
@Validated
@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private IndexService indexService;

    @Resource
    private IndexCourseInfoService indexCourseInfoService;

    /**
     * 首页数据
     *
     * @return 首页需要展示的数据
     */
    @RequestMapping("/index")
    public ServerResponse<Object> index(HttpSession session) {
        return indexService.index(session);
    }

    /**
     * 首页点击头像
     *
     * @param session
     */
    @RequestMapping("/portrait")
    public ServerResponse<Object> clickPortrait(HttpSession session) {
        return indexService.clickPortrait(session);
    }

    /**
     * 首页右侧小人显示需要复习的单词和句型数
     *
     * @param session
     * @return
     */
    @GetMapping("/getRiepCount")
    public Object getNeedReviewCount(HttpSession session) {
        return indexService.getNeedReviewCount(session);
    }

    /**
     * 获取各个年级课程数据
     *
     * @param type     1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @param courseId 课程id，用于查询课程版本
     * @return
     */
    @GetMapping("/getStudyCourse")
    public ServerResponse<Object> getStudyCourse(Integer type, Long courseId) {
        if (type == null) {
            throw new ServiceException("获取年级课程数据出错！type=null!");
        }
        return indexCourseInfoService.getStudyCourse(type, courseId);
    }

    /**
     * 获取当前单元下所有单元信息
     *
     * @param dto
     * @return
     */
    @GetMapping("/getUnitInfo")
    public ServerResponse<Object> getUnitInfo(@Valid UnitInfoDTO dto, BindingResult result) {
        return indexCourseInfoService.getUnitInfo(dto);
    }
}

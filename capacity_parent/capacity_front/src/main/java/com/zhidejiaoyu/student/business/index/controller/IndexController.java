package com.zhidejiaoyu.student.business.index.controller;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.service.IndexService;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 单词首页面数据展示
 *
 * @author: wuchenxi
 * @date: 2019/12/21 14:25:25
 */
@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private IndexService indexService;

    @Resource
    private IndexCourseInfoService indexCourseInfoService;

    /**
     * 首页数据(单词首页)
     *
     * @return 首页需要展示的数据
     */
    @RequestMapping("/vocabularyIndex")
    public ServerResponse<Object> indexDate(HttpSession session) {
        return indexService.wordIndex(session);
    }

    /**
     * 首页数据(例句首页)
     *
     * @return 首页需要展示的数据
     */
    @RequestMapping("/sentenceIndex")
    public ServerResponse<Object> sentenceIndex(HttpSession session) {
        return indexService.sentenceIndex(session);
    }

    /**
     * 首页点击头像
     *
     * @param type 类型：1.单词；2.句型；3.课文；4.字母、音标
     */
    @RequestMapping("/portrait")
    public ServerResponse<Object> clickPortrait(HttpSession session, Integer type) {
        if (type == null) {
            Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
            log.warn("学生[{} -{} -{}]头像信息中：type=null", student.getId(), student.getAccount(), student.getStudentName());
            type = 1;
        }
        return indexService.clickPortrait(session, type);
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
     * @param type 1：单词；2：句型；3：语法；4：课文
     * @return
     */
    @GetMapping("/getStudyCourse")
    public ServerResponse<CourseInfoVO> getStudyCourse(Integer type) {
        if (type == null) {
            throw new ServiceException("获取年级课程数据出错！type=null!");
        }
        return indexCourseInfoService.getStudyCourse(type);
    }
}

package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/readCourse")
public class ReadCourseController extends BaseController {

    @Autowired
    private ReadCourseService readService;


    /**
     * 获取全部分配的课程
     *
     * @param session
     * @return
     */
    @RequestMapping("/getAllCourse")
    public ServerResponse<Object> getAllCourse(HttpSession session) {
        return readService.getAllCourse(session);
    }

    /**
     * 修改正在学习课程信息
     *
     * @param session
     * @param unitId  要修改的课程id
     * @return
     */
    @RequestMapping("/updStudyPlan")
    public ServerResponse<Object> updStudyPlan(HttpSession session, Long unitId, String grade) {
        return readService.updStudyPlan(session, unitId, grade);
    }

    /**
     * 获取正在学习的课程信息
     *
     * @param session
     * @param unitId
     * @return
     */
    @RequestMapping("/getStudyCourse")
    public ServerResponse<Object> getStudyCourse(HttpSession session, Long unitId, String grade) {
        return readService.getStudyCourse(session, unitId, grade);
    }


    @RequestMapping("/getContent")
    public ServerResponse<Object> getContent(Long typeId, Long courseId) {
        return readService.getContent(typeId, courseId);
    }
}

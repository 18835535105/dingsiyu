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
     * @param session
     * @return
     */
    @RequestMapping("/getAllCourse")
    public ServerResponse<Object> getAllCourse(HttpSession session){
        return readService.getAllCourse(session);
    }


}

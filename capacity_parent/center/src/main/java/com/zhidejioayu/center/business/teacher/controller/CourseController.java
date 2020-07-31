package com.zhidejioayu.center.business.teacher.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.course.CourseFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程数据
 *
 * @author wuchenxi
 * @date 2020-07-31 16:40:32
 */
@RestController
@RequestMapping("/teacher/course")
public class CourseController {

    @Resource
    private CourseFeignClient courseFeignClient;

    /**
     * 获取所有版本
     *
     * @param studyParagraph 学段
     *                       <ul>
     *                       <li>小学</li>
     *                       <li>初中</li>
     *                       <li>高中</li>
     *                       </ul>
     * @return
     */
    @GetMapping("/getAllVersion")
    public ServerResponse<Object> getAllVersion(@RequestParam(required = false) String studyParagraph) {
        List<String> allVersion = courseFeignClient.getAllVersion(studyParagraph);
        return ServerResponse.createBySuccess(allVersion);
    }
}

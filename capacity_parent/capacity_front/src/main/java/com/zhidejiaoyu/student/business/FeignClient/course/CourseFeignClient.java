package com.zhidejiaoyu.student.business.FeignClient.course;

import com.zhidejiaoyu.common.pojo.CourseNew;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:12:12
 */
@FeignClient(name = "course", path = "/course")
public interface CourseFeignClient {

    /**
     * 通过课程id获取课程信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    CourseNew getById(@PathVariable Long id);
}

package com.zhidejiaoyu.student.business.feignclient.course;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "course", path = "/course/course")
public interface CourseCourseFeginClient extends CourseFeignClient{
}

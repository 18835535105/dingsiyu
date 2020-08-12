package com.zhidejiaoyu.student.business.feignclient.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "course", path = "/course/course")
public interface CourseCourseFeginClient extends CourseFeignClient{

    @RequestMapping(value = "/selectIdAndVersionByStudentIdByPhase", method = RequestMethod.GET)
    List<Map<String, Object>> selectIdAndVersionByStudentIdByPhase(@RequestParam Long studentId,@RequestParam String phase);

    /**
     * 查询课程下各个单元单词数
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/selectUnitsWordSum", method = RequestMethod.GET)
    Map<Long, Map<Long, Object>> selectUnitsWordSum(@RequestParam long courseId);
}

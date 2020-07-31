package com.zhidejioayu.center.business.feignclient.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/7/31 16:44:44
 */
@FeignClient(value = "course", path = "/course")
public interface CourseFeignClient {

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
    List<String> getAllVersion(@RequestParam(required = false) String studyParagraph);
}

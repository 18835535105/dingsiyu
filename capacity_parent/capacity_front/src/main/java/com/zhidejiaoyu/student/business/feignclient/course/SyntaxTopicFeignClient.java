package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course", path = "/course/syntaxTopic")
public interface SyntaxTopicFeignClient{

    /**
     * 语法获取数据
     */
    /**
     * 根据id获取语法数据
     *
     * @param id
     * @return
     */
    @GetMapping("/selectSyntaxTopicById/{id}")
    SyntaxTopic selectSyntaxTopicById(@PathVariable Long id);
}

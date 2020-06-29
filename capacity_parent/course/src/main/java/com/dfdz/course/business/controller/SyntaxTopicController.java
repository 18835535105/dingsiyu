package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.SyntaxTopicService;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/course/syntaxTopic")
public class SyntaxTopicController {

    @Resource
    private SyntaxTopicService syntaxTopicService;

    /**
     * 根据sentenceId获取语法数据
     *
     * @param id
     * @return
     */
    @GetMapping("/selectSyntaxTopicById/{id}")
    public SyntaxTopic selectSyntaxTopicById(@PathVariable Long id) {
        return syntaxTopicService.getById(id);
    }

}

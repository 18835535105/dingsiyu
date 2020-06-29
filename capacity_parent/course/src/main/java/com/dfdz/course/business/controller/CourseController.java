package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.*;
import com.zhidejiaoyu.common.pojo.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:07:07
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private UnitService unitService;
    @Resource
    private VocabularyService vocabularyService;
    @Resource
    private SentenceService sentenceService;
    @Resource
    private SyntaxTopicService syntaxTopicService;

    /**
     * 根据courseId获取courseNew数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public CourseNew getById(@PathVariable String id) {
        return courseService.getById(id);
    }

    /**
     * 根据id获取unitNew数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getUnitNewById/{id}")
    public UnitNew getUnitNewById(@PathVariable Long id) {
        return unitService.getById(id);
    }

    /**
     * 根据vocabularyId获取Vocabulary信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getVocabularyById/{id}")
    public Vocabulary selectVocabularyById(@PathVariable Long id) {
        return vocabularyService.getById(id);
    }

    /**
     * 根据sentenceId获取Sentence信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getSentenceById/{id}")
    public Sentence selectSentenceById(@PathVariable Long id) {
        return sentenceService.getById(id);
    }

    /**
     * 根据sentenceId获取Sentence信息
     *
     * @param id
     * @return
     */
    @GetMapping("/selectSyntaxTopicById/{id}")
    public SyntaxTopic selectSyntaxTopicById(@PathVariable Long id) {
        return syntaxTopicService.getById(id);
    }
}

package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.SentenceService;
import com.zhidejiaoyu.common.pojo.Sentence;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/course/sentence")
public class SentenceController {

    @Resource
    private SentenceService sentenceService;


    /**
     * 根据sentenceId获取句型
     *
     * @param id
     * @return
     */
    @GetMapping("/getSentenceById/{id}")
    public Sentence selectSentenceById(@PathVariable Long id) {
        return sentenceService.getById(id);
    }


    @GetMapping("/getSentenceChinsesByUnitIdAndSentenceId/{unitId}/{sentenceId}")
    public String getSentenceChinsesByUnitIdAndSentenceId(@PathVariable Long unitId,@PathVariable Long sentenceId){
         return sentenceService.selectSentenceChineseByUnitIdAndSentenceId(unitId, sentenceId);
    }

}

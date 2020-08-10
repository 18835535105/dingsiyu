package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.SentenceService;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.TeksNew;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 根据unitId和group查询句型id
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/getSentenceIdsByUnitIdAndGroup/{unitId}/{group}")
    public List<Long> getSentenceIdsByUnitIdAndGroup(@PathVariable Long unitId,@PathVariable Integer group){
        return sentenceService.getSentenceIdsByUnitIdAndGroup(unitId,group);
    }


    /**
     * 去掉指定字符查询数据
     * @param sentence
     * @return
     */
    @GetMapping("/replaceSentence")
    public Sentence getReplaceSentece(String sentence) {
        return sentenceService.getReplaceTeks(sentence);
    }


    @GetMapping("/getSentenceIdsByUnitIdAndGroup")
    public Integer countSentenceByUnitIdAndGroup(@RequestParam Long unitId, @RequestParam Integer group){
        return sentenceService.countSentenceByUnitIdAndGroup(unitId,group);
    }

    /**
     * 获取句型下一个句型学习信息
     * @param wordIds
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selectSentenceOneWordNotInIdsNew")
    public Sentence selectSentenceOneWordNotInIdsNew(@RequestParam List<Long> wordIds, @RequestParam Long unitId, @RequestParam Integer group){
        return sentenceService.selectOneWordNotInIdsNew(wordIds,unitId,group);
    }

}

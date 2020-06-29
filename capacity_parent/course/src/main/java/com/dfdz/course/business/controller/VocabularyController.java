package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/course/vocabulary")
public class VocabularyController {


    @Resource
    private VocabularyService vocabularyService;

    /**
     * 根据vocabularyId获取单词信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getVocabularyById/{id}")
    public Vocabulary selectVocabularyById(@PathVariable Long id) {
        return vocabularyService.getById(id);
    }

    /**
     * 获取单元总学习数据数量
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/countLearnVocabularyByUnitIdAndGroup/{unitId}/{group}")
    public Integer countLearnVocabularyByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group) {
        return vocabularyService.countLearnVocabularyByUnitIdAndGroup(unitId, group);
    }

    /**
     * 根据UnitId和group获取单元wordIds
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/getWordIdByUnitIdAndGroup/{unitId}/{group}")
    public List<Long> getWordIdByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group) {
        return vocabularyService.getWordIdByUnitIdAndGroup(unitId, group);
    }

    /**
     * 根据单元id和wordId获取单词中文
     * @param unitId
     * @param wordId
     * @return
     */
    @GetMapping("/getWordChineseByUnitIdAndWordId/{unitId}/{wordId}")
    public String getWordChineseByUnitIdAndWordId(@PathVariable Long unitId, @PathVariable Long wordId) {
        return vocabularyService.getWordChineseByUnitIdAndWordId(unitId, wordId);
    }

    /**
     * 获取下一个学习的单元单词
     * @param wordIds
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/vocabulary/selectOneWordNotInIdsNew/{wordIds}/unitId/group")
    public Vocabulary getOneWordNotInIdsNew(@PathVariable List<Long> wordIds, @PathVariable Long unitId, @PathVariable Integer group){
        return vocabularyService. getOneWordNotInIdsNew( wordIds, unitId, group);
    }
    /**
     *  获取单元中带有图片的单词数量
     */
    @GetMapping("/countWordPictureByUnitId")
    public Integer countWordPictureByUnitId(Long unitId, Integer group){
        return vocabularyService.countWordPictureByUnitId(unitId,group);
    }



}

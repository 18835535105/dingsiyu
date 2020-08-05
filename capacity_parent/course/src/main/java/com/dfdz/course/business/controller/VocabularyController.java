package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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
     *
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
     *
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
     *
     * @param wordId
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selectOneWordNotInIdsNew")
    public Vocabulary getOneWordNotInIdsNew(Long[] wordId, Long unitId, Integer group) {
        List<Long> wordIds = new ArrayList<>();
        if (wordId != null && wordId.length > 0) {
            wordIds = Arrays.asList(wordId);
        }

        return vocabularyService.getOneWordNotInIdsNew(wordIds, unitId, group);
    }

    /**
     * 获取单元中带有图片的单词数量
     */
    @GetMapping("/countWordPictureByUnitId/{unitId}/{group}")
    public Integer countWordPictureByUnitId(@PathVariable Long unitId, @PathVariable Integer group) {
        return vocabularyService.countWordPictureByUnitId(unitId, group);
    }

    /**
     * 获取新学习单词图鉴数据
     */
    @GetMapping("/getStudyNewMap")
    public Map<String, Object> getStudyNewMap(Long unitId, Long[] wordId, Integer type, Integer group) {
        List<Long> wordIds = new ArrayList<>();
        if (wordId != null && wordId.length > 0) {
            wordIds = Arrays.asList(wordId);
        }
        return vocabularyService.selectStudyMap(unitId, wordIds, type, group);
    }


    /**
     * 根据单词查询中文翻译
     *
     * @param word
     */
    @RequestMapping(value = "/getVocabularyChinsesByWordId", method = RequestMethod.GET)
    public String getVocabularyChinsesByWordId(String word) {
        return vocabularyService.getVocabularyChinsesByWordId(word);
    }

    /**
     * 查询单词及单词读音
     *
     * @param words
     * @return <ul>
     * <li>key:word</li>
     * <li>key:readUrl</li>
     * </ul>
     */
    @GetMapping("/getWordAndReadUrlByWords")
    public List<Map<String, String>> getWordAndReadUrlByWords(@RequestParam List<String> words) {
        if (words.isEmpty()) {
            return Collections.emptyList();
        }
        return vocabularyService.getWordAndReadUrlByWords(words);
    }

    /**
     * 根据单元获取单元全部单词
     * @param unitId
     * @return
     */
    @GetMapping("/getVocabularyByUnitId")
    public List<Vocabulary> getVocabularyByUnitId(@RequestParam Long unitId){
        return vocabularyService.getVocabularyByUnitId(unitId);
    }

    /**
     * 获取排除当前数据的中文干扰项
     */
    @GetMapping("/selectChineseByNotVocabularyIds")
    public List<String> selectChineseByNotVocabularyIds(@RequestBody List<Long> vocabularyIds){
        return vocabularyService.selectChineseByNotVocabularyIds(vocabularyIds);
    }

    /**
     * 根据单词id 获取 单词 翻译
     */
    @RequestMapping(value = "/vocabulary/getVocabularyMapByVocabularys", method = RequestMethod.GET)
    public List<Vocabulary> getVocabularyMapByVocabularys(@RequestBody List<Long> vocabularyIds){
        return vocabularyService.getVocabularyMapByVocabularys(vocabularyIds);
    }
}

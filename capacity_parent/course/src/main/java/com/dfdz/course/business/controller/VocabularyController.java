package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import org.apache.commons.collections4.CollectionUtils;
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
    public String getVocabularyChinsesByWordId(@RequestParam String word) {
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
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getVocabularyByUnitId")
    public List<Vocabulary> getVocabularyByUnitId(@RequestParam Long unitId) {
        return vocabularyService.getVocabularyByUnitId(unitId);
    }

    /**
     * 获取排除当前数据的中文干扰项
     */
    @GetMapping("/selectChineseByNotVocabularyIds")
    public List<String> selectChineseByNotVocabularyIds(@RequestParam List<Long> vocabularyIds) {
        return vocabularyService.selectChineseByNotVocabularyIds(vocabularyIds);
    }

    /**
     * 根据单词id 获取 单词 翻译
     */
    @GetMapping("/getVocabularyMapByVocabularys")
    public List<Vocabulary> getVocabularyMapByVocabularys(@RequestParam List<Long> vocabularyIds) {
        return vocabularyService.getVocabularyMapByVocabularys(vocabularyIds);
    }

    /**
     * 获取指定单元下单词摸底测试的测试题
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getSubjectsVOByUnitIds")
    public List<SubjectsVO> getSubjectsVOByUnitIds(@RequestParam List<Long> unitIds) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptyList();
        }
        return vocabularyService.getSubjectsVOByUnitIds(unitIds);
    }

    @RequestMapping(value = "/selectInterferenceTerm", method = RequestMethod.GET)
    public List<String> selectInterferenceTerm(@RequestParam Long unitId, @RequestParam Long vocabularyId, @RequestParam String wordChinese) {
        return vocabularyService.selectInterferenceTerm(unitId, vocabularyId, wordChinese);
    }

    /**
     * 根据unitId获取Vocabulary信息
     *
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/selectByUnitId", method = RequestMethod.GET)
    public List<Vocabulary> selectByUnitId(@RequestParam Long unitId) {
        return vocabularyService.selectByUnitId(unitId);
    }

    /**
     * 根据courseId获取单词信息
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/selectByCourseId", method = RequestMethod.GET)
    public List<Vocabulary> selectByCourseId(@RequestParam Long courseId) {
        return vocabularyService.selectByCourseId(courseId);
    }

    @RequestMapping(value = "/selectByCourseIdWithoutWordIds", method = RequestMethod.GET)
    public List<Vocabulary> selectByCourseIdWithoutWordIds(@RequestParam Long courseId, @RequestParam List<Vocabulary> rightVocabularies) {
        return vocabularyService.selectByCourseIdWithoutWordIds(courseId,rightVocabularies);
    }

    @RequestMapping(value = "/selectByStudentPhase", method = RequestMethod.GET)
    public List<Vocabulary> selectByStudentPhase(@RequestParam String version,@RequestParam int flag){
        return vocabularyService.selectByStudentPhase(version,flag);
    }

    /**
     * 随机获取30道题
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getRandomCourseThirty", method = RequestMethod.GET)
    public List<Vocabulary> getRandomCourseThirty(@RequestParam Long courseId){
        return vocabularyService.getRandomCourseThirty(courseId);
    }

    @RequestMapping(value = "/getStudyParagraphTest", method = RequestMethod.GET)
    public List<Vocabulary> getStudyParagraphTest(@RequestParam String studyParagraph,@RequestParam String model){
        return vocabularyService.getStudyParagraphTest(studyParagraph,model);
    }

    @RequestMapping(value = "/getTestPaperGenerationAll", method = RequestMethod.GET)
    public List<Vocabulary> getTestPaperGenerationAll(@RequestParam long courseId,@RequestParam int typeTwo,@RequestParam String[] unitId){
        return vocabularyService.getTestPaperGenerationAll(courseId,typeTwo,unitId);
    }

    /**
     * 根据unitId和group获取单词数据
     * @param unitId
     * @param group
     * @return
     */
    @RequestMapping(value = "/selectByUnitIdAndGroup", method = RequestMethod.GET)
    public List<Vocabulary> selectByUnitIdAndGroup(@RequestParam Long unitId,@RequestParam Integer group){
        return vocabularyService.selectByUnitIdAndGroup(unitId,group);
    }

    /**
     * 根据单元id获取单元单词数量
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/countByUnitId", method = RequestMethod.GET)
    public long countByUnitId(@RequestParam Long unitId){
        return vocabularyService.countByUnitId(unitId);
    }

    /**
     * 跟据courseId获取单元单词数量
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/countAllCountWordByCourse", method = RequestMethod.GET)
    public int countAllCountWordByCourse(@RequestParam Long courseId){
        return vocabularyService.countAllCountWordByCourse(courseId);
    }

}

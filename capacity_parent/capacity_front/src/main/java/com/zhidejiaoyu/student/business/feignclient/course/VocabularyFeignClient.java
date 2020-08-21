package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "course", path = "/course/vocabulary")
public interface VocabularyFeignClient {
    /**
     * 根据单词查询中文翻译
     *
     * @param word
     */
    @RequestMapping(value = "/getVocabularyChinsesByWordId", method = RequestMethod.GET)
    String getVocabularyChinsesByWordId(@RequestParam("word") String word);

    /**
     * 获取指定单元下单词摸底测试的测试题
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getSubjectsVOByUnitIds")
    List<SubjectsVO> getSubjectsVOByUnitIds(@RequestParam List<Long> unitIds);

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
    List<Map<String, String>> getWordAndReadUrlByWords(@RequestParam List<String> words);

    /**
     * 根据单元获取单元全部单词
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getVocabularyByUnitId")
    List<Vocabulary> getVocabularyByUnitId(@RequestParam Long unitId);
    /**
     * 单词获取数据
     */
    /**
     * 根据id获取单词数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getVocabularyById/{id}")
    Vocabulary selectVocabularyById(@PathVariable Long id);

    /**
     * 查询单元下单词数据数量
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/countLearnVocabularyByUnitIdAndGroup/{unitId}/{group}")
    Integer countLearnVocabularyByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取单元需要学习的单词id
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/getWordIdByUnitIdAndGroup/{unitId}/{group}")
    List<Long> getWordIdByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 根据unitId和wordId获取单词中文
     *
     * @param unitId
     * @param wordId
     * @return
     */
    @GetMapping("/getWordChineseByUnitIdAndWordId/{unitId}/{wordId}")
    String getWordChineseByUnitIdAndWordId(@PathVariable Long unitId, @PathVariable Long wordId);

    /**
     * 获取即将学习的单词数据
     *
     * @param wordIds
     * @param unitId
     * @param group
     * @return
     */
    @RequestMapping(value = "/selectOneWordNotInIdsNew", method = RequestMethod.GET)
    Vocabulary getOneWordNotInIdsNew(@RequestParam("wordId") Long[] wordIds, @RequestParam("unitId") @PathVariable Long unitId,
                                     @RequestParam("group") @PathVariable Integer group);

    /**
     * 获取单元中带有图片的单词数量
     */
    @GetMapping("/countWordPictureByUnitId/{unitId}/{group}")
    Integer countWordPictureByUnitId(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取排除当前数据的中文干扰项
     */
    @RequestMapping(value = "/selectChineseByNotVocabularyIds", method = RequestMethod.GET)
    List<String> selectChineseByNotVocabularyIds(@RequestParam List<Long> vocabularyIds);


    /**
     * 根据单词id 获取 单词 翻译
     */
    @RequestMapping(value = "/getVocabularyMapByVocabularys", method = RequestMethod.GET)
    List<Vocabulary> getVocabularyMapByVocabularys(@RequestParam List<Long> vocabularyIds);

    /**
     * 获取单词图鉴下一个学习的数据
     *
     * @param unitId
     * @param wordId
     * @param type
     * @param group
     * @return
     */
    @RequestMapping(value = "/getStudyNewMap", method = RequestMethod.GET)
    Map<String, Object> getStudyNewMap(@RequestParam("unitId") Long unitId, @RequestParam("wordId") Long[] wordId,
                                       @RequestParam("type") Integer type, @RequestParam("group") Integer group);

    @RequestMapping(value = "/selectInterferenceTerm", method = RequestMethod.GET)
    List<String> selectInterferenceTerm(@RequestParam Long unitId, @RequestParam Long vocabularyId, @RequestParam String wordChinese);

    /**
     * 根据unitId获取Vocabulary信息
     *
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/selectByUnitId", method = RequestMethod.GET)
    List<Vocabulary> selectByUnitId(@RequestParam Long unitId);

    /**
     * 根据courseId获取单词信息
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/selectByCourseId", method = RequestMethod.GET)
    List<Vocabulary> selectByCourseId(@RequestParam Long courseId);

    @RequestMapping(value = "/selectByCourseIdWithoutWordIds", method = RequestMethod.GET)
    List<Vocabulary> selectByCourseIdWithoutWordIds(@RequestParam Long courseId, @RequestParam List<Vocabulary> rightVocabularies);

    /**
     * 根据学生学段获取单词信息
     * <p>初中学段获取当前教材版本的初一单词信息</p>
     * <p>高中学段获取当前教材版本的高一单词信息</p>
     *
     * @param version
     * @param flag    1:初一教材  2：高一教材 3:必修一教材(年级只有高中的时候）
     * @return
     */
    @RequestMapping(value = "/selectByStudentPhase", method = RequestMethod.GET)
    List<Vocabulary> selectByStudentPhase(@RequestParam String version, @RequestParam int flag);

    /**
     * 随机获取30道题
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/getRandomCourseThirty", method = RequestMethod.GET)
    List<Vocabulary> getRandomCourseThirty(@RequestParam Long courseId);

    @RequestMapping(value = "/getStudyParagraphTest", method = RequestMethod.GET)
    List<Vocabulary> getStudyParagraphTest(@RequestParam String studyParagraph, @RequestParam String model);

    @RequestMapping(value = "/getTestPaperGenerationAll", method = RequestMethod.GET)
    List<Vocabulary> getTestPaperGenerationAll(@RequestParam long courseId, @RequestParam int typeTwo, @RequestParam String[] unitId);

    /**
     * 根据unitId和group获取单词数据
     *
     * @param unitId
     * @param group
     * @return
     */
    @RequestMapping(value = "/selectByUnitIdAndGroup", method = RequestMethod.GET)
    List<Vocabulary> selectByUnitIdAndGroup(@RequestParam Long unitId, @RequestParam Integer group);

    /**
     * 根据单元id获取单元单词数量
     *
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/countByUnitId", method = RequestMethod.GET)
    long countByUnitId(@RequestParam Long unitId);

    /**
     * 跟据courseId获取单元单词数量
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/countAllCountWordByCourse", method = RequestMethod.GET)
    int countAllCountWordByCourse(@RequestParam Long courseId);

    /**
     * 从unitIds中过滤出含有单词的单元
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getUnitIdsByUnitIds")
    Map<Long, Long> getUnitIdsByUnitIds(@RequestParam List<Long> unitIds);
}

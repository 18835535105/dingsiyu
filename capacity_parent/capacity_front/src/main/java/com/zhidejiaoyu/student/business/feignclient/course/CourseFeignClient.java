package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:12:12
 */
@FeignClient(name = "course", path = "/course")
public interface CourseFeignClient {

    /**
     * 获取课程数据
     */
    /**
     * []
     * 根据id获取课程数据信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    CourseNew getById(@PathVariable Long id);

    /**
     * 通过单元id查询当前课程所属学段
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getPhaseByUnitId/{unitId}")
    String getPhaseByUnitId(@PathVariable Long unitId);

    /**
     * 根据单元id查询单元与年级的关系
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getGradeAndLabelByUnitIds")
    List<GradeAndUnitIdDTO> getGradeAndLabelByUnitIds(@RequestParam List<Long> unitIds);

    /**
     * 批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIds")
    List<CourseNew> getByIds(@RequestParam List<Long> courseIds);

    /**
     * 获取学生可以学习的版本集合
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIdsGroupByVersion")
    List<CourseNew> getByIdsGroupByVersion(@RequestParam List<Long> courseIds);

    /**
     * 各个课程下所有单元个数
     *
     * @param courseIds
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/countUnitByIds")
    Map<Long, Integer> countUnitByIds(@RequestParam List<Long> courseIds, @RequestParam int type);

    /**
     * 当前版本中小于或等于当前年级的所有课程id
     *
     * @param version   版本
     * @param gradeList 年级
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/getByGradeListAndVersionAndGrade")
    List<Long> getByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList, @RequestParam Integer type);

    /**
     * 获取当前课程的语法数据
     *
     * @param courseNews
     * @return
     */
    @PostMapping("/getByCourseNews")
    Map<Long, Map<String, Object>> getByCourseNews(@RequestBody List<CourseNew> courseNews);

    /**
     * 获取单元数据
     */
    /**
     * 根据id获取单元信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getUnitNewById/{id}")
    UnitNew getUnitNewById(@PathVariable Long id);

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    @GetMapping("/unit/getUnitIdsByCourseNames")
    List<Long> getUnitIdsByCourseNames(@RequestParam List<String> courseNames);

    /**
     * 批量查询单元信息
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/unit/getUnitNewsByIds")
    List<UnitNew> getUnitNewsByIds(@RequestParam List<Long> unitIds);

    /**
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    @GetMapping("/unit/getUnitIdsByGradeListAndVersionAndGrade")
    List<Long> getUnitIdsByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    @GetMapping("/unit/getLessOrEqualsCurrentIdByCourseIdAndUnitId")
    List<Long> getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(@RequestParam Long courseId, @RequestParam Long unitId);

    /**
     * 根据类型查询单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；4：课文
     * @return
     */
    @GetMapping("/unit/getMaxGroupByUnitIsdAndType")
    ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(@RequestParam List<Long> unitIds, @RequestParam Integer type);

    /**
     * 根据单词查询中文翻译
     *
     * @param word
     */
    @RequestMapping(value = "/vocabulary/getVocabularyChinsesByWordId", method = RequestMethod.GET)
    String getVocabularyChinsesByWordId(@RequestParam("word") String word);

    /**
     * 获取指定单元下单词摸底测试的测试题
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/vocabulary/getSubjectsVOByUnitIds")
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
    @GetMapping("/vocabulary/getWordAndReadUrlByWords")
    List<Map<String, String>> getWordAndReadUrlByWords(@RequestParam List<String> words);

    /**
     * 根据单元获取单元全部单词
     *
     * @param unitId
     * @return
     */
    @GetMapping("/vocabulary/getVocabularyByUnitId")
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
    @GetMapping("/vocabulary/getVocabularyById/{id}")
    Vocabulary selectVocabularyById(@PathVariable Long id);

    /**
     * 查询单元下单词数据数量
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/vocabulary/countLearnVocabularyByUnitIdAndGroup/{unitId}/{group}")
    Integer countLearnVocabularyByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取单元需要学习的单词id
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/vocabulary/getWordIdByUnitIdAndGroup/{unitId}/{group}")
    List<Long> getWordIdByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 根据unitId和wordId获取单词中文
     *
     * @param unitId
     * @param wordId
     * @return
     */
    @GetMapping("/vocabulary/getWordChineseByUnitIdAndWordId/{unitId}/{wordId}")
    String getWordChineseByUnitIdAndWordId(@PathVariable Long unitId, @PathVariable Long wordId);

    /**
     * 获取即将学习的单词数据
     *
     * @param wordIds
     * @param unitId
     * @param group
     * @return
     */
    @RequestMapping(value = "/vocabulary/selectOneWordNotInIdsNew", method = RequestMethod.GET)
    Vocabulary getOneWordNotInIdsNew(@RequestParam("wordId") Long[] wordIds, @RequestParam("unitId") @PathVariable Long unitId,
                                     @RequestParam("group") @PathVariable Integer group);

    /**
     * 获取单元中带有图片的单词数量
     */
    @GetMapping("/vocabulary/countWordPictureByUnitId/{unitId}/{group}")
    Integer countWordPictureByUnitId(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取排除当前数据的中文干扰项
     */
    @RequestMapping(value = "/vocabulary/selectChineseByNotVocabularyIds", method = RequestMethod.GET)
    List<String> selectChineseByNotVocabularyIds(@RequestParam List<Long> vocabularyIds);


    /**
     * 根据单词id 获取 单词 翻译
     */
    @RequestMapping(value = "/vocabulary/getVocabularyMapByVocabularys", method = RequestMethod.GET)
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
    @RequestMapping(value = "/vocabulary/getStudyNewMap", method = RequestMethod.GET)
    Map<String, Object> getStudyNewMap(@RequestParam("unitId") Long unitId, @RequestParam("wordId") Long[] wordId,
                                       @RequestParam("type") Integer type, @RequestParam("group") Integer group);


    /**
     * 获取句型数据`
     */
    /**
     * 根据id获取句型数据
     *
     * @param id
     * @return
     */
    @GetMapping("/sentence/getSentenceById/{id}")
    Sentence selectSentenceById(@PathVariable Long id);

    /**
     * 去掉指定字符查询数据
     *
     * @param sentence
     * @return
     */
    @RequestMapping(value = "/sentence/replaceSentence", method = RequestMethod.GET)
    public Sentence getReplaceSentece(@RequestParam("sentence") String sentence);

    /**
     * 根据unitId和sentenceId获取句型中文
     *
     * @param unitId
     * @param sentenceId
     * @return
     */
    @GetMapping("/sentence/getSentenceChinsesByUnitIdAndSentenceId/{unitId}/{sentenceId}")
    String getSentenceChinsesByUnitIdAndSentenceId(@PathVariable Long unitId, @PathVariable Long sentenceId);

    /**
     * 根据unitId和group查询句型id
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/sentence/getSentenceIdsByUnitIdAndGroup/{unitId}/{group}")
    List<Long> getSentenceIdsByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 语法获取数据
     */
    /**
     * 根据id获取语法数据
     *
     * @param id
     * @return
     */
    @GetMapping("/syntaxTopic/selectSyntaxTopicById/{id}")
    SyntaxTopic selectSyntaxTopicById(@PathVariable Long id);


    /**
     * 课文获取数据
     */
    /**
     * 根据单元和group获取课文数据
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/centerTeks/selTeksByUnitIdAndGroup/{unitId}/{group}")
    List<TeksNew> selTeksByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取20个句型信息
     *
     * @return
     */
    @GetMapping("/centerTeks/getTwentyTeks")
    List<TeksNew> getTwentyTeks();

    /**
     * 去掉指定字符查询数据
     *
     * @param sentence
     * @return
     */
    @RequestMapping(value = "/centerTeks/replaceTeks", method = RequestMethod.GET)
    TeksNew replaceTeks(@RequestParam("sentence") String sentence);


}

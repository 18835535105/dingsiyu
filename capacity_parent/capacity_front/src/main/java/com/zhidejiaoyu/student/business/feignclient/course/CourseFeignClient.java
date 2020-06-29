package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
     * 根据id获取课程数据信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    CourseNew getById(@PathVariable Long id);

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
    @GetMapping("/vocabulary/selectOneWordNotInIdsNew/{wordIds}/{unitId}/{group}")
    Vocabulary getOneWordNotInIdsNew(@PathVariable List<Long> wordIds, @PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取单元中带有图片的单词数量
     */
    @GetMapping("/vocabulary/countWordPictureByUnitId/{unitId}/{group}")
    Integer countWordPictureByUnitId(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取单词图鉴下一个学习的数据
     *
     * @param unitId
     * @param longs
     * @param type
     * @param group
     * @return
     */
    @GetMapping("/vocabulary/getStudyNewMap/{unitId}/{longs}/{type}/{group}")
    Map<String, Object> getStudyNewMap(Long unitId, List<Long> longs, Integer type, Integer group);


    /**
     * 获取句型数据
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
    @GetMapping("/teks/selTeksByUnitIdAndGroup/{unitId}/{group}")
    List<TeksNew> selTeksByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 获取20个句型信息
     *
     * @return
     */
    @GetMapping("/teks/getTwentyTeks")
    List<TeksNew> getTwentyTeks();


}

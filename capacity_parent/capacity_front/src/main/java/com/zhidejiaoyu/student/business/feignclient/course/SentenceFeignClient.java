package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.Sentence;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "course", path = "/course/sentence")
public interface SentenceFeignClient {

    /**
     * 获取句型数据`
     */
    /**
     * 根据id获取句型数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getSentenceById/{id}")
    Sentence selectSentenceById(@PathVariable Long id);

    /**
     * 去掉指定字符查询数据
     *
     * @param sentence
     * @return
     */
    @RequestMapping(value = "/replaceSentence", method = RequestMethod.GET)
    public Sentence getReplaceSentece(@RequestParam("sentence") String sentence);

    /**
     * 根据unitId和sentenceId获取句型中文
     *
     * @param unitId
     * @param sentenceId
     * @return
     */
    @GetMapping("/getSentenceChinsesByUnitIdAndSentenceId/{unitId}/{sentenceId}")
    String getSentenceChinsesByUnitIdAndSentenceId(@PathVariable Long unitId, @PathVariable Long sentenceId);

    /**
     * 根据unitId和group查询句型id
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/getSentenceIdsByUnitIdAndGroup/{unitId}/{group}")
    List<Long> getSentenceIdsByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group);

    /**
     * 根据unitId和group查询句型单词总数
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/getSentenceIdsByUnitIdAndGroup")
    Integer countSentenceByUnitIdAndGroup(@RequestParam Long unitId, @RequestParam Integer group);

    /**
     * 获取句型下一个句型学习信息
     *
     * @param wordIds
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selectSentenceOneWordNotInIdsNew")
    Sentence selectSentenceOneWordNotInIdsNew(@RequestParam List<Long> wordIds, @RequestParam Long unitId, @RequestParam Integer group);

    /**
     * 根据unitId和group获取游戏数据
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selectSentenceAndChineseByUnitIdAndGroup")
    List<Map<String, Object>> selectSentenceAndChineseByUnitIdAndGroup(@RequestParam Long unitId, @RequestParam Integer group);

    /**
     * 根据unitId和group获取数据
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selectByUnitIdAndGroup")
    List<Sentence> selectByUnitIdAndGroup(@RequestParam Long unitId,@RequestParam Integer group);

    /**
     * 根据课程id获取打乱句型信息
     * @param courseId
     * @return
     */
    @GetMapping("/selectRoundSentence")
    List<Sentence> selectRoundSentence(@RequestParam Long courseId);
}

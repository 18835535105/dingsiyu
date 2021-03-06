package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;

import java.util.List;
import java.util.Map;

public interface VocabularyService extends IService<Vocabulary> {

    Integer countLearnVocabularyByUnitIdAndGroup(Long unitId, Integer group);

    List<Long> getWordIdByUnitIdAndGroup(Long unitId, Integer group);

    String getWordChineseByUnitIdAndWordId(Long unitId, Long wordId);

    Vocabulary getOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group);

    Integer countWordPictureByUnitId(Long unitId, Integer group);

    Map<String, Object> selectStudyMap(Long unitId, List<Long> wordIds, Integer type, Integer group);

    String getVocabularyChinsesByWordId(String word);

    /**
     * 查询单词及单词读音
     *
     * @param words
     * @return
     */
    List<Map<String, String>> getWordAndReadUrlByWords(List<String> words);

    List<Vocabulary> getVocabularyByUnitId(Long unitId);

    List<String> selectChineseByNotVocabularyIds(List<Long> vocabularyIds);

    List<Vocabulary> getVocabularyMapByVocabularys(List<Long> vocabularyIds);

    /**
     * 获取指定单元下单词的测试题
     *
     * @param unitIds
     * @return
     */
    List<SubjectsVO> getSubjectsVOByUnitIds(List<Long> unitIds);

    List<String> selectInterferenceTerm(Long unitId, Long vocabularyId, String wordChinese);

    List<Vocabulary> selectByUnitId(Long unitId);

    List<Vocabulary> selectByCourseId(Long courseId);

    List<Vocabulary> selectByCourseIdWithoutWordIds(Long courseId, List<Vocabulary> rightVocabularies);

    List<Vocabulary> selectByStudentPhase(String version, int flag);

    List<Vocabulary> getRandomCourseThirty(Long courseId);

    List<Vocabulary> getStudyParagraphTest(String studyParagraph, String model);

    List<Vocabulary> getTestPaperGenerationAll(long courseId, int typeTwo, String[] unitId);

    List<Vocabulary> selectByUnitIdAndGroup(Long unitId, Integer group);

    long countByUnitId(Long unitId);

    int countAllCountWordByCourse(Long courseId);

    /**
     * 从unitIds中过滤出含有单词的单元
     *
     * @param unitIds
     * @return
     */
    Map<Long, Long> getUnitIdsByUnitIds(List<Long> unitIds);
}

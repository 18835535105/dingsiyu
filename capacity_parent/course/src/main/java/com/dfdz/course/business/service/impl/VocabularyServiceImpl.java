package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.mapper.UnitVocabularyNewMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleVocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class VocabularyServiceImpl extends ServiceImpl<VocabularyMapper, Vocabulary> implements VocabularyService {

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

    @Resource
    private SimpleVocabularyMapper simpleVocabularyMapper;


    @Override
    public Integer countLearnVocabularyByUnitIdAndGroup(Long unitId, Integer group) {
        return unitVocabularyNewMapper.countByUnitIdAndGroup(unitId, group);
    }

    @Override
    public List<Long> getWordIdByUnitIdAndGroup(Long unitId, Integer group) {
        return unitVocabularyNewMapper.selectWordIdByUnitIdAndGroup(unitId, group);
    }

    @Override
    public String getWordChineseByUnitIdAndWordId(Long unitId, Long wordId) {
        return unitVocabularyNewMapper.selectWordChineseByUnitIdAndWordId(unitId, wordId);
    }

    @Override
    public Vocabulary getOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group) {
        return vocabularyMapper.selectOneWordNotInIdsNew(wordIds, unitId, group);
    }

    @Override
    public Integer countWordPictureByUnitId(Long unitId, Integer group) {
        return unitVocabularyNewMapper.countWordPictureByUnitId(unitId, group);
    }

    @Override
    public Map<String, Object> selectStudyMap(Long unitId, List<Long> wordIds, Integer type, Integer group) {

        return unitVocabularyNewMapper.selectStudyMap(unitId, wordIds, type, group);
    }

    @Override
    public String getVocabularyChinsesByWordId(String word) {
        Vocabulary vocabulary = vocabularyMapper.selectByWord(word);
        return vocabulary != null ? vocabulary.getWordChinese() : null;
    }

    @Override
    public List<Map<String, String>> getWordAndReadUrlByWords(List<String> words) {
        List<Map<String, String>> maps = vocabularyMapper.selectWordAndReadUrlByWords(words);
        maps.forEach(m -> m.put("readUrl", GetOssFile.getPublicObjectUrl(m.get("readUrl"))));
        return maps;
    }

    @Override
    public List<Vocabulary> getVocabularyByUnitId(Long unitId) {
        return vocabularyMapper.selectByUnitId(unitId);
    }

    @Override
    public List<String> selectChineseByNotVocabularyIds(List<Long> vocabularyIds) {
        return vocabularyMapper.selectChineseByNotVocabularyIds(vocabularyIds);
    }

    @Override
    public List<Vocabulary> getVocabularyMapByVocabularys(List<Long> vocabularyIds) {
        return vocabularyMapper.selectByWordIds(vocabularyIds);
    }

    @Override
    public List<SubjectsVO> getSubjectsVOByUnitIds(List<Long> unitIds) {
        return vocabularyMapper.selectSubjectsVOByUnitIds(unitIds);
    }

    @Override
    public List<String> selectInterferenceTerm(Long unitId, Long vocabularyId, String wordChinese) {
        return unitVocabularyNewMapper.selectInterferenceTerm(unitId, vocabularyId, wordChinese);
    }

    @Override
    public List<Vocabulary> selectByUnitId(Long unitId) {
        return vocabularyMapper.selectByUnitId(unitId);
    }

    @Override
    public List<Vocabulary> selectByCourseId(Long courseId) {
        return vocabularyMapper.selectByCourseId(courseId);
    }

    @Override
    public List<Vocabulary> selectByCourseIdWithoutWordIds(Long courseId, List<Vocabulary> rightVocabularies) {
        return vocabularyMapper.selectByCourseIdWithoutWordIds(courseId, rightVocabularies);
    }

    @Override
    public List<Vocabulary> selectByStudentPhase(String version, int flag) {
        return vocabularyMapper.selectByStudentPhase(version, flag);
    }

    @Override
    public List<Vocabulary> getRandomCourseThirty(Long courseId) {
        return vocabularyMapper.getRandomCourseThirty(courseId);
    }

    @Override
    public List<Vocabulary> getStudyParagraphTest(String studyParagraph, String model) {
        return simpleVocabularyMapper.getStudyParagraphTest(studyParagraph, model);
    }

    @Override
    public List<Vocabulary> getTestPaperGenerationAll(long courseId, int typeTwo, String[] unitId) {
        return simpleVocabularyMapper.getTestPaperGenerationAll(courseId, typeTwo, unitId);
    }

    @Override
    public List<Vocabulary> selectByUnitIdAndGroup(Long unitId, Integer group) {
        return vocabularyMapper.selectByUnitIdAndGroup(unitId, group);
    }

    @Override
    public long countByUnitId(Long unitId) {
        return unitVocabularyNewMapper.countByUnitId(unitId);
    }

    @Override
    public int countAllCountWordByCourse(Long courseId) {
        return unitVocabularyNewMapper.countAllCountWordByCourse(courseId);
    }


}

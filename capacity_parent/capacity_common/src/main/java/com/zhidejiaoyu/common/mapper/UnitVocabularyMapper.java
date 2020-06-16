package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitVocabulary;
import com.zhidejiaoyu.common.pojo.UnitVocabularyExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface UnitVocabularyMapper {
    int countByExample(UnitVocabularyExample example);

    int deleteByExample(UnitVocabularyExample example);

    int insert(UnitVocabulary record);

    int insertSelective(UnitVocabulary record);

    List<UnitVocabulary> selectByExample(UnitVocabularyExample example);

    int updateByExampleSelective(@Param("record") UnitVocabulary record,
                                 @Param("example") UnitVocabularyExample example);


    /**
     * 根据课程id查询出当前单元的所有单词
     *
     * @param courseId
     * @return key：单词id value: map  key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询指定单元一批单词的释义
     *
     * @param unitId
     * @param idSet  单词id集合
     * @return map key:单词id， map:key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByUnitIdAndWordIds(@Param("unitId") Long unitId, @Param("idSet") Set<Long> idSet);

    /**
     * 查询当前课程一批单词的释义
     *
     * @param courseId 当前课程id
     * @param idSet    单词id集合
     * @return map key:单词id， map:key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByCourseIdIdAndWordIds(@Param("courseId") Long courseId, @Param("idSet") Set<Long> idSet);

    /**
     * 查询当前单元下所有单词个数
     *
     * @param unitId
     * @return
     */
    @Select("select COUNT(b.id) from unit_vocabulary a JOIN vocabulary b ON a.vocabulary_id = b.id AND b.delStatus = 1 AND a.unit_id = #{unitId}")
    Integer allCountWord(@Param("unitId") Long unitId);

    /**
     * 根据课程id和单词id查询
     *
     * @param courseId
     * @param wordId
     * @return
     */
    List<UnitVocabulary> selectByCourseIdAndWordId(@Param("courseId") Long courseId, @Param("wordId") Long wordId);

    /**
     * 查询单元下所有的单词数
     *
     * @param unitIds
     * @return
     */
    int countWordByUnitIds(@Param("unitIds") List<Long> unitIds);

    /**
     * 查询指定模块下当前单元的单词个数
     *
     * @param unitId     单元id
     * @param studyModel 学习模块 慧记忆，慧听写，慧默写，单词图鉴
     * @return
     */
    long countByUnitIdAndStudyModel(@Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询指定模块下当前单元的单词个数
     *
     * @param courseId   课程id
     * @param studyModel 学习模块 慧记忆，慧听写，慧默写，单词图鉴
     * @return
     */
    Long countByCourseIdAndStudyModel(@Param("courseId") Long courseId, @Param("studyModel") String studyModel);

    /**
     * 查询当前单元下含有图片的单词总个数
     *
     * @param unitId
     * @return
     */
    @Select("SELECT count(v.id) FROM unit_vocabulary_new uv, vocabulary v WHERE uv.vocabulary_id = v.id AND v.delStatus = 1 AND recordpicurl IS NOT NULL AND uv.unit_id = #{unitId}")
    int countWordPictureByUnitId(@Param("unitId") Long unitId);

    /**
     * 查询指定课程一批单词的释义
     *
     * @param courseId
     * @param idSet  单词id集合
     * @return map key:单词id， map:key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, Object>> selectWordChineseMapByCourseIdAndWordIds(@Param("courseId") Long courseId, @Param("idSet") Set<Long> idSet);

    List<Map<String, Object>> selUnitIdAndNameByCourseIdsAndStartUnitIdAndEndUnitId(@Param("courseId") Long courseId
            ,@Param("startUnitId") Long startUnitId,@Param("endUnitId") Long endUnitId);

    /**
     * 查询出三个本单元非当前单词的单词释义
     *
     * @param unitId       单元 id
     * @param vocabularyId 单词 id
     * @return
     */
    List<String> selectWordChineseByUnitIdAndCurrentWordId(@Param("unitId") Long unitId, @Param("vocabularyId") Long vocabularyId);

    /**
     * 查询当前课程下不在当前单元的单词释义
     *
     * @param courseId
     * @param unitId
     * @param limitSize 查询数据量
     * @return
     */
    List<String> selectWordChineseByCourseIdAndNotInUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("limitSize") int limitSize);

    /**
     * 根据单词 id 随机查询一个单元的 id
     *
     * @param wordId
     * @return
     */
    Long selectOneUnitIdByVocabularyId(@Param("wordId") Long wordId);
}

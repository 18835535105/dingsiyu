package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitVocabulary;
import com.zhidejiaoyu.common.pojo.UnitVocabularyExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UnitVocabularyMapper {
    int countByExample(UnitVocabularyExample example);

    int deleteByExample(UnitVocabularyExample example);

    int insert(UnitVocabulary record);

    int insertSelective(UnitVocabulary record);

    List<UnitVocabulary> selectByExample(UnitVocabularyExample example);

    int updateByExampleSelective(@Param("record") UnitVocabulary record,
                                 @Param("example") UnitVocabularyExample example);

    int updateByExample(@Param("record") UnitVocabulary record, @Param("example") UnitVocabularyExample example);

    /**
     * 根据单词id获取所有对应的单元id
     *
     * @param id 单词id
     * @return
     */
    @Select("SELECT unit_id FROM unit_vocabulary WHERE vocabulary_id = #{id}")
    List<Long> selectUnitIsByWordId(@Param("id") Long id);

    /**
     * 根据单元id获取所对应的所有单词id
     *
     * @param unitId
     * @return
     */
    @Select("select vocabulary_id from unit_vocabulary where unit_id=#{unitId} order by vocabulary_id desc")
    List<Long> selectWordIdsByUnitId(Long unitId);

    /**
     * 获取当前单元下的所有不是删除状态单词的总个数
     *
     * @param unitId
     * @return
     */
    @Select("select count(distinct uv.vocabulary_id) from unit_vocabulary uv where uv.unit_id=#{unitId} and (select v.delStatus from vocabulary v where v.id = uv.vocabulary_id) = 1")
    Long countByUnitId(Long unitId);
    /**
     * 获取当前课程下的所有不是删除状态单词的总个数
     *
     * @param courseId
     * @return
     */
    @Select("select count(distinct uv.vocabulary_id) from unit_vocabulary uv, unit u where u.id = uv.unit_id and u.course_id = #{courseId} and (select v.delStatus from vocabulary v where v.id = uv.vocabulary_id) = 1")
    Long countByCourseId(Long courseId);

    @Select("SELECT uv.classify FROM unit_vocabulary uv WHERE uv.vocabulary_id= #{id}  and  uv.unit_id = (SELECT u.id FROM unit u WHERE u.joint_name = #{courseUnit} )")
    Integer selectClassify(@Param("id") Long id, @Param("courseUnit") String courseUnit);

    /**
     * 根据单元 ids 和 单词 id 查找 UnitVocabulary 集合
     *
     * @param ids 单元 ids
     * @param id  单词 id
     * @return
     */
    List<UnitVocabulary> selectByUnitIds(@Param("ids") List<Long> ids, @Param("id") Long id);

    /**
     * 查询当前单元下所有单词的个数
     *
     * @param unitId
     * @return
     */
    @Select("SELECT count(uv.unit_id) FROM unit_vocabulary uv,vocabulary v where uv.unit_id = #{unitId} AND uv.vocabulary_id = v.id AND v.delStatus = 1")
    Long selectWordCountByUnitId(Long unitId);

    /**
     * 根据单元id和单词id查找单词的释义
     *
     * @param unitId
     * @param wordId
     * @return
     */
    @Select("select word_chinese from unit_vocabulary where unit_id = #{unitId} and vocabulary_id = #{wordId} ")
    String selectWordChineseByUnitIdAndWordId(@Param("unitId") Long unitId, @Param("wordId") Long wordId);

    /**
     * 根据单元id查询出当前单元的所有单词
     *
     * @param unitId
     * @return key：单词id value: map  key:单词id，value：单词释义
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectWordChineseMapByUnitId(@Param("unitId") Long unitId);

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

    int insertWordToUnit(@Param("wordId") Integer wordId, @Param("unitId") Integer unitId, @Param("word_chinese") Object word_chinese, @Param("classify") Object classify);

    /**
     * 根据学生所学课程和单词id查找当前单词所属的单元id
     *
     * @param wordId
     * @param courseId
     * @return
     */
    @Select("select DISTINCT u.id from unit_vocabulary uv,unit u where uv.unit_id = u.id and uv.vocabulary_id= #{wordId} and u.course_id = #{courseId}")
    Long selectUnitIdByWordIdAndCourseId(@Param("wordId") Long wordId, @Param("courseId") Long courseId);

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

    @Delete("delete from unit_vocabulary where vocabulary_id = #{id}")
    void delWord(String id);

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

    @Select("SELECT count(uv.unit_id) FROM unit_vocabulary uv,vocabulary v where uv.unit_id = #{unitId} AND uv.vocabulary_id = v.id AND v.recordpicurl is not NULL AND v.delStatus = 1 ")
    Long selectWordPicCountByUnitId(@Param("unitId") Integer unitId);

    /**
     * 查询当前单元下含有图片的单词总个数
     *
     * @param unitId
     * @return
     */
    @Select("SELECT count(v.id) FROM unit_vocabulary uv, vocabulary v WHERE uv.vocabulary_id = v.id AND v.delStatus = 1 AND recordpicurl IS NOT NULL AND uv.unit_id = #{unitId}")
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

    @Select("SELECT count(DISTINCT(c.id)) FROM	unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{courseId} AND c.delStatus = 1 ")
    int getAllCountWordByCourse(Long courseId);
}
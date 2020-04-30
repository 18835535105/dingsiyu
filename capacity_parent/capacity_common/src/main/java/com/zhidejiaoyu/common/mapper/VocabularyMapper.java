package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.UnitVocabulary;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VocabularyMapper extends BaseMapper<Vocabulary> {

    Vocabulary selectByPrimaryKey(Long id);

    /**
     * 根据单词的 ids 获取单词集合
     *
     * @param wordIds
     * @return
     */
    List<Vocabulary> selectByWordIds(@Param("ids") List<Long> wordIds);

    /**
     * 获取当前单元下的所有单词信息(不含删除的单词)
     *
     * @param unitId
     * @return
     */
    List<Vocabulary> selectByUnitId(@Param("unitId") Long unitId);

    /**
     * 获取当前单元下的所有单词信息(不含删除的单词)
     *
     * @param unitId
     * @return
     */
    List<Vocabulary> selectByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 根据单词id查询该单词的中文意思
     *
     * @param id
     * @return
     */
    @Select("select word_chinese from vocabulary where id = #{id}")
    String selectWordChineseById(Long id);

    /**
     * 根据学生id查询当前单元下指定模块的所有生词/熟词
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @param condition  查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<Vocabulary> selectUnknownWordByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                               @Param("studyModel") String studyModel, @Param("condition") Integer condition);

    /**
     * 根据学生id查询当前单元下指定模块的未学习的所有单词
     *
     * @param studentId
     * @param unitId
     * @param studyModel
     * @return
     */
    List<Vocabulary> selectUnlearnedByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                             @Param("studyModel") String studyModel);

    /**
     * 根据学生id查询当前课程下指定模块的未学习的所有单词
     *
     * @param studentId  学生id
     * @param courseId   课程id
     * @param studyModel 学习模块
     * @return
     */
    List<Vocabulary> selectUnlearnedByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                               @Param("studyModel") String studyModel);

    /**
     * 取单元下一个未学习过的单词
     *
     * @param unitId 单元id
     * @param id     学生id
     * @return 单词数据
     */
    Vocabulary showWord(@Param("unit_id") String unitId, @Param("id") Long id);

    /**
     * 获取当前课程下的单词总量
     *
     * @param courseId
     * @param flag     1：单词图鉴 2：其他
     * @return
     */
    int countByCourseId(@Param("courseId") Long courseId, @Param("flag") Integer flag);

    /**
     * 根据学生学段获取单词信息
     * <p>初中学段获取当前教材版本的初一单词信息</p>
     * <p>高中学段获取当前教材版本的高一单词信息</p>
     *
     * @param student
     * @param flag    1:初一教材  2：高一教材 3:必修一教材(年级只有高中的时候）
     * @return
     */
    List<Vocabulary> selectByStudentPhase(@Param("student") Student student, @Param("flag") int flag);

    /**
     * 获取课程下的单词数
     *
     * @param courseId
     * @return
     */
    @Select("select COUNT(c.id) FROM unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND c.delStatus = 1 AND a.course_id = #{course_id}")
    Integer courseCountVocabulary(@Param("course_id") Long courseId);

    /**
     * 查找指定单词的音节
     *
     * @param ids 单词id
     * @return map key:单词id  value：map key：单词id value：单词音节
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectSyllableByWordId(@Param("ids") List<Long> ids);

    /**
     * 查询当前单元未学习的首个单词
     *
     * @param wordIds
     * @param unitId
     * @return
     */
    Vocabulary selectOneWordNotInIds(@Param("wordIds") List<Long> wordIds, @Param("unitId") Long unitId);

    /**
     * 根据单词查询当前单词的id
     *
     * @param word
     * @return
     */
    List<Vocabulary> selectIdsByWord(@Param("word") String word);

    /**
     * 获取当前课程的单词信息（不含删除的单词）
     *
     * @param courseId 当前课程id
     * @return
     */
    List<Vocabulary> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生id查询当前课程下指定模块的所有生词/熟词
     *
     * @param studentId  当前学生信息
     * @param courseId   当前课程id
     * @param studyModel 学习模块
     * @param condition  查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<Vocabulary> selectUnknownWordByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("studyModel") String studyModel, @Param("condition") Integer condition);

    /**
     * 查询出删除的单元id
     *
     * @param nv
     * @return
     */
    List<Long> selectUnitIdsNotInIds(@Param("nv") UnitVocabulary nv);

    @Select("select word_chinese from vocabulary limit #{i}, #{i1}")
    List<String> getThreeChinese(@Param("i") int i, @Param("i1") int i1);

    @Select("select word from vocabulary limit #{i}, #{i1}")
    List<String> getThreeWord(@Param("i") int i, @Param("i1") int i1);

    List<Map<String, Object>> getWordIdByCourse(@Param("correctWordId") Long correctWordId, @Param("courseId") Long courseId, @Param("unidId") Long unidId);

    List<Map<String, Object>> getWordIdNewByCourse(@Param("correctWordId") Long correctWordId, @Param("unidId") Long unidId);

    List<Vocabulary> getWordPicAll(@Param("unitId") Long unitId, @Param("group") Integer group);

    List<Vocabulary> getWordIdByCourseAll(@Param("courseId") Long courseId);

    List<Vocabulary> getWordIdByAll(@Param("size") int size);

    List<Map<String, Object>> getWordIdByUnit(@Param("id") Long id, @Param("unitId") String unitId);

    List<Vocabulary> getMemoryWordPicAll(@Param("unit_id") long unitId, @Param("studentId") Long studentId, @Param("data") String data);

    /**
     * 查询当前单元指定模块的单词个数
     *
     * @param unitId     单元id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，单词图鉴）
     * @return
     */
    List<Vocabulary> selectByUnitIdAndStudyModel(@Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 查询当前单元指定模块的单词个数
     *
     * @param courseId   单元id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，单词图鉴）
     * @return
     */
    List<Vocabulary> selectByCourseIdAndStudyModel(@Param("courseId") Long courseId, @Param("studyModel") String studyModel);

    /**
     * 从当前课程下随机取一个当前单词对应的版本的单词释义
     *
     * @param version
     * @param wordId
     * @param phase
     * @return
     */
    String selectWordChineseByVersionAndWordId(@Param("version") String version, @Param("wordId") Long wordId, @Param("phase") String phase);

    @Select("select count(c.id) FROM unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{courseId} AND c.recordpicurl IS NOT NULL")
    Integer picByCourseId(@Param("courseId") Long course_id);

    Integer countByModel(@Param("unitId") Integer id, @Param("model") int i);

    @Select("select c.id, c.word, c.word_chinese as wordChinese from unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{courseId} AND c.delStatus = 1 GROUP BY vocabulary_id  order by rand() LIMIT 30")
    List<Vocabulary> getRandomCourseThirty(@Param("courseId") Long courseId);

    /**
     * 获取单词好声音题目
     *
     * @param unitId
     * @return
     */
    List<Vocabulary> selectWordVoice(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询单词本中单词播放机还未学习的单词
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<Vocabulary> selectUnlearnInBookPlayer(@Param("studentId") Long studentId, @Param("unitId") Long unitId);


    /**
     * 获取课程下目标单词外的单词信息
     *
     * @param courseId
     * @param wordMapList
     * @return
     */
    List<Vocabulary> selectByCourseIdNotInWord(@Param("courseId") Long courseId, @Param("wordMapList") List<Map<String, Object>> wordMapList);

    /**
     * 根据单词查询单词信息
     *
     * @param word
     * @return
     */
    Vocabulary selectByWord(@Param("word") String word);

    /**
     * 查询课程中指定数量的单词
     *
     * @param courseId
     * @param limitStart
     * @param limitEnd
     * @param wordIds
     * @return
     */
    List<Map<String, String>> selectWordByCourseId(@Param("courseId") long courseId, @Param("limitStart") int limitStart,
                                                   @Param("limitEnd") int limitEnd, @Param("wordIds") List<Long> wordIds);

    /**
     * 获取指定数量的学生已学含有图片的单词信息
     *
     * @param studentId
     * @param limit
     * @return
     */
    List<Map<String, Object>> selectPictureWordFromLearned(@Param("studentId") Long studentId, @Param("limit") int limit);

    /**
     * 获取指定进度的单词图鉴信息
     *
     * @param studentId
     * @param unitId
     * @param plan
     * @return
     */
    Map<String, Object> selectPictureWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("plan") Integer plan);

    /**
     * 获取指定单元范围的单词信息
     *
     * @param startUnitId
     * @param endUnitId
     * @return
     */
    List<Vocabulary> selectByStartUnitIdAndEndUnitId(@Param("startUnitId") Long startUnitId, @Param("endUnitId") Long endUnitId);

    /**
     * 查询课程下除去指定单词的单词信息
     *
     * @param courseId
     * @param vocabularies
     * @return
     */
    List<Vocabulary> selectByCourseIdWithoutWordIds(@Param("courseId") long courseId, @Param("vocabularies") List<Vocabulary> vocabularies);

    /**
     * 查询当前学段并且不在ignoreList中的单词
     *
     * @param studyParagraph 学段
     * @param ignoreList     需要忽略的单词数据
     * @return
     */
    List<Vocabulary> selectByPhaseNotInWord(@Param("studyParagraph") String studyParagraph, @Param("ignoreList") List<Map<String, Object>> ignoreList);

    /**
     * 获取指定个数含有图片的单词信息
     *
     * @param offset 从第多少条开始获取
     * @param limit  需要获取的个数
     * @return
     */
    List<Vocabulary> selectPictureRandom(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 眼脑训练取10个单词
     *
     * @param studentId 学生id
     * @param type      1，查询已学的单词  2查询全部单词
     */
    List<String> selByStudentIdLimitTen(@Param("studentId") Long studentId, @Param("type") int type, @Param("start") Integer start);

    /**
     * 获取单词数量
     *
     * @param studentId 学生id
     * @param type      1，查询已学的单词  2查询全部单词
     */
    Integer selCountByStudentIdLimitTen(@Param("studentId") Long studentId, @Param("type") int type);

    /**
     * 获取火眼精金数据
     *
     * @param type   1 查找小于10个字母的单词  2 查询固定长度的单词
     * @param length 最大单词长度
     * @param max    查询单词数量 ：1 15个单词 ， 2 1个单词
     * @param start  单词查询开始位置
     * @return
     */
    List<String> selRandWord(@Param("type") Integer type, @Param("leng") Integer length, @Param("max") Integer max, @Param("start") Integer start);

    /**
     * 获取火眼精金数据數量
     *
     * @param type
     * @param length 单词长度
     * @return
     */
    Integer selCountRandWord(@Param("type") Integer type, @Param("leng") Integer length);

    /**
     * 通过单元名查询当前单元下的所有单词信息
     *
     * @param jointName
     * @return
     */
    List<Vocabulary> selectByJointName(@Param("jointName") String jointName);

    /**
     * 统计当前单元含有图片的单词个数
     *
     * @param unitId
     * @return
     */
    int countPicture(@Param("unitId") Long unitId);

    /**
     * 获取单元下所有单词数据
     *
     * @param unitIds
     * @return
     */
    List<SubjectsVO> selectSubjectsVOByUnitIds(@Param("unitIds") List<Long> unitIds);

    List<SubjectsVO> selectSubjectsVO();


    Vocabulary selectOneWordNotInIdsNew(@Param("wordIds") List<Long> wordIds, @Param("unitId") Long unitId,
                                        @Param("group") Integer group);

    List<String> selectChineseByNotVocabularyIds(@Param("wordIds") List<Long> vocabularyIds);
}

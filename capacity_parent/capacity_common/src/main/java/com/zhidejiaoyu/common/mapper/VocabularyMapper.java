package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.UnitVocabulary;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.pojo.VocabularyExample;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VocabularyMapper extends BaseMapper<Vocabulary> {
    int countByExample(VocabularyExample example);

    int deleteByExample(VocabularyExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Vocabulary record);

    List<Vocabulary> selectByExample(VocabularyExample example);

    Vocabulary selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Vocabulary record, @Param("example") VocabularyExample example);

    int updateByExample(@Param("record") Vocabulary record, @Param("example") VocabularyExample example);

    int updateByPrimaryKeySelective(Vocabulary record);

    int updateByPrimaryKey(Vocabulary record);

    int wordAdd(Vocabulary vocabulary);

    @Insert("insert into unit_vocabulary(unit_id, vocabulary_id, classify, word_chinese) values(#{id},#{id2}, #{classify}, #{chinese})")
    void vocabularyToUnit(@Param("id") String id, @Param("id2") Long id2, @Param("classify") String classify, @Param("chinese") String chinese);

    /**
     * 查询单词是否存在
     */
    @Select("select id from vocabulary where word = #{word} and delStatus = 1")
    List<Integer> showVocabulary(@Param("word") String word);

    List<Vocabulary> page(Vocabulary vo);

    /**
     * 删除单词
     */
    //@Update("update vocabulary set delStatus = 2 where id = #{id}")
    @Delete("delete from vocabulary where id = #{id}")
    Integer delWord(@Param("id") String id);

    // @Delete("delete from vocabulary where id = #{id}")
    // Integer delWord(@Param("id") String id);

    // @Delete("delete from unit_vocabulary where vocabulary_id = #{id}")
    // Integer delWordTo(@Param("id") String id);

    /**
     * 根据拼接的名查询单元id
     */
    @Select("select id from unit where joint_name = #{pjm} and delStatus = 1")
    Integer selectJoint_name(@Param("pjm") String pjm);

    /**
     * 单元关联单词
     */
    @Insert("insert into unit_vocabulary(unit_id, vocabulary_id, classify) values(#{id}, #{id2}, #{classify})")
    Integer addUnit_vocabulary(@Param("id") Integer id, @Param("id2") Long id2, @Param("classify") Integer classify);

    @Select("select b.unit_id from unit a INNER JOIN unit_vocabulary b on a.id = b.unit_id where a.joint_name=#{pjm} and b.vocabulary_id=#{in} ")
    Integer selectUnit_vo(@Param("pjm") String pjm, @Param("in") Integer in);

    /**
     * 新增/更改初中例句
     */
    @Update("update vocabulary set centreExample = #{valueOf}, centreTranslate = #{valueOf2} where id = #{in} ")
    void updateCentreVocabulary(@Param("in") Integer in, @Param("valueOf") String valueOf,
                                @Param("valueOf2") String valueOf2);

    /**
     * 新增/更改高中例句
     */
    @Update("update vocabulary set tallExample = #{valueOf}, tallTranslate = #{valueOf2} where id = #{in} ")
    void updateTallVocabulary(@Param("in") Integer in, @Param("valueOf") String valueOf,
                              @Param("valueOf2") String valueOf2);

    Vocabulary mapSelectVocabulary(String id);

    /**
     * 修改词汇
     */
    Integer wordUpdate(Vocabulary vocabulary);

    @Insert("insert into unit_vocabulary(unit_id, vocabulary_id, classify, word_chinese) values(#{unitid}, #{id}, #{classify}, #{chinese})")
    void wordToUnit(@Param("id") Long id, @Param("unitid") String unitid, @Param("classify") String classify, @Param("chinese") String chinese);

    @Select("select vocabulary_id from unit_vocabulary where unit_id = #{unitid} and vocabulary_id = #{id} ")
    Integer wordToUnitRelevancy(@Param("id") Long id, @Param("unitid") String unitid);

    /**
     * 单词id, 单元id, 词汇分类
     *
     * @param chinese
     */
    @Update("update unit_vocabulary set classify = #{string}, word_chinese = #{chinese} where vocabulary_id = #{id} and unit_id = #{unitid}")
    void updateClassfiy(@Param("id") Long id, @Param("unitid") String unitid, @Param("string") String string, @Param("chinese") String chinese);

    /**
     * 删除取消关联的单词的单元
     */
    // @Delete("delete from unit_vocabulary where vocabulary_id = #{id} ${unit_id}")
    // void delUnit_vocabulary(@Param("id") Long id, @Param("unit_id")String
    // unit_id);

    void delUnit_vocabulary(UnitVocabulary nv);

    /**
     * 根据单词的 ids 获取单词集合
     *
     * @param wordIds
     * @return
     */
    List<Vocabulary> selectByWordIds(@Param("ids") List<Long> wordIds);

    @Select("select id from vocabulary where id = #{id} and word = #{word}")
    Integer showVocabularyId(@Param("id") Long id, @Param("word") String word);

    /**
     * 获取当前单元下的所有单词信息(不含删除的单词)
     *
     * @param unitId
     * @return
     */
    List<Vocabulary> selectByUnitId(@Param("unitId") Long unitId);

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
     * @param unit_id 单元id
     * @param id      学生id
     * @return 单词数据
     */
    Vocabulary showWord(@Param("unit_id") String unit_id, @Param("id") Long id);

    /**
     * 获取当前课程下的单词总量
     *
     * @param courseId
     * @param flag     1：单词图鉴 2：其他
     * @return
     */
    int countByCourseId(@Param("courseId") Long courseId, @Param("flag") Integer flag);

    @Select("select count(id) from vocabulary where word = #{word} and delStatus = 1 and id != #{id}")
    Integer selectVocabuularyId(@Param("id") Long id, @Param("word") String word);

    /**
     * 查询单词-单元中间表是否已经关联
     *
     * @param unitId 单元id
     * @param wordId 单词id
     * @return
     */
    @Select("select unit_id from unit_vocabulary where unit_id = #{unitId} and vocabulary_id = #{wordId}")
    Integer selectUnit_Word(@Param("unitId") Integer unitId, @Param("wordId") Integer wordId);

    @Update("update vocabulary set word_chinese = #{wordChinese} where id = #{id}")
    Integer excelUpdateWork(@Param("wordChinese") Object wordChinese, @Param("id") int id);

    List<Vocabulary> pageWord(@Param("word") String word);

    @Select("select word_chinese from vocabulary where word = #{word} and delStatus = 1")
    String showVocabularyWordChinese(@Param("word") String word);

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

    @Select("select COUNT(c.id) FROM unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND c.delStatus = 1 AND a.course_id = #{course_id}")
    Integer courseCountVocabulary(@Param("course_id") Long course_id);

    Integer countUnitWeek(@Param("studentId") Long studentId, @Param("course_id") Long course_id, @Param("model") int model);

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

    @Select("select IFNULL(syllable,word) from vocabulary where id = #{vocabulary_id}")
    String getSyllableByWordid(@Param("vocabulary_id") Long vocabulary_id);

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

    @Select("select id, recordpicurl from vocabulary where word = #{fileName}")
    Vocabulary matchingImagesToWord(@Param("fileName") String fileName);

    @Update("update vocabulary set recordpicurl = #{s} where id = #{wordId}")
    void updateRecordpicurl(@Param("s") String s, @Param("wordId") Long wordId);

    List<Vocabulary> pagePicNotNull(Vocabulary vo);

    List<Vocabulary> pageWordPicNotNull(@Param("word") String word);

    List<Map<String, Object>> getWordIdByCourse(@Param("correctWordId") Long correctWordId, @Param("courseId") Long courseId, @Param("unidId") Long unidId);

    Map<String, Object> getNotNeedReviewWord(@Param("unitId") Long unidId, @Param("studentId") Long studentId);

    List<Vocabulary> getWordPicAll(@Param("unitId") Long unitId);

    List<Vocabulary> getWordIdByCourseAll(@Param("courseId") Long courseId);

    List<Vocabulary> getWordIdByAll(@Param("size") int size);

    List<Map<String, Object>> getWordIdByUnit(@Param("id") Long id, @Param("unitId") String unitId);

    List<Vocabulary> getMemoryWordPicAll(@Param("unit_id") long unit_id, @Param("studentId") Long studentId, @Param("data") String data);

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
     * @param studentId
     * @param unitId
     * @return
     */
    List<Vocabulary> selectWordVoice(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 查询单词本中单词播放机还未学习的单词
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<Vocabulary> selectUnlearnInBookPlayer(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}
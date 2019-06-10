package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.bookVo.BookVo;
import com.zhidejiaoyu.common.Vo.simple.SimpleCapacityVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.UnitVocabulary;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface SimpleVocabularyMapper extends BaseMapper<Vocabulary> {

    int wordAdd(Vocabulary vocabulary);

    @Insert("insert into unit_vocabulary(unit_id, vocabulary_id, classify, word_chinese) values(#{id},#{id2}, #{classify}, #{chinese})")
    void vocabularyToUnit(@Param("id") String id, @Param("id2") Long id2, @Param("classify") String classify, @Param("chinese") String chinese);

    /**
     * 查询单词是否存在
     */
    @Select("select id from vocabulary where word = #{word} and delStatus = 1")
    List<Integer> showVocabulary(@Param("word") String word);

    List<Vocabulary> page(Vocabulary vo);

    @Delete("delete from vocabulary where id = #{id}")
    Integer delWord(@Param("id") String id);

    /**
     * 根据拼接的名查询单元id
     */
    @Select("select id from unit where joint_name = #{pjm}")
    Integer selectJoint_name(@Param("pjm") String pjm);


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

    void delUnit_vocabulary(UnitVocabulary nv);

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
     * 根据学生id查询当前课程下未学习的所有单词
     *
     * @param studentId 学生id
     * @param courseId  课程id
     * @return
     */
    List<BookVo> selectUnlearnedBookVoByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生id查询当前课程下未学习的所有单词
     *
     * @param studentId  学生id
     * @param courseIds   课程id
     * @return
     */
    List<Vocabulary> selectUnlearnedByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds);

    /**
     * 根据学生id查询当前课程下未学习的所有单词单词本
     *
     * @param studentId 学生id
     * @param courseIds 课程id
     * @return
     */
    List<BookVo> selectUnlearnedBookVoByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds);


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
     * @param flag 1：单词图鉴 2：其他
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
     * 获取当前课程的单词本信息信息（不含删除的单词）
     *
     * @param courseId   当前课程id
     * @return
     */
    List<BookVo> selectBookVoByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生id查询当前课程下所有生词/熟词
     *
     * @param studentId  当前学生信息
     * @param courseId   当前课程id
     * @param condition  查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<Vocabulary> selectUnknownWordByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                                 @Param("condition") Integer condition);

    /**
     * 根据学生id查询当前课程下单词本所有生词/熟词
     *
     * @param studentId 当前学生信息
     * @param courseId  当前课程id
     * @param condition 查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<BookVo> selectUnknownWordBookVoByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                                   @Param("condition") Integer condition);


    /**
     * 根据学生id查询当前课程下指定模块的所有生词/熟词
     *
     * @param studentId  当前学生信息
     * @param courseIds   当前课程id
     * @param condition  查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<Vocabulary> selectUnknownWordByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds,
                                                  @Param("condition") Integer condition);

    /**
     * 根据学生id查询当前课程下指定模块的所有生词/熟词单词本
     *
     * @param studentId 当前学生信息
     * @param courseIds 当前课程id
     * @param condition 查询类型		2：查询生词 3：查询熟词
     * @return
     */
    List<BookVo> selectUnknownWordBookVoByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds,
                                                    @Param("condition") Integer condition);

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

    Map<String,Object> getNotNeedReviewWord(@Param("unitId") Long unidId, @Param("studentId") Long studentId);

    List<Vocabulary> getWordPicAll(@Param("unitId") Long unitId);

    List<Vocabulary> getWordIdByCourseAll(@Param("courseId") Long courseId);

    List<Vocabulary> getWordIdByAll(@Param("size") int size);

    List<Map<String,Object>> getWordIdByUnit(@Param("id") Long id, @Param("unitId") String unitId);

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
     * 从当前课程下随机取一个当前单词对应的版本的单词释义
     *
     * @param version
     * @param wordId
     * @param phase
     * @return
     */
    String selectWordChineseByVersionAndWordId(@Param("version") String version, @Param("wordId") Long wordId, @Param("phase") String phase);

    /**
     * 获取下一单词
     *
     * @param unitId
     * @param studentId
     * @return
     */
    SimpleCapacityVo showWordSimple(@Param("unitId") Long unitId, @Param("studentId") Long studentId, @Param("type") int type);

    @Select("select c.id, c.word, c.word_chinese as wordChinese, c.example_english AS exampleEnglish,c.example_chinese AS exampleChinese from unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{courseId} AND c.delStatus = 1 GROUP BY vocabulary_id  order by rand() LIMIT 50")
    List<Vocabulary> getRandomCourseThirty(@Param("courseId") Long courseId);

    @Select("select word, word_chinese AS wordChinese from vocabulary where id = #{vocabularyId}")
    Vocabulary selectByWordInfo(@Param("vocabularyId") Long vocabularyId);

    @Select("SELECT d.id, d.word, d.word_chinese as wordChinese, d.example_english AS exampleEnglish,d.example_chinese AS exampleChinese FROM course a JOIN unit b ON a.id = b.course_id JOIN unit_vocabulary c ON b.id = c.unit_id JOIN vocabulary d ON c.vocabulary_id = d.id AND a.version like '${start}%' AND a.version like '%${end}' AND d.delStatus = 1 group by d.id order by rand() LIMIT 100")
    List<Vocabulary> getStudyParagraphTest(@Param("start") String start, @Param("end") String end);

    List<Vocabulary> getTestPaperGenerationAll(@Param("courseId") long courseId, @Param("typeTwo") int typeTwo, @Param("unitId") String[] unitId);

    @Select("select count(id) from unit_vocabulary a JOIN vocabulary b ON a.vocabulary_id = b.id AND a.unit_id  = #{unitId} AND b.delStatus = 1")
    Integer countByModel(@Param("unitId") Object unitId);

    /**
     * 查询课程下所有单词
     *
     * @param courseIds
     * @return
     */
    List<Vocabulary> selectByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 查询课程下所有单词本列表信息
     *
     * @param courseIds
     * @return
     */
    List<BookVo> selectBookVoByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 查询当前单元的单词本
     *
     * @param unitId
     * @return
     */
    List<BookVo> selectBookVoByUnitId(@Param("unitId") Long unitId);

    Vocabulary selectByWord(@Param("text") String text);

    /**
     * 查询各个课程下所有单词数量
     *
     * @param courseIds
     * @return
     */
    @MapKey("id")
    Map<Long, Map<Long, Long>> countWordMapByCourseIds(@Param("courseIds") List<Long> courseIds);

    List<Vocabulary> getWordByCourseGetNumber(@Param("courseId") Long courseId, @Param("start") Integer start, @Param("row") Integer row);

    List<Vocabulary> selectByCourseIdWithoutWordIds(@Param("courseId") Long courseId, @Param("vocabularies") List<Vocabulary> vocabularys);

    List<Vocabulary> getWord(@Param("start") int start, @Param("row") int end, @Param("wordIds") List<Long> wordIds);
}

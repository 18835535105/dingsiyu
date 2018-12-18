package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SentenceMapper extends BaseMapper<Sentence> {

    /**
     * 分页数据
     *
     * @param sentence
     * @return
     */
    List<Sentence> sentencePage(@Param("sentence")String sentence, @Param("course_id")String course_id);

    /**
     * 新增
     *
     * @param sentence
     * @return
     */
    Integer sentenceAdd(Sentence sentence);

    /**
     * 编辑
     *
     * @param sentence
     * @return
     */
    Integer sentenceEdit(Sentence sentence);

    /**
     * 根据id显示数据
     *
     * @param id
     */
    Sentence editshow(Integer id);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Delete("delete from sentence where id = #{id}")
    Integer del(@Param("id") Integer id);

    /**
     * 根据单词查询单词例句是否已经存在 (去重)
     *
     * @param word 单词
     * @return id
     */
    @Select("select id from sentence where word = #{word}")
    String selectToSentence(@Param("word") String word);

    /**
     * 根据单词查询单词例句是否已经存在 (去重)
     *
     * @param word 单词
     * @return 例句对象
     */
    @Select("select * from sentence where word = #{word}")
    Sentence selectToSentenceToWord(@Param("word") String string);

    // 3.1.1修改保存高中例句
    @Update("update sentence set tallExample = #{tallExample}, tallTranslate = #{tallTranslate}, tallExampleDisturb = #{tallExampleDisturb}, tallTranslateDisturb = #{tallTranslateDisturb} where id = #{id}")
    void updateTallExample(@Param("id") Long id, @Param("tallExample") String tallExample,
                           @Param("tallTranslate") String tallTranslate, @Param("tallExampleDisturb") String tallExampleDisturb,
                           @Param("tallTranslateDisturb") String tallTranslateDisturb);

    // 3.2.1修改保存初中例句
    @Update("update sentence set centreExample = #{centreExample}, centreTranslate = #{centreTranslate}, centreExampleDisturb = #{centreExampleDisturb}, centreTranslateDisturb = #{centreTranslateDisturb} where id = #{id}")
    void updateCentreExample(@Param("id") Long id, @Param("centreExample") String centreExample,
                             @Param("centreTranslate") String centreTranslate,
                             @Param("centreExampleDisturb") String centreExampleDisturb,
                             @Param("centreTranslateDisturb") String centreTranslateDisturb);

    /**
     * 获取当前课程下的所有例句
     *
     * @param courseId
     * @return
     */
    @Select("SELECT s.* FROM unit_sentence us,unit u, sentence s WHERE us.unit_id = u.id AND us.sentence_id = s.id AND u.course_id = #{courseId}")
    List<Sentence> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据例句id和学段查询当前例句的翻译
     *
     * @param id    例句id
     * @return
     */
    String selectChineseByIdAndPhase(@Param("id") Long id);

    /**
     * 根据单元id查询当前学生当前模块下的例句
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel 学习模块
     * @param condition  查询类型		2：查询生句，3：查询熟句
     * @return
     */
    List<Sentence> selectUnKnowSentenceByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel, @Param("condition") Integer condition);

    /**
     * 根据课程id查询当前学生当前模块下的例句
     *
     * @param studentId  学生id
     * @param courseId   课程id
     * @param studyModel 学习模块
     * @param condition  查询类型		2：查询生句，3：查询熟句
     * @return
     */
    List<Sentence> selectUnKnowSentenceByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("studyModel") String studyModel, @Param("condition") Integer condition);

    /**
     * 根据id查询当前学生当前模块下未学习的例句
     *
     * @param studentId  学生id
     * @param unitId     单元id
     * @param studyModel 学习模块
     * @return
     */
    List<Sentence> selectUnLearnedSentenceByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    /**
     * 根据id查询当前学生当前模块下未学习的例句
     *
     * @param studentId  学生id
     * @param courseId   课程id
     * @param studyModel 学习模块
     * @return
     */
    List<Sentence> selectUnLearnedSentenceByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("studyModel") String studyModel);

    /**
     * 根据主键获取例句信息
     *
     * @param exampleId
     * @return
     */
    Sentence selectByPrimaryKey(Long exampleId);

    /**
     * 根据单词英文查找例句集合
     *
     * @param words 单词英文集合
     * @return
     */
    List<Sentence> selectByWords(@Param("words") List<String> words);

    @Select("select id from sentence where centreExample = #{str} ")
	Integer selectCentreExample(@Param("str") String str);

    // 新增主建返回
	Integer insertSentence(Sentence se);

	@Insert("insert into unit_sentence(unit_id, sentence_id) values(#{unit_id}, #{sentence_id})")
	Integer SentenceToUnit(@Param("sentence_id") Long sentence_id,@Param("unit_id") Integer unit_id);

	@Select("select sentence_id from unit_sentence where unit_id = #{unit_id} and sentence_id = #{sentence_id}")
	Integer selectSentenceToUnit(@Param("sentence_id") Integer sentence_id,@Param("unit_id") Integer unit_id);

	@Select("select count(a.id) from sentence a, unit_sentence b where a.id = b.sentence_id and b.unit_id = #{unitId}")
	Long countByUnitId(@Param("unitId") Long unitId);
	
	/**
     * 查询单元-单词是否已经关
     *
     * @param id 例句id
     * @param unitId 单元id
     * @return
     */
    @Select("select sentence_id from unit_sentence where unit_id = #{unitId} and sentence_id = #{id}")
	Integer wordToUnitRelevancy(@Param("id")Long id, @Param("unitId")String unitId);

	/**
	 * 新增例句单元中间表
	 *
	 * @param id 例句id
	 * @param unitId 单元id
	 */
    @Insert("insert into unit_sentence(unit_id, sentence_id) values(#{unitId}, #{id})")
	void wordToUnit(@Param("id")Long id, @Param("unitId")String unitId);

	/**
	 * 删除取消关联的单元
	 *
	 * @param id 例句id
	 * @param unit_id 不需要删除的单元id
	 */
	void delUnit_vocabulary(@Param("id")Long id, @Param("unit_id")String unit_id);

	@Select("select COUNT(c.id) FROM unit a JOIN unit_sentence b ON a.id = b.unit_id JOIN sentence c ON b.sentence_id = c.id AND a.course_id = #{course_id}")
	Integer courseCountSentence(@Param("course_id")Long course_id);

    /**
     * 查看当前课程下例句总数
     * @param courseId  课程id
     * @return
     */
    @Select("select count(s.id) from sentence s, unit_sentence us, unit u where s.id = us.sentence_id and u.id = us.unit_id and u.course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询当前单元未学习的首个例句
     * @param ids
     * @param unitId
     * @return
     */
    Sentence selectOneSentenceNotInIds(@Param("ids") List<Long> ids, @Param("unitId") Long unitId);

    /**
     * 根据id查找例句
     *
     * @param ids 例句id集合
     * @return 例句集合
     */
    List<Sentence> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 获取当前单元下所有例句信息
     *
     * @param unitId
     * @return
     */
    List<Sentence> selectByUnitId(@Param("unitId") Long unitId);

    /**
     * 获取当前单元下未测试的一个句型
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<Sentence> selectOneByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 获取句型好声音题目
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<Sentence> selectSentenceVoice(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 获取当前单元还未学习过的句型信息
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<Sentence> selectUnlearnInBookPlayer(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}
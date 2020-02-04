package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
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
     * 新增主建返回
     *
     * @param se
     * @return
     */
	Integer insertSentence(Sentence se);

	@Select("select count(a.id) from sentence a, unit_sentence_new b where a.id = b.sentence_id and b.unit_id = #{unitId}")
	Long countByUnitId(@Param("unitId") Long unitId);

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
     * @param unitIds  单元id集合
     * @return
     */

    int countByCourseId(@Param("unitIds") List<Long> unitIds);

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

    List<Sentence> selectRoundSentence(Long courseId);

    List<Map<String, Object>> selectSentenceLaterLearnTimeByStudentId(Long id);

    Sentence selectOneWordNotInIdsNew(@Param("wordIds") List<Long> wordIds,
                                        @Param("unitId") Long unitId,@Param("group") Integer group);

    List<Sentence> selectByUnitIdAndGroup(@Param("unitId") Long unitId,@Param("group") Integer group);

    List<String> selectNotIds(@Param("sentenceIds") List<Long> sentenIds);
}

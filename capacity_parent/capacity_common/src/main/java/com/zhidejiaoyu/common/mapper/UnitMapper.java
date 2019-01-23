package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.common.pojo.UnitOneExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UnitMapper extends BaseMapper<Unit> {
    int countByExample(UnitOneExample example);

    int deleteByExample(UnitOneExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Unit record);

    List<Unit> selectByExample(UnitOneExample example);

    Unit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Unit record, @Param("example") UnitOneExample example);

    int updateByExample(@Param("record") Unit record, @Param("example") UnitOneExample example);

    int updateByPrimaryKeySelective(Unit record);

    int updateByPrimaryKey(Unit record);

    /**
     * 批量保存单元信息
     *
     * @param units
     */
    void insertList(List<Unit> units);

    /**
     * 查询当前课程下最大的单元index
     *
     * @param courseId
     * @return
     */
    @Select("select max(unit_index) from unit where course_id=#{courseId}")
    Integer selectMaxUnitIndexByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取当前单元的unit_index
     *
     * @param unitId
     * @return
     */
    @Select("select unit_index from unit where id = #{unitId}")
    Integer selectCurrentUnitIndexByUnitId(@Param("unitId") Long unitId);

    /**
     * 获取下个单元的id
     *
     * @param courseId
     * @param nextUnitIndex
     * @return
     */
    @Select("select DISTINCT unit.id from unit unit join unit_vocabulary uv on unit.id=uv.unit_id  where unit.course_id=#{courseId} and unit.unit_index=#{nextUnitIndex}")
    Long selectNextUnitIndexByCourseId(@Param("courseId") Long courseId, @Param("nextUnitIndex") Integer nextUnitIndex);

    /**
     * 删除当前课程下的所有单元
     *
     * @param courseId
     */
    @Delete("delete from unit where courseId = #{courseId}")
    void deleteUnitByCourseId(int courseId);

    /**
     * 根据单元id获取所属课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from unit where id = #{unitId}")
    Long selectCourseIdByUnitId(Long unitId);

    /**
     * 获取单元下所有单词总数
     * @param unit_id 单元id
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1")
    Integer countWordByUnitid(@Param("unit_id") String unit_id);

    /**
     * 获取单元下所有例句总量
     * @param unitId 单元id
     * @return
     */
    @Select("select count(b.id) from unit_sentence a JOIN sentence b ON a.sentence_id = b.id AND a.unit_id = #{unitId}")
    Integer countSentenceByUnitid(@Param("unitId") String unitId);

    //select id from (select * from unit a LIMIT 0,#{unitNumber}) a WHERE course_id = #{courseId} ORDER BY id DESC LIMIT 0,1
	Map<String, Object> getUnitIdByCourseIdAndUnitNumber(@Param("courseId")Integer courseId, @Param("unitNumber")Integer unitNumber);

	List<Map<String, Object>> allUnit(Integer id);

	Map<String, String> getCourseNameAndUnitName(@Param("courseId")Integer courseId, @Param("unitId")Integer unitId);

    /**
     * 根据课程id和单词的id查询当前单词所属的单元id
     * @param courseId
     * @param wordIds   单词id集合
     * @param StudyModel 0:词义强化；1：慧记忆；2：慧听写；3：慧默写
     * @return  map key:单词id value map key 单词id，value 单元id
     */
    @MapKey("id")
    Map<Long,Map<Long,Long>> selectIdMapByCourseIdAndWordIds(@Param("courseId") Long courseId, @Param("wordIds") List<Long> wordIds, @Param("student_id") Long student_id, @Param("model") int StudyModel);

    @Select("select a.unit_id from unit_vocabulary a JOIN vocabulary b ON a.vocabulary_id = b.id AND a.unit_id = #{unitId} AND b.delStatus=1 limit 0,1")
	Integer selectByWord(@Param("unitId")Integer unitId);

    @Select("select unit_id from unit_sentence where unit_id = #{unitId} limit 0,1")
	Integer selectBySentence(Integer unitId);

    @Delete("delete from unit_vocabulary where unit_id = #{unitId}")
	void deleteByVocabulary(@Param("unitId")Integer unitId);

	List<Map<String, Object>> selectByUnitIdAndUnitName(@Param("courseIdw")Integer courseIdw);

	/**
	 * 获取:课程下一共有多少单词
	 *
	 * @param course_id
	 * @return
	 */
	@Select("select COUNT(a.id) from unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{course_id} AND c.delStatus = 1")
	Integer countWordByCourse(String course_id);

	@Select("select b.unit_id from unit a JOIN unit_vocabulary b ON a.id = b.unit_id AND a.course_id = #{course_id} AND b.vocabulary_id = #{vocabulary_id} LIMIT 0,1")
	String getUnitIdByCourseIdAndWordId(@Param("course_id")String course_id, @Param("vocabulary_id")Long vocabulary_id);

	@Select("select id from unit where course_id = #{courseId} ORDER BY id asc LIMIT 0,1")
	Integer getMinUnit(@Param("courseId")Integer courseId);

	@Select("select unit_name from unit where id = #{unitId}")
    String getUnitNameByUnitId(@Param("unitId") Long unitId);

    /**
     * 查找指定课程下的所有单元
     *
     * @param courseId
     * @return
     */
    List<Unit> selectUnitsByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据主键集合查询单元信息
     * @param ids   单元id集合
     * @return
     */
    List<Unit> selectByPrimaryKeys(@Param("ids") List<Long> ids);

    /**
     * 查询学生当前学段下所有单元
     *
     * @param student
     * @param phase 学段
     * @return
     */
    List<Long> selectIdByPhase(@Param("student") Student student, @Param("phase") String phase);

    @Select("select count(b.id) from unit_vocabulary a join vocabulary b on a.vocabulary_id = b.id and a.unit_id = #{s} and b.recordpicurl is not null and b.delStatus = 1")
    Integer countWordByUnitidByPic(Long s);


    /**
     * 根据单元id查询课程单元拼接名
     * @param unitId 单元id
     * @return
     */
    @Select("select joint_name from unit where id = #{unitId}")
    String getCourseNameByunitId(@Param("unitId") Integer unitId);


    /**
     * 根据单元id查询课程id
     * @param unitId 单元id
     * @return
     */
    Long getCourseIdByunitId(@Param("unitId") Integer unitId);

    /**
     * 单元下单词图鉴的总数
     * @param unitId
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1 and a.recordpicurl is not null")
    Integer countWordPicByUnitid(String unitId);

    /**
     * 查询课程下最大单元id
     * @param courseId
     * @return
     */
    @Select("select id from unit where course_id = #{courseId} AND unit_index = (select MAX(unit_index) from unit where course_id = #{courseId} ) and delStatus = 1")
    int maxUnitIndexByCourseId(@Param("courseId") long courseId);

    @Select("select COUNT(b.sentence_id) from unit a JOIN unit_sentence b ON a.id = b.unit_id AND a.course_id = #{courseId}")
    Long countSentenceByCourse(@Param("courseId") String courseId);

    Map getLimitOneByCourseId(@Param("courseId") long courseId);

    Map nextUnitId(@Param("courseId") long courseId, @Param("unitId") long unitId);

    /**
     * 获取课程最后一个单元id
     * @param courseId
     * @return
     */
    @Select("select id from unit WHERE course_id = #{courseId} AND delStatus = 1 ORDER BY unit_index desc LIMIT 1")
    long getMaxUnitIdByCourseId(@Param("courseId") long courseId);

    Map getMinUnitData(@Param("courseId") long courseId);

    /**
     * 获取当前学生当前课程下的所有单元信息
     *
     * @param courseId
     * @param studentId
     * @return
     */
    List<Map<String, Object>> selectUnitIdAndUnitNameByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * 当前课程下单元总个数
     *
     * @param courseId
     * @return
     */
    @Select("select count(id) from unit where course_id = #{courseId} ")
    int countByCourseId(@Param("courseId") Long courseId);
}
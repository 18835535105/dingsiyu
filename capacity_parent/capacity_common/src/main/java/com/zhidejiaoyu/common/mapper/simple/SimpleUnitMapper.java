package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.common.pojo.UnitOneExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SimpleUnitMapper extends BaseMapper<Unit> {
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
     * 查询当前课程下最大的单元index
     *
     * @param courseId
     * @return
     */
    @Select("select max(unit_index) from unit_new where course_id=#{courseId}")
    Integer selectMaxUnitIndexByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取当前单元的unit_index
     *
     * @param unitId
     * @return
     */
    @Select("select unit_index from unit_new where id = #{unitId}")
    Integer selectCurrentUnitIndexByUnitId(@Param("unitId") Long unitId);

    /**
     * 获取下个单元的id
     *
     * @param courseId
     * @param nextUnitIndex
     * @return
     */
    @Select("select id from unit_new where course_id=#{courseId} and unit_index=#{nextUnitIndex}")
    Long selectNextUnitIndexByCourseId(@Param("courseId") Long courseId, @Param("nextUnitIndex") Integer nextUnitIndex);


    /**
     * 根据单元id获取所属课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from unit_new where id = #{unitId}")
    Long selectCourseIdByUnitId(Long unitId);

    /**
     * 获取单元下所有单词总数
     * @param unitId 单元id
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary_new b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1")
    Integer countWordByUnitId(@Param("unit_id") String unitId);

    /**
     * 获取单元下所有例句总量
     * @param unitId 单元id
     * @return
     */
    @Select("select count(b.id) from unit_sentence_new a JOIN sentence b ON a.sentence_id = b.id AND a.unit_id = #{unitId}")
    Integer countSentenceByUnitId(@Param("unitId") String unitId);

	List<Map<String, Object>> allUnit(Integer id);

	Map<String, String> getCourseNameAndUnitName(@Param("courseId") Integer courseId, @Param("unitId") Integer unitId);

    /**
     * 根据课程id和单词的id查询当前单词所属的单元id
     * @param courseId
     * @param wordIds   单词id集合
     * @param StudyModel 0:词义强化；1：慧记忆；2：慧听写；3：慧默写
     * @return  map key:单词id value map key 单词id，value 单元id
     */
    @MapKey("id")
    Map<Long,Map<Long,Long>> selectIdMapByCourseIdAndWordIds(@Param("courseId") Long courseId, @Param("wordIds") List<Long> wordIds, @Param("student_id") Long student_id, @Param("model") int StudyModel);

	/**
	 * 获取:课程下一共有多少单词
	 *
	 * @param course_id
	 * @return
	 */
	@Select("select COUNT(a.id) from unit_new a JOIN unit_vocabulary_new b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{course_id} AND c.delStatus = 1")
	Integer countWordByCourse(Long course_id);

	@Select("select id from unit_new where course_id = #{courseId} ORDER BY id asc LIMIT 0,1")
	Integer getMinUnit(@Param("courseId") Integer courseId);


    @Select("select count(b.id) from unit_vocabulary_new a join vocabulary b on a.vocabulary_id = b.id and a.unit_id = #{s} and b.recordpicurl is not null and b.delStatus = 1")
    Integer countWordByUnitidByPic(Long s);


    /**
     * 根据单元id查询课程单元拼接名
     * @param unitId 单元id
     * @return
     */
    @Select("select joint_name from unit_new where id = #{unitId}")
    String getCourseNameByUnitId(@Param("unitId") Integer unitId);

    /**
     * 单元下单词图鉴的总数
     * @param unitId
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary_new b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1 and a.recordpicurl is not null")
    Integer countWordPicByUnitId(String unitId);

    /**
     * 获取当前课程下最后一个单元 id
     *
     * @param courseId
     * @return
     */
    @Select("select max(id) from unit_new where course_id = #{courseId} limit 1")
    Long selectMaxUnitIdInCourse(@Param("courseId") Long courseId);

    /**
     * 获取课程下第一个单元
     *
     * @param courseId
     * @return
     */
    Unit selectFirstUnitByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取各个课程下所有单元 id 和单元名
     *
     * @param courseIds
     * @return
     */
    List<Map<String, Object>> selectIdAndUnitNameByCourseIds(@Param("courseIds") List<Long> courseIds);
}

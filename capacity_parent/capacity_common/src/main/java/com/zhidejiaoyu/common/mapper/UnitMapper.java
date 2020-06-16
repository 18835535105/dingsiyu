package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Unit;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface UnitMapper extends BaseMapper<Unit> {

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
     *
     * @param unit_id 单元id
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary_new b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1")
    Integer countWordByUnitId(@Param("unit_id") String unit_id);

    /**
     * 获取单元下所有例句总量
     *
     * @param unitId 单元id
     * @return
     */
    @Select("select count(b.id) from unit_sentence_new a JOIN sentence b ON a.sentence_id = b.id AND a.unit_id = #{unitId}")
    Integer countSentenceByUnitId(@Param("unitId") String unitId);

    List<Map<String, Object>> selectIdAndUnitNameByCourseId(Integer courseId);

    Map<String, String> getCourseNameAndUnitName(@Param("courseId") Integer courseId, @Param("unitId") Integer unitId);

    /**
     * 根据课程id和单词的id查询当前单词所属的单元id
     *
     * @param courseId
     * @param wordIds    单词id集合
     * @param StudyModel 0:词义强化；1：慧记忆；2：慧听写；3：慧默写
     * @return map key:单词id value map key 单词id，value 单元id
     */
    @MapKey("id")
    Map<Long, Map<Long, Long>> selectIdMapByCourseIdAndWordIds(@Param("courseId") Long courseId, @Param("wordIds") List<Long> wordIds, @Param("student_id") Long student_id, @Param("model") int StudyModel);

    /**
     * 获取:课程下一共有多少单词
     *
     * @param course_id
     * @return
     */
    @Select("select COUNT(a.id) from unit_new a JOIN unit_vocabulary_new b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND a.course_id = #{course_id} AND c.delStatus = 1")
    Integer countWordByCourse(String course_id);

    @Select("select id from unit_new where course_id = #{courseId} ORDER BY id asc LIMIT 0,1")
    Integer selectMinUnitIdByCourseId(@Param("courseId") Integer courseId);

    @Select("select unit_name from unit_new where id = #{unitId}")
    String selectUnitNameByUnitId(@Param("unitId") Long unitId);

    /**
     * 查找指定课程下的所有单元
     *
     * @param courseId
     * @return
     */
    List<Unit> selectUnitsByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据主键集合查询单元信息
     *
     * @param ids 单元id集合
     * @return
     */
    List<Unit> selectByPrimaryKeys(@Param("ids") List<Long> ids);

    /**
     * 查询学生当前学段下所有单元
     *
     * @param student
     * @param phase   学段
     * @return
     */
    List<Long> selectIdByPhase(@Param("student") Student student, @Param("phase") String phase);

    @Select("select count(b.id) from unit_vocabulary a join vocabulary b on a.vocabulary_id = b.id and a.unit_id = #{s} and b.recordpicurl is not null and b.delStatus = 1")
    Integer countWordByUnitidByPic(Long s);


    /**
     * 根据单元id查询课程单元拼接名
     *
     * @param unitId 单元id
     * @return
     */
    @Select("select joint_name from unit where id = #{unitId}")
    String getCourseNameByunitId(@Param("unitId") Integer unitId);

    /**
     * 单元下单词图鉴的总数
     *
     * @param unitId
     * @return
     */
    @Select("select COUNT(a.id) from vocabulary a INNER JOIN unit_vocabulary b on a.id = b.vocabulary_id and b.unit_id = #{unit_id} AND a.delStatus = 1 and a.recordpicurl is not null")
    Integer countWordPicByUnitid(String unitId);

    /**
     * 获取当前学生当前课程下的所有单元信息
     *
     * @param courseId
     * @param studentId
     * @return
     */
    List<Map<String, Object>> selectUnitIdAndUnitNameByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

}

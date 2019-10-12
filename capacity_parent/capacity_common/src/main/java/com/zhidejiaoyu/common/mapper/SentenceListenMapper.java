package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SentenceListen;
import com.zhidejiaoyu.common.pojo.SentenceListenExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceListenMapper  {
    int countByExample(SentenceListenExample example);

    int deleteByExample(SentenceListenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SentenceListen record);

    int insertSelective(SentenceListen record);

    List<SentenceListen> selectByExample(SentenceListenExample example);

    SentenceListen selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SentenceListen record,
                                 @Param("example") SentenceListenExample example);

    int updateByExample(@Param("record") SentenceListen record, @Param("example") SentenceListenExample example);

    int updateByPrimaryKeySelective(SentenceListen record);

    int updateByPrimaryKey(SentenceListen record);

    /**
     * 根据学生id删除
     *
     * @param studentId
     * @return
     */
    @Delete("delete from sentence_listen where student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 根据学生id和课程id获取生句信息
     *
     * @param courseId
     * @param studentId
     * @return
     */
    List<SentenceListen> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                      @Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id获取生句信息
     *
     * @param unitId
     * @param studentId
     * @return
     */
    List<SentenceListen> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
                                                    @Param("studentId") Long studentId);

    /**
     * 根据课程id和学生id获取需要需要达到黄金记忆点的例句数
     *
     * @param studentId
     * @param courseId
     * @return
     */
    int countNeedReviewByStudentIdAndCourseId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * 通过学生id，单元id和单词id获取当前例句的记忆追踪信息
     *
     * @param studentId
     * @param unitId
     * @param wordId
     * @return
     */
    SentenceListen selectByStuIdAndUnitIdAndWordId(@Param("stuId") Long studentId, @Param("unitId") Long unitId,
                                                   @Param("wordId") Long wordId);

    /**
     * 通过学生id，单元id和单词id获取当前例句的记忆追踪信息
     *
     * @param studentId
     * @param courseId
     * @param wordId
     * @return
     */
    SentenceListen selectByStuIdAndCourseIdAndWordId(@Param("stuId") Long studentId, @Param("courseId") Long courseId,
                                                     @Param("wordId") Long wordId);

    @Select("select count(id) from sentence_listen where student_id = #{student_id} and push < #{dateTime}")
    Integer countByPushByCourseid(@Param("student_id") Long student_id, @Param("dateTime") String dateTime);

    /**
     * 获取当前课程下的所有例句个数
     *
     * @param courseId
     * @return
     */
    @Select("select COUNT(DISTINCT us.sentence_id) from unit_sentence us,unit u where us.unit_id = u.id and u.course_id = #{courseId}")
    Integer countByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据单元id和学生id获取需要需要达到黄金记忆点的例句数
     *
     * @param unitId
     * @param stuId
     * @return
     */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("unitId") Long unitId, @Param("studentId") Long stuId);

    @Delete("delete from sentence_listen where student_id=#{studentId} and unit_id=#{unitId}")
    Integer deleteByUnitIdAndStudentId(@Param("studentId") Long id, @Param("unitId") Integer unitId);
}

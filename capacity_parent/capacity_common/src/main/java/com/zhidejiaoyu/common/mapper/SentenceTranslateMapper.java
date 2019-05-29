package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SentenceTranslate;
import com.zhidejiaoyu.common.pojo.SentenceTranslateExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SentenceTranslateMapper {
    int countByExample(SentenceTranslateExample example);

    int deleteByExample(SentenceTranslateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SentenceTranslate record);

    int insertSelective(SentenceTranslate record);

    List<SentenceTranslate> selectByExample(SentenceTranslateExample example);

    SentenceTranslate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SentenceTranslate record,
                                 @Param("example") SentenceTranslateExample example);

    int updateByExample(@Param("record") SentenceTranslate record, @Param("example") SentenceTranslateExample example);

    int updateByPrimaryKeySelective(SentenceTranslate record);

    int updateByPrimaryKey(SentenceTranslate record);

    /**
     * 根据学生id删除
     *
     * @param studentId
     * @return
     */
    @Delete("delete from sentence_translate where student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 根据学生id和课程id获取生句信息
     *
     * @param courseId
     * @param studentId
     * @return
     */
    List<SentenceTranslate> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                         @Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id获取生句信息
     *
     * @param unitId
     * @param studentId
     * @return
     */
    List<SentenceTranslate> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
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
    SentenceTranslate selectByStuIdAndUnitIdAndWordId(@Param("stuId") Long studentId, @Param("unitId") Long unitId,
                                                      @Param("wordId") Long wordId);

    /**
     * 通过学生id，课程id和单词id获取当前例句的记忆追踪信息
     *
     * @param studentId
     * @param courseId
     * @param wordId
     * @return
     */
    SentenceTranslate selectByStuIdAndCourseIdAndWordId(@Param("stuId") Long studentId, @Param("courseId") Long courseId,
                                                        @Param("wordId") Long wordId);

    @Select("select count(id) from sentence_translate where student_id = #{student_id} and push < #{dateTime}")
    Integer countByPushByCourseid(@Param("student_id") Long student_id, @Param("dateTime") String dateTime);

    /**
     * 根据单元id和学生id获取需要需要达到黄金记忆点的例句数
     *
     * @param unitId
     * @param stuId
     * @return
     */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("unitId") Long unitId, @Param("studentId") Long stuId);

    @Delete("delete from sentence_translate where student_id=#{studentId} and unit_id=#{unitId}")
    Integer deleteByUnitIdAndStudentId(@Param("studentId") Long id, @Param("unitId") Integer unitId);

}
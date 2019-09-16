package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityListen;
import com.zhidejiaoyu.common.pojo.CapacityListenExample;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CapacityListenMapper extends BaseMapper<CapacityListen> {
    int countByExample(CapacityListenExample example);

    int deleteByExample(CapacityListenExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(CapacityListen record);

    List<CapacityListen> selectByExample(CapacityListenExample example);

    CapacityListen selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CapacityListen record,
                                 @Param("example") CapacityListenExample example);

    int updateByExample(@Param("record") CapacityListen record, @Param("example") CapacityListenExample example);

    int updateByPrimaryKeySelective(CapacityListen record);

    int updateByPrimaryKey(CapacityListen record);

    @Delete("delete from capacity_listen where student_id = #{studentId}")
    int deleteByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据单元id和单词id查找对应的慧听力记忆追踪信息
     *
     * @param studentId
     * @param unitId
     * @param correctWordId
     * @return
     */
    List<CapacityListen> selectByUnitIdAndId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                       @Param("correctWordId") Long correctWordId);

 /**
     * 根据课程id和单词id查找对应的慧听力记忆追踪信息
     *
     * @param studentId
     * @param courseId
     * @param correctWordId
     * @return
     */
    CapacityListen selectByCourseIdAndId(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                                       @Param("correctWordId") Long correctWordId);

    /**
     * 根据学生id和课程id获取生词信息
     *
     * @param courseId
     * @param studentId
     * @return
     */
    List<CapacityListen> selectByCourseIdAndStudentId(@Param("courseId") Long courseId,
                                                      @Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id获取生词信息
     *
     * @param unitId
     * @param studentId
     * @return
     */
    List<CapacityListen> selectByUnitIdAndStudentId(@Param("unitId") Long unitId,
                                                      @Param("studentId") Long studentId);

    /**
     * 根据课程id和学生id获取需要需要达到黄金记忆点的单词数
     *
     * @param studentId
     * @param courseId
     * @return
     */
    int countNeedReviewByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    Vocabulary selectCapacityListen(@Param("unit_id") String unit_id, @Param("id") Long id);

    @Select("select count(id) from capacity_listen where student_id = #{student_id} and push < #{dateTime}")
    Integer countByPushByCourseid(@Param("student_id") Long student_id, @Param("dateTime") String dateTime);

    /**
     * 根据单元id和学生id获取需要需要达到黄金记忆点的单词数
     *
     * @param stuId
     * @param unitId
     * @return
     */
    Integer countNeedReviewByStudentIdAndUnitId(@Param("studentId") Long stuId, @Param("unitId") Long unitId);

    /**
     * 根据指定条件查询
     *
     * @param capacityListen    记忆追踪信息
     * @return  记忆追踪信息
     */
    List<CapacityListen> selectByCapacityListen(CapacityListen capacityListen);

    /**
     * 删除学生当前单元的记忆追踪信息
     *
     * @param studentId
     * @param startUnit
     * @param endUnit
     */
    @Delete("delete from capacity_listen where student_id = #{studentId} and unit_id >= #{startUnit} and unit_id <= #{endUnit}")
    void deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit);

    @Select("select fault_time from capacity_listen where student_id = #{studentId} AND vocabulary_id = #{vocabularyId} limit 1")
    Integer getFaultTime(Long studentId, Long vocabularyId);

    @Update("update capacity_listen set push = date_add(push, interval ${pushRise} hour) where student_id = #{studentId} AND vocabulary_id = #{vocabularyId}")
    void updatePush(Long studentId, Long vocabularyId, int pushRise);

    /**
     * 获取一个生词
     *
     * @param student
     * @param unitId
     * @param ignoreWordId 忽略的单词 id
     * @return
     */
    Long selectUnknownWordByUnitId(@Param("student") Student student, @Param("unitId") String unitId, @Param("ignoreWordId") Long[] ignoreWordId);

    @Delete("delete from capacity_listen where student_id = #{studentId} and unit_id = #{unitId} and vocabulary_id=#{vocabularyId}")
    void deleteByStudentIdAndUnitIdAndVocabulary(@Param("studentId") Long studentId,@Param("unitId") Long unitId,@Param("vocabularyId") Long vocabularyId);
}

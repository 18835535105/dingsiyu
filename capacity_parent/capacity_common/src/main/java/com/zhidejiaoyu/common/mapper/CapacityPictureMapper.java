package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CapacityPicture;
import com.zhidejiaoyu.common.pojo.CapacityPictureExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CapacityPictureMapper extends BaseMapper<CapacityPicture> {
    int countByExample(CapacityPictureExample example);

    int deleteByExample(CapacityPictureExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(CapacityPicture record);

    List<CapacityPicture> selectByExample(CapacityPictureExample example);

    CapacityPicture selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CapacityPicture record, @Param("example") CapacityPictureExample example);

    int updateByExample(@Param("record") CapacityPicture record, @Param("example") CapacityPictureExample example);

    int updateByPrimaryKeySelective(CapacityPicture record);

    int updateByPrimaryKey(CapacityPicture record);

    Map<String,Object> selectNeedReviewWord(@Param("unidId") Long unidId, @Param("studentId") Long studentId, @Param("s") String s);

    /**
     * 根据单元id和单词id数组查找对应的单词图鉴记忆追踪信息
     *
     * @param studentId
     * @param unitId
     * @param correctWordId
     * @return
     */
    List<CapacityPicture> selectByUnitIdAndId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                       @Param("correctWordId") Long correctWordId);

    Map<String,Object> selectNeedReviewWordCourse(@Param("course_id") String course_id, @Param("studentId")Long studentId, @Param("s")String s);

    /**
     * 查询学生当前单元下记忆强度小于1的 单词图鉴 慧追踪数据
     *
     * @param unitId    单元id
     * @param studentId 学生id
     * @return
     */
    List<CapacityPicture> selectByUnitIdAndStudentId(@Param("unitId") Long unitId, @Param("studentId") Long studentId);

    /**
     * 查询学生当前课程下记忆强度小于1的 单词图鉴 慧追踪数据
     *
     * @param courseId  单元id
     * @param studentId 学生id
     * @return
     */
    List<CapacityPicture> selectByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * 查询单词图鉴慧追踪中需要复习的单词（达到黄金记忆点）
     *
     * @param studentId 学生id
     * @param unitId    单元id
     * @return
     */
    int countNeedReviewByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 查询单词图鉴慧追踪中需要复习的单词（达到黄金记忆点）
     *
     * @param studentId 学生id
     * @param courseId  课程id
     * @return
     */
    int countNeedReviewByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    void deleteAllMemory(@Param("studentId") long studentId, @Param("courseId") long courseId, @Param("model") int model);

    /**
     * 删除学生当前单元的记忆追踪信息
     *
     * @param studentId
     * @param startUnit
     * @param endUnit
     */
    @Delete("delete from capacity_picture where student_id = #{studentId} and unit_id >= #{startUnit} and unit_id <= #{endUnit}")
    void deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit);
    @Delete("delete from capacity_picture where student_id = #{studentId} and unit_id = #{unitId} and vocabulary_id=#{vocabularyId}")
    void deleteByStudentIdAndUnitIdAndVocabulary(@Param("studentId") Long studentId,@Param("unitId") Long unitId,@Param("vocabularyId") Long vocabularyId);
}
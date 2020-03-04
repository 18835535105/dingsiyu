package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnHistory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface LearnHistoryMapper extends BaseMapper<LearnHistory> {

    List<Map<String, Object>> selectStudyFiveStudent(@Param("studentIds") List<Long> studentIds);

    List<Map<String, Object>> selectStudyUnitByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id修改状态
     *
     * @param studentId
     * @param unitId
     * @param state
     */
    @Update("update learn_history set state = #{state} where student_id = #{studentId} and unit_id = #{unitId}")
    void updateStateByStudentIdAndUnitId(Long studentId, Long unitId, int state);

    /**
     * 统计当前课程已经学完的单元个数
     *
     * @param studentId
     * @param courseId
     * @return
     */
    @Select("select count(distinct unit_id) from learn_history where student_id = #{studentId} and course_id = #{courseId}")
    int countUnitIdByCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 统计各个课程下已学习单元个数
     *
     * @param studentId
     * @param courseIds
     * @param type      1:单词；2：句型；3：课文；4：语法
     * @return
     */
    @MapKey("courseId")
    Map<Long, Map<Long, Object>> countUnitByStudentIdAndCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds, @Param("type") int type);

    List<Long> selectWordListBystudentId(@Param("studentId") long studentId);
}

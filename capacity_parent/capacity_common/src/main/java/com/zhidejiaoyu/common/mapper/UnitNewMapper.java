package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.UnitNew;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface UnitNewMapper extends BaseMapper<UnitNew> {

    /**
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    List<Long> selectByGradeListAndVersionAndGrade(String version, List<String> gradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    List<Long> selectLessOrEqualsCurrentIdByCourseIdAndUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId);

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    List<Long> selectIdsByCourseNames(@Param("courseNames") List<String> courseNames);

    @MapKey("unitId")
    Map<Long, Map<String, Object>> selectCountByUnitIds(@Param("unitIds") List<Long> unitIds);

    int selectByUnitIdAndCourseId(@Param("unitId") Long unitId, @Param("courseId") Long courseId);

    @Select("select count(b.id) from unit_vocabulary_new a join vocabulary b on a.vocabulary_id = b.id and a.unit_id = #{s} and b.recordpicurl is not null and b.delStatus = 1")
    Integer countWordByUnitidByPic(Long s);

    /**
     * 统计当前课程下有多少个单元
     *
     * @param courseId
     * @return
     */
    @Select("select count(id) from unit_new where course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询当前课程下的所有单元信息
     *
     * @param courseId
     * @param type
     * @return
     */
    List<Map<String, Object>> selectIdAndNameByCourseId(@Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 查询当前课程最大的单元信息
     *
     * @param courseId
     * @return
     */
    UnitNew selectMaxUnitByCourseId(@Param("courseId") Long courseId);
}

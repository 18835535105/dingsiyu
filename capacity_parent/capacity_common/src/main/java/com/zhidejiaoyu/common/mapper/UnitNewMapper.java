package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.vo.course.UnitStudyStateVO;
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
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    List<Map<String,Long>> selectMapByGradeListAndVersionAndGrade(String version, List<String> gradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    List<Long> selectLessOrEqualsCurrentIdByCourseIdAndUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId);
    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    List<Map<String,Long>> selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(@Param("courseId") Long courseId, @Param("unitId") Long unitId);

    /**
     * 根据id集合查询id信息
     * @param unitIds
     * @return
     */
    List<Map<String,Object>> selectByIds(@Param("unitIds") List<Long> unitIds);

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    List<Long> selectIdsByCourseNames(@Param("courseNames") List<String> courseNames);
    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    List<Map<String,Long>> selectIdsMapByCourseNames(@Param("courseNames") List<String> courseNames);

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
    List<UnitStudyStateVO> selectIdAndNameByCourseId(@Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 查询语法模块当前课程最大的单元信息
     *
     * @param courseId
     * @return
     */
    UnitNew selectSyntaxMaxUnitByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询最大单元id
     *
     * @param unitId
     * @return
     */
    Long selectMaxUnitIdByUnitId(@Param("unitId") Long unitId);

    /**
     * 根据jointName模糊查询
     *
     * @param jointName
     * @return
     */
    UnitNew selectSyntaxUnitLikeJointName(String jointName);

    /**
     * 获取语法模块当前课程的下一个单元信息
     *
     * @param unitId
     * @param courseId
     * @return
     */
    UnitNew selectNextSyntaxUnitByCourseId(@Param("unitId") Long unitId, @Param("courseId") Long courseId);

    /**
     * 查询各个模块单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；3：语法；4：课文
     * @return
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Integer>> selectMaxGroupByUnitIsdAndType(@Param("unitIds") List<Long> unitIds, @Param("type") Integer type);

    /**
     * 根据courseIds查询unit信息
     * @param courseNewIds
     * @return
     */
    List<UnitNew> selectByCourseIds(@Param("courseIds") List<Long> courseNewIds);
}

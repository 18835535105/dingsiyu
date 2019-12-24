package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.UnitNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}

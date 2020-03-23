package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface CourseNewMapper extends BaseMapper<CourseNew> {

    /**
     * 通过单元id查询当前课程所属学段
     *
     * @param unitId
     * @return
     */
    String selectPhaseByUnitId(Long unitId);

    /**
     * 根据课程id查询学段
     *
     * @param courseId
     * @return
     */
    String selectPhaseById(@Param("courseId") Long courseId);

    /**
     * 根据单元id查询单元与年级的关系
     *
     * @param unitIds
     * @return
     */
    List<GradeAndUnitIdDTO> selectGradeAndLabelByUnitIds(@Param("unitIds") List<Long> unitIds);

    String selectGradeByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询各个课程下指定模块单元总个数
     *
     * @param courseIds
     * @param type      1:单词；2：句型；3：语法；4：课文
     * @return
     */
    @MapKey("courseId")
    Map<Long, Map<Long, Object>> countUnitByIds(@Param("courseIds") List<Long> courseIds, @Param("type") int type);

    /**
     * 查询单元所属的课程
     *
     * @param unitId
     * @return
     */
    CourseNew selectByUnitId(@Param("unitId") Long unitId);

    /**
     * 查询指定版本、年级的课程id
     *
     * @param version        版本
     * @param smallGradeList 年级集合
     * @return
     */
    List<Long> selectByGradeListAndVersionAndGrade(@Param("version") String version, @Param("smallGradeList") List<String> smallGradeList);

    /**
     * 批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    List<CourseNew> selectByIds(@Param("courseIds") List<Long> courseIds);
}

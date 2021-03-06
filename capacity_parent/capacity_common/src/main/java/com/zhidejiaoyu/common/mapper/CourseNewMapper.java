package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseExample;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @param type
     * @return
     */
    List<Long> selectByGradeListAndVersionAndGrade(@Param("version") String version, @Param("smallGradeList") List<String> smallGradeList, Integer type);

    /**
     * 批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    List<CourseNew> selectByIds(@Param("courseIds") List<Long> courseIds);

    /**
     * @param studentId
     * @param phase
     * @return
     */
    List<Map<String, Object>> selectIdAndVersionByStudentIdByPhase(@Param("studentId") Long studentId, @Param("phase") String phase);

    /**
     * 查询课程下各个单元单词数
     *
     * @param courseId
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Object>> selectUnitsWordSum(Long courseId);

    /**
     * 获取语法数据
     *
     * @param courseNews
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Object>> selectByCourseNews(@Param("courseNews") List<CourseNew> courseNews);

    /**
     * 获取课程数据
     * @param courseIds
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Object>> selectGradeAndLabelByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 根据课程名查询课程id
     */
    @Select("select id from course_new where version = #{version} and grade = #{grade} and label = #{label} and delStatus = 1")
    List<Integer> selectCourse(@Param("version") String version, @Param("grade") String grade,
                               @Param("label") String label);

    List<CourseNew> selectExperienceCourses();

    List<CourseNew> selectByExample(CourseExample example);

    /**
     * 根据学段查询版本
     *
     * @param studyParagraph 学段
     * @return
     */
    List<String> selectVersionByStudyParagraph(String studyParagraph);

    /**
     * 获取当前版本下所有课程id
     *
     * @param version
     * @return
     */
    @Select("select id from course_new where version = #{version}")
    List<Long> selectIdsByVersion(@Param("version") String version);

    /**
     * 查询学段中的课程id
     *
     * @param phaseArr
     * @param courseIds
     * @return
     */
    List<Long> selectIdsByPhasesAndIds(@Param("phaseArr") List<String> phaseArr, @Param("courseIds") List<Long> courseIds);


}

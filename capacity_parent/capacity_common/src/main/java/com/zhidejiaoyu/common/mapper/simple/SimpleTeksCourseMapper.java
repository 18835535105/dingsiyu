package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.TeksCourse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface SimpleTeksCourseMapper extends BaseMapper<TeksCourse> {

    /**
     * 查找课文课程
     *
     * @param version   版本
     * @param grade     年级
     * @param label     标签
     * @return
     */
    @Select("select id from teks_course where version = #{version} and grade = #{grade} and label = #{label} and delStatus = 1")
    List<Long> selectByVersionAndGradeAndLabel(@Param("version") String version, @Param("grade") String grade, @Param("label") String label);

    /**
     * 获取学段下所有课程版本
     *
     * @param phase
     * @param teacherId
     * @param courseName
     * @return
     */
    List<String> selectVersionByPhase(@Param("phase") String phase, @Param("teacherId") Integer teacherId, @Param("courseName") String courseName);

    /**
     * 获取当前课程版本下所有课程名称和课程 id(不在当前教师课程库中的课程)
     *
     * @param version
     * @param teacherId
     * @param phase
     * @return
     */
    List<Map<String, Object>> selectCourseNameByVersion(@Param("version") String version, @Param("teacherId") Integer teacherId, @Param("phase") String phase);

}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CourseConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface CourseConfigMapper extends BaseMapper<CourseConfig> {

    /**
     * 查询用户指定的配置信息
     *
     * @param userId
     * @param type  1:一键学习；2：自由学习
     * @return
     */
    List<CourseConfig> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") int type);

    List<Long> selectByUserId(@Param("userId") Long userId, @Param("gradeList") List<String> gradeList);

    /**
     * 查看是否有排课的内容
     *
     * @param userId
     */
    int countByUserIdAndType(@Param("userId") Long userId, @Param("type") int type);

    CourseConfig selectByTypeAndUserIdAndCourseId(@Param("type") int type, @Param("adminId") Long adminId, @Param("courseId") Long courseId);

    /**
     * 查询配置的课程id
     *
     * @param userId
     * @param type
     * @return
     */
    List<Long> selectCourseIdsByUserIdAndType(@Param("userId") Long userId, @Param("type") int type);

    /**
     * 查询指定用户配置的
     * @param userId
     * @param type
     * @param oneKeyLearn
     * @return
     */
    List<Long> selectCourseIdsByUserIdAndTypeAndOneKeyLearn(@Param("userId") Integer userId, @Param("type") int type, @Param("oneKeyLearn") int oneKeyLearn);
}

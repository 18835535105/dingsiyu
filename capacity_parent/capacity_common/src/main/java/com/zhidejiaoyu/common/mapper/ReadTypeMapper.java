package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读类型表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadTypeMapper extends BaseMapper<ReadType> {

    /**
     * 根据课程id获取信息
     * @param courseId
     * @return
     */
    List<ReadType> selByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程集合获取信息
     * @param courseIds
     * @return
     */
    List<ReadType> selByCourseList(@Param("courseIds") List<Long> courseIds);

    Integer selCountByCourseId(@Param("courseId") long courseId);
}

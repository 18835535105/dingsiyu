package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SyntaxCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 超级语法课程表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface SyntaxCourseMapper extends BaseMapper<SyntaxCourse> {

    /**
     * 通过courseNewId匹配相对应的语法课程
     *
     * @param courseIds
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Object>> selectByCourseNewIds(@Param("courseIds") List<Long> courseIds);
}

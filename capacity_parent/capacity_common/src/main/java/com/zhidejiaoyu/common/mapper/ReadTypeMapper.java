package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
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

    List<ReadType> selByCourseId(@Param("courseId") Long courseId);
}

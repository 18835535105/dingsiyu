package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadArder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 队长阅读与英语乐园文章表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadArderMapper extends BaseMapper<ReadArder> {


    List<ReadArder> selByCourseId(@Param("courseId") Long courseId,@Param("textTypes") Integer textTypes);
}

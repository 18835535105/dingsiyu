package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadJudge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读判断对错表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadJudgeMapper extends BaseMapper<ReadJudge> {


    List<ReadJudge> selectByTypeIdOrCourseId(@Param("typeId") Long typeId,@Param("courseId") Long courseId);
}

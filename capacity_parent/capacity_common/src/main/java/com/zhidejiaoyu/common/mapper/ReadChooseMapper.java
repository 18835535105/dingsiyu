package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadChoose;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读选择答案表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadChooseMapper extends BaseMapper<ReadChoose> {


    List<ReadChoose> selectByTypeIdOrCourseId(@Param("typeId") Long typeId,@Param("courseId") Long courseId);
}

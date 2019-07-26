package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ReadPicture;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-26
 */
public interface ReadPictureMapper extends BaseMapper<ReadPicture> {

    List<ReadPicture> selByCourseIdAndType(@Param("courseId") Long courseId,@Param("type") Integer type);
}

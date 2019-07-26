package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadWiseCounsel;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 队长阅读锦囊妙计表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadWiseCounselMapper extends BaseMapper<ReadWiseCounsel> {

    ReadWiseCounsel getByCourseId(@Param("courseId") Long courseId);
}

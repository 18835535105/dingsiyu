package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sun.tracing.dtrace.ProviderAttributes;
import com.zhidejiaoyu.common.pojo.ReadQuestionAnswering;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读回答问题答案表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadQuestionAnsweringMapper extends BaseMapper<ReadQuestionAnswering> {

    List<ReadQuestionAnswering> selectByTypeIdOrCourseId(@Param("typeId") Long typeId);
}

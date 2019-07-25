package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读内容表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadContentMapper extends BaseMapper<ReadContent> {

    List<ReadContent> selectByTypeId(@Param("typeId") Long typeId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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


    /**
     * 获取当前阅读类型下的所有句子信息
     *
     * @param readTypeId
     * @return
     */
    List<ReadContent> selectByReadTypeId(@Param("readTypeId") Long readTypeId);
    List<ReadContent> selectByTypeId(@Param("typeId") Long typeId);
}

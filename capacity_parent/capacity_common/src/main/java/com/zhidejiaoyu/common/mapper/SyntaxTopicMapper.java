package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface SyntaxTopicMapper extends BaseMapper<SyntaxTopic> {

    /**
     * 查询当前单元下的所有语法内容
     *
     * @param unitId
     * @return
     */
    List<SyntaxTopic> selectByUnitId(Long unitId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 智能版学习流程数据表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface StudyFlowNewMapper extends BaseMapper<StudyFlowNew> {

    /**
     * 通过learn_id查询当前学生的流程节点信息
     *
     * @param learnId
     * @return
     */
    StudyFlowNew selectByLearnId(@Param("learnId") Long learnId);
}

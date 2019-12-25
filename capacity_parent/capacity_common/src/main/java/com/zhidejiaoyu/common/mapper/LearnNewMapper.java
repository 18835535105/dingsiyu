package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface LearnNewMapper extends BaseMapper<LearnNew> {

    List<Map<String, Object>> selectStudyFiveStudent(@Param("studentIds") List<Long> studentIds);

    /**
     * 查询最大优先级对应的学习记录
     *
     * @param studentStudyPlanNew
     * @return
     */
    LearnNew selectByStudentStudyPlanNew(@Param("studentStudyPlanNew") StudentStudyPlanNew studentStudyPlanNew);
}

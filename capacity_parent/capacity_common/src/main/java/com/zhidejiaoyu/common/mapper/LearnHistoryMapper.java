package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnHistory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface LearnHistoryMapper extends BaseMapper<LearnHistory> {

    List<Map<String, Object>> selectStudyFiveStudent(@Param("studentIds") List<Long> studentIds);

    List<Map<String,Object>> selectStudyUnitByStudentId(@Param("studentId") Long studentId);
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnHistory;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface LearnHistoryMapper extends BaseMapper<LearnHistory> {

    List<Map<String, Object>> selectStudyFiveStudent(@Param("studentIds") List<Long> studentIds);

    List<Map<String, Object>> selectStudyUnitByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生id和单元id修改状态
     *
     * @param studentId
     * @param unitId
     * @param state
     */
    @Update("update learn_history set state = #{state} where student_id = #{studentId} and unit_id = #{unitId}")
    void updateStateByStudentIdAndUnitId(Long studentId, Long unitId, int state);
}

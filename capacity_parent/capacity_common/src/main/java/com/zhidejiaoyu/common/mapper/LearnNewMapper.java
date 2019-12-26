package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.apache.ibatis.annotations.Param;

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
public interface LearnNewMapper extends BaseMapper<LearnNew> {

    /**
     * 查询最大优先级对应的学习记录
     *
     * @param studentStudyPlanNew
     * @return
     */
    LearnNew selectByStudentStudyPlanNew(@Param("studentStudyPlanNew") StudentStudyPlanNew studentStudyPlanNew);

    /**
     * 根据学生id和单元id查询学习表数据
     *
     * @param studentId
     * @param unitId
     * @return
     */
    List<LearnNew> selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 获取接下来要学的词信息
     *
     * @param studentId
     * @param unitId
     * @param wodIds
     * @return
     */
    Map<String, Object> selectStudyMap(@Param("studentId") Long studentId,
                                       @Param("unitId") Long unitId,
                                       @Param("wordIds") List<Long> wodIds,
                                       @Param("type") Integer type,
                                       @Param("model") Integer model);

    /**
     * 查询当前单元是否已经学习过指定流程
     *
     * @param studentId
     * @param unitId
     * @param flowName
     * @return
     */
    int countByStudentIdAndFlow(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("flowName") String flowName);


    List<Long> selectByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                    @Param("easyOrHard") Integer easyOrHard);
}

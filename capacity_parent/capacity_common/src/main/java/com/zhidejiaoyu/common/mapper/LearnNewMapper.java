package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
@Repository
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
     * @param easyOrHard
     * @return
     */
    LearnNew selectByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                     @Param("easyOrHard") Integer easyOrHard);

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
                                       @Param("model") Integer model,
                                       @Param("group") Integer group);


    List<Long> selectIdByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                         @Param("easyOrHard") Integer easyOrHard, @Param("modelType") Integer modelType);

    @Select("select id from learn_new where student_id =#{studentId}")
    List<Long> selectIdByStudentId(@Param("studentId") Long studentId);

    @Select("select count(id) from learn_new where student_id =#{studentId}")
    int selectCountByStudentId(Long studentId);

    List<LearnNew> selectDelLearnIdByStudentIdAndNumber(@Param("studentId") Long studentId, @Param("number") int number);

    /**
     * 查询学习信息
     *
     * @param studentId
     * @param unitId
     * @param easyOrHard
     * @param modelType
     * @return
     */
    LearnNew selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                                 @Param("easyOrHard") Integer easyOrHard, @Param("modelType") int modelType);

    /**
     * 统计学生学习的所有单词数（去重）
     *
     * @param studentId
     * @return
     */
    int countLearnedWordCount(@Param("studentId") Long studentId);

    /**
     * 统计学生指定日期范围内学习的单词数
     *
     * @param studentId
     * @param beginDate
     * @param endDate
     * @return
     */
    int countLearnedWordCountByStartDateAndEndDate(@Param("studentId") Long studentId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /**
     * 根据学生id和类型获取单元id
     *
     * @param studentId
     * @param type
     */
    List<Long> getUnitIdByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") int type);

    /**
     * 获取指定日期学习的记录
     *
     * @param date
     * @return
     */
    List<LearnNew> selectByUpdateTime(Date date);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIds);
}

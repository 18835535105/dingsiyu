package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.pojo.LearnExtend;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 黄金记忆点 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface StudyCapacityMapper extends BaseMapper<StudyCapacity> {

     void deleteByTypeAndvocaId(@Param("list") List<Integer> listType,@Param("vocaId") Long vocaId) ;

    /**
     * 查询达到黄金记忆点的语法
     *
     * @param dto
     * @return
     */
    StudyCapacity selectLargerThanGoldTimeWithStudentIdAndUnitId(@Param("dto") NeedViewDTO dto);

    /**
     * 获取没有掌握的知识点
     *
     * @param dto
     * @return
     */
    StudyCapacity selectUnKnownByStudentIdAndUnitId(@Param("dto") NeedViewDTO dto);

    /**
     * 跟句学生id，单元id，type获取学生语法的记忆追踪信息
     *
     * @param learn
     * @param learnExtend
     * @param type
     * @return
     */
    StudyCapacity selectByLearn(@Param("learn") LearnNew learn, @Param("extend") LearnExtend learnExtend, @Param("type") int type);

    /**
     * 删除学生指定课程语法的记忆追踪信息
     *
     * @param studentId
     * @param courseId
     */
    void deleteSyntaxByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取需要复习的单元
     *
     * @param unitId
     * @param studentId
     * @param dateTime
     * @return
     */
    Map<String, Object> selectNeedReviewWord(@Param("unitId") Long unitId,
                                             @Param("studentId") Long studentId,
                                             @Param("dateTime") String dateTime,
                                             @Param("type") Integer type,
                                             @Param("easyOrHard") Integer easyOrHard,
                                             @Param("group") Integer group);

    List<StudyCapacity> selectByStudentIdAndUnitIdAndWordIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                                   @Param("wordId") Long wordId, @Param("type") Integer type);

    void deleteByStudentIdAndUnitIdAndVocabulary(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                 @Param("wordId") Long wordId, @Param("type") Integer type);

    StudyCapacity selectLearnHistory(@Param("unitId") Long unitId,
                                     @Param("studentId") Long studentId,
                                     @Param("dateTime") String dateTime,
                                     @Param("type") Integer type,
                                     @Param("group") Integer group,
                                     @Param("wordeIds") List<Long> wordIds);

    /**
     * 一键学习跳转接点时删除当前单元group的记忆追踪数据，防止删除学习数据后匹配不到学习记录
     *
     * @param studentId
     * @param unitId
     * @param group
     */
    @Delete("delete from study_capacity where student_id = #{studentId} and unit_id = #{unitId} and `group` = #{group}")
    void deleteByStudentIdAndUnitIdAndGroup(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                            @Param("group") Integer group);

    /**
     * 自由学习跳转接点是删除当前单元、学习模块group的记忆追踪数据
     *
     * @param studentId
     * @param unitId
     * @param type
     */
    @Delete("delete from study_capacity where student_id = #{studentId} and unit_id = #{unitId} and type = #{type}")
    void deleteByStudentIdAndUnitIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                           @Param("type") Integer type);
}

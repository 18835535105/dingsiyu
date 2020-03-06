package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnExtend;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import org.apache.ibatis.annotations.Delete;
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
public interface LearnExtendMapper extends BaseMapper<LearnExtend> {

    /**
     * 查询当前单元是否已经学习过指定流程
     *
     * @param studentId
     * @param unitId
     * @param flowName
     * @return
     */
    int countByStudentIdAndFlow(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("flowName") String flowName);

    /**
     * 根据学习id删除记录
     *
     * @param learnId
     */
    @Delete("delete from learn_extend where learn_id = #{learnId}")
    void deleteByLearnId(Long learnId);

    /**
     * 查询当前单元所有已经学习过的单词信息
     *
     * @param unitId     单元id
     * @param studentId  学生id
     * @param studyModel 模块 1，单词播放机 2，单词图鉴 3，慧记忆 4，会听写
     *                   5，慧默写 6，单词游戏 7，句型翻译 8，句型听力 9，音译练习
     *                   10，句型默写 11，课文试听 12，课文训练 13，闯关测试 14，课文跟读
     *                   15，读语法 16，选语法 17，写语法 18，语法游戏
     * @return
     */
    List<Long> selectByUnitIdAndStudentIdAndType(@Param("unitId") Long unitId, @Param("studentId") Long studentId,
                                                 @Param("type") String studyModel, @Param("modelType") Integer modelType);

    /**
     * 获取当前模块的所有数据
     *
     * @param learnId
     * @param wordId
     * @param studyModel
     * @return
     */
    List<LearnExtend> selectByLearnIdsAndWordIdAndStudyModel(@Param("learnId") Long learnId, @Param("wordId") Long wordId,
                                                             @Param("studyModel") String studyModel);

    Integer countLearnWord(@Param("learnId") Long learnId, @Param("unitId") Long unitId,
                           @Param("group") Integer group, @Param("studyModel") String studyModel);

    List<Map<String, Object>> selectLearnedByUnitId(@Param("unitId") Long unitId,
                                                    @Param("start") int startRow, @Param("end") int end);

    /**
     * 删除指定单元模块的学习详情
     *
     * @param learnId
     * @param modelName
     */
    @Delete("delete from learn_extend where learn_id = #{learnId} and study_model = #{modelName}")
    void deleteByUnitIdAndStudyModel(@Param("learnId") Long learnId, @Param("modelName") String modelName);

    /**
     * 查询句子学习次数
     *
     * @param learnId
     * @param wordId
     * @return
     */
    Integer countByLearnIdAndWordIdAndType(@Param("learnId") Long learnId, @Param("wordId") Long wordId);

    /**
     * 查询已学习的数据
     *
     * @param learnId
     * @param wordId
     * @return
     */
    LearnExtend selectByLearnIdAndWordIdAndType(@Param("learnId") Long learnId, @Param("wordId") Long wordId);

    /**
     * 查询指定单词的学习次数
     *
     * @param studyCapacity
     * @param studyModel
     * @return
     */
    Integer selectStudyCount(@Param("studyCapacity") StudyCapacity studyCapacity, @Param("studyModel") String studyModel);

    /**
     * 统计学生当前单元学习的语法内容
     *
     * @param studentId
     * @param unitId
     * @param studyModel 语法模块
     * @return
     */
    int countLearnedSyntax(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel);

    List<Long> selectWordListByStudentId(@Param("studentId") long studentId);
}

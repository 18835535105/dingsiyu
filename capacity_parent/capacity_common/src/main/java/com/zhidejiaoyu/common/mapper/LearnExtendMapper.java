package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnExtend;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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
}

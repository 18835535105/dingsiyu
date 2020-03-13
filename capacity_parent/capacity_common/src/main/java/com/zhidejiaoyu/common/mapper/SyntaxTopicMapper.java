package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface SyntaxTopicMapper extends BaseMapper<SyntaxTopic> {

    /**
     * 查询当前单元下的所有选语法内容
     *
     * @param unitId
     * @return
     */
    List<SyntaxTopic> selectSelectSyntaxByUnitId(Long unitId);

    /**
     * 统计当前单元指定类型的语法内容个数
     *
     * @param unitId
     * @param type   1：选语法；2：所有
     * @return
     */
    Integer countByUnitIdAndType(@Param("unitId") Long unitId, @Param("type") int type);

    /**
     * 查询当前单元下下一个未学习的语法内容
     *
     * @param studentId
     * @param unitId
     * @param studyModel 学习模块
     * @return
     */
    SyntaxTopic selectNextByUnitIdAndType(@Param("studentId") Long studentId, @Param("unitId") Long unitId ,@Param("studyModel") String studyModel);
}

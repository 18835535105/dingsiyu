package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 知识点表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface KnowledgePointMapper extends BaseMapper<KnowledgePoint> {

    /**
     * 获取当前单元的知识点个数
     *
     * @param unitId
     * @return
     */
    int countByUnitId(Long unitId);

    /**
     * 获取单元下一个可学习的知识点
     *
     * @param studentId
     * @param unitId
     * @return
     */
    KnowledgePoint selectNextByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}

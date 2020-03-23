package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SyntaxUnitTopicNew;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 超级语法单元-内容表
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface SyntaxUnitTopicNewMapper extends BaseMapper<SyntaxUnitTopicNew> {


    /**
     * 统计当前单元group下语法题个数
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("select count(id) from syntax_unit_topic_new where unit_id = #{unitId} and `group` = #{group}")
    int countByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查找当前单元的下一个group
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("select `group` from syntax_unit_topic_new where unit_id = #{unitId} and `group` > #{group} order by id limit 1")
    Integer selectNextGroup(Long unitId, Integer group);
}

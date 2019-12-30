package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitSentenceNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 单元-例句(中间表) Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface UnitSentenceNewMapper extends BaseMapper<UnitSentenceNew> {

    /**
     * 查询当前单元下个group
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("select `group` from unit_sentence_new where unit_id = #{unitId} and `group` > #{group} order by id limit 1")
    Integer selectNextGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    /**
     * 查询句型模块当前单元group的个数
     *
     * @param unitId
     * @param group
     * @return
     */
    @Select("SELECT COUNT(1) FROM unit_sentence_new WHERE (unit_id = #{unitId} AND `group` = #{group})")
    Integer countByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    String selectSentenceChineseByUnitIdAndSentenceId(@Param("unitId") Long unitId,@Param("sentenceId") Long sentenceId);
}

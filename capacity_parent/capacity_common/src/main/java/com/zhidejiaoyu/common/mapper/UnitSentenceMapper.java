package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitSentence;
import com.zhidejiaoyu.common.pojo.UnitSentenceExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UnitSentenceMapper {
    int countByExample(UnitSentenceExample example);

    int deleteByExample(UnitSentenceExample example);

    int insert(UnitSentence record);

    int insertSelective(UnitSentence record);

    List<UnitSentence> selectByExample(UnitSentenceExample example);

    int updateByExampleSelective(@Param("record") UnitSentence record, @Param("example") UnitSentenceExample example);

    int updateByExample(@Param("record") UnitSentence record, @Param("example") UnitSentenceExample example);

    /**
     * 获取当前单元下的例句总数
     * @param unitId    当前单元id
     * @return  例句总数
     */
    @Select("select count(unit_id) from unit_sentence where unit_id = #{unitId}")
    int countByUnitId(@Param("unitId")Long unitId);

    /**
     * 获取当前单元下的例句总数
     * @param courseId    当前单元id
     * @return  例句总数
     */
    @Select("select count(us.unit_id) from unit_sentence us, unit u where u.id = us.unit_id and u.course_id = #{courseId}")
    long countByCourseId(@Param("courseId")Long courseId);

    /**
     * 根据例句id查询其所属的所有单元
     * @param id
     * @return
     */
    @Select("select unit_id from unit_sentence where  sentence_id = #{id}")
    List<Long> selectUnitIdsBySentenceId(@Param("id") Long id);

    /**
     * 查询指定单元下所有例句的个数
     *
     * @param unitIds
     * @return
     */
    int countSentenceByUnitIds(@Param("unitIds") List<Long> unitIds);

    /**
     *查询指定单元下所有例句的个数
     *
     * @param unitId
     * @return
     */
    @Select("select count(distinct uv.sentence_id) from unit_sentence uv where uv.unit_id = #{unitId}")
    Long selectSentenceCountByUnitId(@Param("unitId") long unitId);

    /**
     * 获取课程下所有例句的单元信息
     *
     *
     * @param studentId
     * @param courseIds
     * @return
     */
    List<Map<String, Object>> selectUnitIdAndNameByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds);

    List<Map<String,Object>> selUnitIdAndNameByCourseIdsAndStartUnitIdAndEndUnitId(@Param("courseId") Long courseId,@Param("startUnitId") Long unitId,@Param("endUnitId") Long endUnitId);
}
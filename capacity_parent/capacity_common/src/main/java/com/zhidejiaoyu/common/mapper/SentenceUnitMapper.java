package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SentenceUnit;
import com.zhidejiaoyu.common.pojo.Unit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface SentenceUnitMapper extends BaseMapper<SentenceUnit> {

    /**
     * 根据单元id获取所属课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from sentence_unit where id = #{unitId}")
    Long selectCourseIdByUnitId(Long unitId); 

    SentenceUnit selectByPrimaryKey(Long id);

    @Select("select unit_name from unit where id = #{unitId}")
    String getUnitNameByUnitId(@Param("unitId") Long unitId);

    /**
     * 根据单元id查询课程id
     * @param unitId 单元id
     * @return
     */
    Long getCourseIdById(@Param("unitId") Integer unitId);
}

package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SentenceUnit;
import com.zhidejiaoyu.common.pojo.Unit;
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

}

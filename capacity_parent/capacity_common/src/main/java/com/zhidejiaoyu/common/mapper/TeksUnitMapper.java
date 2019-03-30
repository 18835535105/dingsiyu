package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.TeksUnit;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface TeksUnitMapper extends BaseMapper<TeksUnit> {

    /**
     * 根据单元id获取所属课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from teks_unit where id = #{unitId}")
    Long selectCourseIdByUnitId(Long unitId);


}

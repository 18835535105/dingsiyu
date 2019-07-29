package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
public interface MemoryCapacityMapper extends BaseMapper<MemoryCapacity> {

    /**
     * 判断今天是否已经学过记忆容量了
     * @param studentId
     * @return
     */
    @Select("select count(id) from memory_capacity where student_id=#{studentId} and to_days(create_time) = to_days(now())")
    Integer selTodayMemoryCapacity(@Param("studentId") Long studentId);
}

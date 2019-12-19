package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SchoolTime;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface SchoolTimeMapper extends BaseMapper<SchoolTime> {

    /**
     * 查询指定用户的校区时间表
     *
     * @param userId
     * @param type
     * @param month
     * @param week
     * @return
     */
    SchoolTime selectByUserIdAndTypeAndMonthAndWeek(@Param("userId") Long userId, @Param("type") int type,
                                                    @Param("month") Integer month, @Param("week") Integer week);
}

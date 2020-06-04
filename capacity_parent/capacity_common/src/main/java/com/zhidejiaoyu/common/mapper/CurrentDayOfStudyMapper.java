package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-06-02
 */
public interface CurrentDayOfStudyMapper extends BaseMapper<CurrentDayOfStudy> {


    List<CurrentDayOfStudy> selectByDate(@Param("date") String date);
}

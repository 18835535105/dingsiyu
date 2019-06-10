package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Ranking;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 学生排名统计表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-02
 */
public interface RankingMapper extends BaseMapper<Ranking> {

    Ranking selByStudentId(@Param("studentId") Long studentId);
}

package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentRank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-08
 */
public interface SimpleStudentRankMapper extends BaseMapper<StudentRank> {

    void insertList(@Param("rankList") List<StudentRank> rankList);

    StudentRank selByStudentIdAndType(@Param("studentId") Long studentId, @Param("type") int type);
}

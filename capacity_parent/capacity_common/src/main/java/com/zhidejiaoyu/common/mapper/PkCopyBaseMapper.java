package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PkCopyBase;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-03-17
 */
public interface PkCopyBaseMapper extends BaseMapper<PkCopyBase> {

    /**
     * 查询指定类型的副本
     *
     * @param studentCount 校区学生个数
     * @return
     */
    List<Map<String, Object>> selectSchoolPkBaseInfoByCount(@Param("studentCount") int studentCount);

    /**
     * 查询学生单日副本挑战情况
     *
     * @param studentId
     * @param firstDayOfWeek 当前周第一天日期
     * @return
     */
    List<Map<String, Object>> selectPersonPkInfoByStudentId(@Param("studentId") Long studentId, @Param("firstDayOfWeek") Date firstDayOfWeek);
}

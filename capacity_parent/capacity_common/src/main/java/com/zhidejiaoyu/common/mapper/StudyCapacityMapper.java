package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 黄金记忆点 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface StudyCapacityMapper extends BaseMapper<StudyCapacity> {

    /**
     * 查询达到黄金记忆点的语法
     *
     * @param studentId
     * @param unitId
     * @param type
     * @return
     */
    StudyCapacity selectLargerThanGoldTimeWithStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    /**
     * 获取没有掌握的知识点
     *
     * @param studentId
     * @param unitId
     * @param type
     * @return
     */
    StudyCapacity selectUnKnownByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    /**
     * 跟句学生id，单元id，type获取学生语法的记忆追踪信息
     *
     * @param learn
     * @param type
     * @return
     */
    StudyCapacity selectByLearn(@Param("learn") Learn learn, @Param("type") int type);
}

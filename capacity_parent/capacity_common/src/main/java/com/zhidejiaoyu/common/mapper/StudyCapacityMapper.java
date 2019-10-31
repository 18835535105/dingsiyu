package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
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
     * @param dto
     * @return
     */
    StudyCapacity selectLargerThanGoldTimeWithStudentIdAndUnitId(@Param("dto") NeedViewDTO dto);

    /**
     * 获取没有掌握的知识点
     *
     * @param dto
     * @return
     */
    StudyCapacity selectUnKnownByStudentIdAndUnitId(@Param("dto") NeedViewDTO dto);

    /**
     * 跟句学生id，单元id，type获取学生语法的记忆追踪信息
     *
     * @param learn
     * @param type
     * @return
     */
    StudyCapacity selectByLearn(@Param("learn") Learn learn, @Param("type") int type);
}

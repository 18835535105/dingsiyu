package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PkCopyBase;
import com.zhidejiaoyu.common.vo.ship.SchoolPkBaseInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param type
     * @param studentCount 校区学生个数
     * @return
     */
    List<SchoolPkBaseInfoVO> selectSchoolPkBaseInfoByType(@Param("type") int type, @Param("studentCount") int studentCount);
}

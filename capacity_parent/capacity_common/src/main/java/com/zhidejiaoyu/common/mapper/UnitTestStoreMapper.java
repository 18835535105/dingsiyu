package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.UnitTestStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-04-16
 */
public interface UnitTestStoreMapper extends BaseMapper<UnitTestStore> {

    /**
     * 查询当前单元下的金币试卷信息
     *
     * @param unitId
     * @return
     */
    UnitTestStore selectByUnitId(@Param("unitId") Long unitId);
}

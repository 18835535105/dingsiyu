package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.TestStore;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-04-16
 */
public interface TestStoreMapper extends BaseMapper<TestStore> {

    /**
     * 查询当前单元下的测试题
     *
     * @param unitId
     * @return
     */
    List<GoldTestVO> selectSubjectsByUnitId(@Param("unitId") Long unitId);
}

package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.TeksNew;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课文表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface TeksNewMapper extends BaseMapper<TeksNew> {

    List<TeksNew> selTeksByUnitIdAndGroup(@Param("unitId") Long unitId, @Param("group") Integer group);

    List<TeksNew> getTwentyTeks();

    TeksNew replaceTeks(@Param("sentence") String sentence);
}

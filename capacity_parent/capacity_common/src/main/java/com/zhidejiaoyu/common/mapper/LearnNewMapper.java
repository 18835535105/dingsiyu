package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LearnNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface LearnNewMapper extends BaseMapper<LearnNew> {


    /**
     * 获取接下来要学的词信息
     *
     * @param studentId
     * @param unitId
     * @param wodIds
     * @return
     */
    Map<String, Object> selectStudyMap(@Param("studentId") Long studentId,
                                       @Param("unitId") Long unitId,
                                       @Param("wordIds") List<Long> wodIds,
                                       @Param("type") Integer type,
                                       @Param("model") Integer model);
}

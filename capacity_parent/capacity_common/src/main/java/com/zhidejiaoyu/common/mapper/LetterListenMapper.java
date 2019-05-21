package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LetterListen;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-20
 */
public interface LetterListenMapper extends BaseMapper<LetterListen> {

    Integer selStudyCountByUnit(Long unitId);
}

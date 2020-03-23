package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Adsense;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface AdsenseMapper extends BaseMapper<Adsense> {

    /**
     * 倒序查询5个广告位
     *
     * @return
     * @param schoolAdminId
     */
    List<Adsense> selectOrderByUpdateTimeLimitFive(Integer schoolAdminId);
}

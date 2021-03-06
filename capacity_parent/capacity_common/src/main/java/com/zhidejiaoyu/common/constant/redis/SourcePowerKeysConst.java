package com.zhidejiaoyu.common.constant.redis;

import com.zhidejiaoyu.common.constant.ServerNoConstant;

/**
 * 源分战力排行缓存key
 *
 * @author: wuchenxi
 * @date: 2020/2/28 11:08:08
 */
public interface SourcePowerKeysConst {

    /**
     * 全国排行
     */
    String COUNTRY_RANK = "SOURCE_POWER_COUNTRY_RANK";

    /**
     * 校区排行
     */
    String SCHOOL_RANK = "SOURCE_POWER_SCHOOL_RANK:" + ServerNoConstant.SERVER_NO + ":";

    /**
     * 同服务器排行
     */
    String SERVER_RANK = "SOURCE_POWER_SERVER_RANK:" + ServerNoConstant.SERVER_NO;
}

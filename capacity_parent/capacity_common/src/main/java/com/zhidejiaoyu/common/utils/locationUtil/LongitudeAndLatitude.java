package com.zhidejiaoyu.common.utils.locationUtil;

import lombok.Data;

/**
 * 经纬度封装类
 *
 * @author wuchenxi
 * @date 2019-05-25
 */
@Data
public class LongitudeAndLatitude {
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 地址
     */
    String addresses;
}

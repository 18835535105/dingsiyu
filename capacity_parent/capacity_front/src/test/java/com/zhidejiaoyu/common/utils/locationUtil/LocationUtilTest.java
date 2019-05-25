package com.zhidejiaoyu.common.utils.locationUtil;

import com.zhidejiaoyu.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-05-25
 */
@Slf4j
public class LocationUtilTest extends BaseTest {

    @Autowired
    private LocationUtil locationUtil;

    @Test
    public void testGetLongitudeAndLatitude() {
        LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude("101.19.104.33");
        LongitudeAndLatitude longitudeAndLatitude1 = locationUtil.getLongitudeAndLatitude("36.17.88.199");
        log.info(longitudeAndLatitude.toString());
        log.info(longitudeAndLatitude1.toString());
    }

    @Test
    public void testGetDistance() {
        LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude("101.19.104.33");
        LongitudeAndLatitude longitudeAndLatitude1 = locationUtil.getLongitudeAndLatitude("36.17.88.199");
        log.info(longitudeAndLatitude.toString());
        log.info(longitudeAndLatitude1.toString());
        int distance = locationUtil.getDistance(longitudeAndLatitude, longitudeAndLatitude1);
        log.info("distance={}", distance);
    }

}

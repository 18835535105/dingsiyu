package com.zhidejiaoyu.common.utils.locationUtil;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * 定位工具
 *
 * @author wuchenxi
 * @date 2019-05-25
 */
@Slf4j
@Component
public class LocationUtil {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据经纬度精确定位
     */
    @Value("${map.tencent.url}")
    private String mapUrl;

    @Value("${map.tencent.key}")
    private String mapKey;

    @Value("${distance.tencent.url}")
    private String distanceUrl;

    @Value("${location.tencent.url}")
    private String locationUrl;

    /**
     * 响应码
     */
    private static final String STATUS = "status";

    /**
     * 通过坐标计算两地的距离
     *
     * @param from  起点坐标
     * @param to    终点坐标
     * @return  两点距离 单位：米
     */
    public int getDistance(LongitudeAndLatitude from, LongitudeAndLatitude to) {
        if (from == null || to == null) {
            return 0;
        }

        String url = this.getDistanceUrl(from, to);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getInteger(STATUS) == 0) {
            Integer distance = jsonObject.getJSONObject("result").getJSONArray("elements").getJSONObject(0).getInteger("distance");
            return Objects.equals(distance, -1) ? 0 : distance;
        } else {
            log.warn("获取坐标距离出错！响应信息：{}, 参数：from={}, to={}", body, from.toString(), to.toString());
        }
        return 0;
    }

    private String getDistanceUrl(LongitudeAndLatitude from, LongitudeAndLatitude to) {
        return distanceUrl +
                    "?key=" + mapKey +
                    "&mode=walking" +
                    "&from=" + from.getLatitude() + "," + from.getLongitude() +
                    "&to=" + to.getLatitude() + "," + to.getLongitude();
    }

    /**
     * 根据 id 获取精确位置
     *
     * @param ip
     * @return
     */
    public LongitudeAndLatitude getLongitudeAndLatitude(String ip) {
        String url = locationUrl + "?key=" + mapKey + "&ip=" + ip;
        ResponseEntity<String> ipResponse = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(ipResponse.getBody());
        if (jsonObject.getInteger(STATUS) == 0) {
            JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
            LongitudeAndLatitude longitudeAndLatitude = new LongitudeAndLatitude();
            longitudeAndLatitude.setLatitude(location.getString("lat"));
            longitudeAndLatitude.setLongitude(location.getString("lng"));

            // 根据经纬度查询位置
            this.getLocation(longitudeAndLatitude);
            return longitudeAndLatitude;
        } else {
            log.error("根据 ip 查找经纬度出错！响应信息：{}，参数：{}", jsonObject.toString(), ip);
        }
        return new LongitudeAndLatitude();
    }

    /**
     * 根据经纬度查询位置
     *
     * @param longitudeAndLatitude
     * @return
     */
    private void getLocation(LongitudeAndLatitude longitudeAndLatitude) {
        String url = mapUrl + "?location=" + longitudeAndLatitude.getLatitude() + "," + longitudeAndLatitude.getLongitude() + "&key=" + mapKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        if (jsonObject.getInteger(STATUS) == 0) {
            longitudeAndLatitude.setAddresses(jsonObject.getJSONObject("result").getString("address"));
        } else {
            log.error("根据经纬度获取地址失败！响应信息：{}, 参数：{}", jsonObject.toString(), longitudeAndLatitude.toString());
        }
    }


}

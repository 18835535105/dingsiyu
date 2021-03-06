package com.zhidejiaoyu.common.utils.locationUtil;

import com.alibaba.csp.ahas.shaded.com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 定位工具
 * <ul>
 *     <li> 腾讯地图，接口地址：https://lbs.qq.com/webservice_v1/guide-gcoder.html</li>
 * </ul>
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
    private static final String mapUrl = "https://apis.map.qq.com/ws/geocoder/v1/";

    private static final String mapKey = "UGPBZ-SVC6X-O3W43-TYLAQ-CVERH-OFF65";

    /**
     * 腾讯 ip 定位
     */
    private static final String locationUrl = "https://apis.map.qq.com/ws/location/v1/ip";

    /**
     * 响应码
     */
    private static final String STATUS = "status";

    /**
     * 通过坐标计算两地的距离
     *
     * @param from 起点坐标
     * @param to   终点坐标
     * @return 两点距离 单位：米
     */
    public int getDistance(LongitudeAndLatitude from, LongitudeAndLatitude to) {
        if (from == null || to == null || from.getLongitude() == null || to.getLongitude() == null || from.getLatitude() == null || to.getLatitude() == null) {
            return 0;
        }
        double dx, dy, dew;
        double distance;
        // 地球半径 m
        double r = 6370693.5;
        // 角度转换为弧度
        // PI/180.0
        double pi180 = 0.01745329252;
        double ew1 = Double.parseDouble(from.getLongitude()) * pi180;
        double ns1 = Double.parseDouble(from.getLatitude()) * pi180;
        double ew2 = Double.parseDouble(to.getLongitude()) * pi180;
        double ns2 = Double.parseDouble(to.getLatitude()) * pi180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        // 2*PI
        double pi2 = 2 * Math.PI;
        if (dew > Math.PI) {
            dew = pi2 - dew;
        } else if (dew < -Math.PI) {
            dew = pi2 + dew;
        }
        // 东西方向长度(在纬度圈上的投影长度)

        dx = r * Math.cos(ns1) * dew;
        // 南北方向长度(在经度圈上的投影长度)
        dy = r * (ns1 - ns2);
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return (int) distance;
    }

    /**
     * 根据 ip 获取精确位置
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

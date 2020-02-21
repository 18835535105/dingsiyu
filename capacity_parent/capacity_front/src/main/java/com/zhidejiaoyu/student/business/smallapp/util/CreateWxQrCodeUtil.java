package com.zhidejiaoyu.student.business.smallapp.util;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.student.business.smallapp.constant.SmallAppApiConstant;
import com.zhidejiaoyu.student.business.smallapp.dto.GetUnlimitedQRCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成微信小程序码工具类
 *
 * @author: wuchenxi
 * @date: 2020/2/20 09:48:48
 */
@Slf4j
@Component
public class CreateWxQrCodeUtil {


    private static RestTemplate restTemplate;

    @Resource
    private RestTemplate restTemplateTmp;

    @PostConstruct
    public void init() {
        restTemplate = this.restTemplateTmp;
    }

    /**
     * 获取小程序二维码，适用于需要的码数量较少的业务场景。通过该接口生成的小程序码，永久有效，有数量限制
     *
     * @param path  扫码进入的小程序页面路径
     * @param width 图片宽度
     * @return
     */
    public static String createQRCode(String path, Integer width) {
        String url = SmallAppApiConstant.CREATE_AQR_CODE + AccessTokenUtil.getAccessToken();

        try {
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("path", path);
            paramMap.put("width", width);
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, JSON.toJSONString(paramMap), String.class);
            byte[] zp = stringResponseEntity.getBody().getBytes();

            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encodeBuffer(zp).trim();
        } catch (RestClientException e) {
            log.error("生成小程序码失败！", e);
            throw new ServiceException("生成小程序码失败！");
        }
    }

    /**
     * 获取小程序二维码，适用于需要的码数量较多的业务场景。通过该接口生成的小程序码，永久有效，无数量限制
     *
     * @param dto
     * @return  图片base64字符串
     */
    public static byte[] getUnlimited(GetUnlimitedQRCodeDTO dto) {
//        return AccessTokenUtil.getAccessToken();

        String url = SmallAppApiConstant.GET_UNLIMIT_QR_CODE + AccessTokenUtil.getAccessToken();

        try {
            ResponseEntity<byte[]> responseEntity = restTemplate.postForEntity(url, JSON.toJSONString(dto), byte[].class);

            return responseEntity.getBody();

        } catch (RestClientException e) {
            log.error("生成小程序码失败！", e);
            throw new ServiceException("生成小程序码失败！");
        }
    }

}

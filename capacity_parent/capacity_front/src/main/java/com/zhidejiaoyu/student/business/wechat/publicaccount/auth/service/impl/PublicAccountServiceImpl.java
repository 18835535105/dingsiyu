package com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.MacIpUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.ConfigVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.UserInfoVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ApiConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ConfigConstant;
import com.zhidejiaoyu.student.business.wechat.publicaccount.util.UserInfoUtil;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.AccessTokenVO;
import com.zhidejiaoyu.student.business.wechat.util.JsApiTicketUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author: wuchenxi
 * @date: 2020/4/28 10:33:33
 */
@Slf4j
@Service
public class PublicAccountServiceImpl implements PublicAccountService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private LocationUtil locationUtil;

    @Override
    public ServerResponse<Object> getOpenId(String code) {
        String publicAccountOpenId = UserInfoUtil.getPublicAccountOpenId(code);
        return ServerResponse.createBySuccess(publicAccountOpenId);
    }

    @Override
    public ServerResponse<Object> getCard(String cardName) {
        try {
            String ip = MacIpUtil.getIpAddr(HttpUtil.getHttpServletRequest());
            LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude(ip);

            // todo:获取地址名，拼接 地址/cardName返回给前端图片路径，如果没有找到指定的地址，返回共有的海报

        } catch (Exception e) {
            log.error("获取学生登录IP地址出错，error=[{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public ServerResponse<Object> getUserInfo(String code) {
        AccessTokenVO publicAccountAuthAccessTokenVO = UserInfoUtil.getPublicAccountAuthAccessTokenVO(code);
        String userInfoApiUrl = ApiConstant.getUserInfoApi(publicAccountAuthAccessTokenVO.getAccess_token(), publicAccountAuthAccessTokenVO.getOpenid());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(userInfoApiUrl, String.class);
        UserInfoVO userInfoVO = JSON.parseObject(forEntity.getBody(), UserInfoVO.class);

        log.info("公众号授权获取用户信息结果：{}", forEntity.getBody());
        if (userInfoVO == null) {
            log.error("获取公众号用户信息失败, body = null！");
            throw new ServiceException("获取公众号用户信息失败！");
        }

        return ServerResponse.createBySuccess(userInfoVO);
    }

    @Override
    public ServerResponse<Object> getConfig(String currentUrl) {

        long timeStamp = System.currentTimeMillis() / 1000;
        String nonceStr = UUID.randomUUID().toString();

        // 获取签名
        String string1 = "jsapi_ticket=" + JsApiTicketUtil.getJsApiTicket()
                + "&noncestr=" + nonceStr
                + "&timestamp=" + timeStamp
                + "&url=" + currentUrl;
        String signature = DigestUtils.sha1Hex(string1);
        log.info("string1={}", string1);
        log.info("jsapi_ticket={}", JsApiTicketUtil.getJsApiTicket());
        log.info("noncestr={}", nonceStr);
        log.info("timestamp={}", timeStamp);
        log.info("url={}", currentUrl);
        log.info("signature={}", signature);

        ConfigVO configVO = new ConfigVO();
        configVO.setAppId(ConfigConstant.APP_ID);
        configVO.setTimestamp(timeStamp);
        configVO.setNonceStr(nonceStr);
        configVO.setSignature(signature);


        return ServerResponse.createBySuccess(configVO);
    }

    public static void main(String[] args) {
        System.out.println(DigestUtils.sha1Hex("jsapi_ticket=kgt8ON7yVITDhtdwci0qefd58moX9kYv1PoEfRE7KBwKlgbHD55vQnhPT48sJVdUKG2xMGI-Qm3KeQjHbLJAiA&noncestr=fc224cc5-474a-445f-a533-6366095bc30c&timestamp=1590722886&url=https://test.shell.yydz100.com/partner/?openId=o7jCDw8qXrGFQ1McqPb5h0Ye2PoQ&headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/rwXoHxiaKh0JDFiaEP3WjuLQsdPOg5CszmSpQczTiaxsicicoMhZmf2hguoicTcdNapBY4gXx8HT97gckc9O62896SlQ/132&nickname=转角的天空╭(╯ε╰)╮&blank=blank&from=groupmessage"));
    }
}


//jsapi_ticket=kgt8ON7yVITDhtdwci0qefd58moX9kYv1PoEfRE7KBwKlgbHD55vQnhPT48sJVdUKG2xMGI-Qm3KeQjHbLJAiA&noncestr=dee49b25-20d4-45ae-b968-9a85170cba8a&timestamp=1590723489998&url=https://test.shell.yydz100.com/partner/?openId=o7jCDw8qXrGFQ1McqPb5h0Ye2PoQ&headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/rwXoHxiaKh0JDFiaEP3WjuLQsdPOg5CszmSpQczTiaxsicicoMhZmf2hguoicTcdNapBY4gXx8HT97gckc9O62896SlQ/132&nickname=转角的天空╭(╯ε╰)╮&blank=blank&from=groupmessage
//jsapi_ticket=kgt8ON7yVITDhtdwci0qefd58moX9kYv1PoEfRE7KBwKlgbHD55vQnhPT48sJVdUKG2xMGI-Qm3KeQjHbLJAiA&noncestr=fc224cc5-474a-445f-a533-6366095bc30c&timestamp=1590722886&url=https://test.shell.yydz100.com/partner/?openId=o7jCDw8qXrGFQ1McqPb5h0Ye2PoQ&headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/rwXoHxiaKh0JDFiaEP3WjuLQsdPOg5CszmSpQczTiaxsicicoMhZmf2hguoicTcdNapBY4gXx8HT97gckc9O62896SlQ/132&nickname=转角的天空╭(╯ε╰)╮&blank=blank&from=groupmessage
//jsapi_ticket=kgt8ON7yVITDhtdwci0qefd58moX9kYv1PoEfRE7KBwKlgbHD55vQnhPT48sJVdUKG2xMGI-Qm3KeQjHbLJAiA&noncestr=dee49b25-20d4-45ae-b968-9a85170cba8a&timestamp=1590723489&url=https://test.shell.yydz100.com/partner/?openId=o7jCDw8qXrGFQ1McqPb5h0Ye2PoQ&headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/rwXoHxiaKh0JDFiaEP3WjuLQsdPOg5CszmSpQczTiaxsicicoMhZmf2hguoicTcdNapBY4gXx8HT97gckc9O62896SlQ/132&nickname=转角的天空╭(╯ε╰)╮&blank=blank&from=groupmessage


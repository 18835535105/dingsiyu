package com.zhidejioayu.center.business.feignclient.smallapp;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.CenterApplication;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: 76339
 * @date: 2020/8/20 17:16:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
@Slf4j
public class BaseSmallAppFeignClientTest {

    @Test
    public void goldCountLimit() {
        String openId = "oTVPO4ogWlL8qS8FZ_SdLFAAmNEU";
        BaseSmallAppFeignClient oTVPO4ogWlL8qS8FZ_sdLFAAmNEU = FeignClientUtil.getBaseSmallAppFeignClient(openId);
        ServerResponse<Object> objectServerResponse = oTVPO4ogWlL8qS8FZ_sdLFAAmNEU.goldCountLimit(openId);
        log.info("objectServerResponse={}", objectServerResponse.toString());
    }
}

package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-07-24
 */
@Slf4j
public class ReadWordServiceTest extends BaseTest {

    @Autowired
    private ReadWordService readWordService;

    @Test
    public void getWordInfo() {
        ServerResponse<Object> response = readWordService.getWordInfo("hello");
        Object data = response.getData();
        if (data != null) {
            log.info("result=[{}]", data.toString());
        }

    }
}

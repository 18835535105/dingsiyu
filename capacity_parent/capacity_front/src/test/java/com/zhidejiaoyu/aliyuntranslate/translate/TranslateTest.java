package com.zhidejiaoyu.aliyuntranslate.translate;

import com.zhidejiaoyu.ZdjyFrontApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/4/3 10:08:08
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class TranslateTest {

    @Test
    public void testTranslate() {
        log.info(Translate.zhToEn("你好").toString());
    }
}

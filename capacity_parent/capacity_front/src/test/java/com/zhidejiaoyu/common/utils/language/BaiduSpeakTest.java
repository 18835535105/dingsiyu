package com.zhidejiaoyu.common.utils.language;

import com.zhidejiaoyu.ZdjyFrontApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = ZdjyFrontApplication.class)
@RunWith(SpringRunner.class)
public class BaiduSpeakTest {

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Test
    public void getLanguagePath() {
        System.out.println(baiduSpeak.getLanguagePath("a/an"));
    }

    @Test
    public void getSentencePath() {
    }

    @Test
    public void getLetterPath() {
    }
}

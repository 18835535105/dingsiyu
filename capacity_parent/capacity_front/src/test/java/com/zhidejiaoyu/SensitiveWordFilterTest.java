package com.zhidejiaoyu;

import com.zhidejiaoyu.student.utils.sensitiveword.SensitiveWordFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author wuchenxi
 * @date 2018/8/14
 */
@Slf4j
public class SensitiveWordFilterTest extends BaseTest {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Test
    public void init() {
    }

    @Test
    public void isContainsSensitiveWord() {
        String txt = "你是个笨蛋还是傻瓜？";
        sensitiveWordFilter.init();
        boolean containsSensitiveWord = sensitiveWordFilter.isContainsSensitiveWord(txt, 1);
        log.info(containsSensitiveWord+"");

        Set<String> sensitiveWord = sensitiveWordFilter.getSensitiveWord(txt, 1);
        sensitiveWord.forEach(log::info);
    }

    @Test
    public void getSensitiveWord() {
    }

    @Test
    public void replaceSensitiveWord() {
    }

    @Test
    public void checkSensitiveWord() {
    }
}
package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-05-23
 */
public class GoodVoiceUtilTest extends BaseTest {

    @Autowired
    private GoodVoiceUtil goodVoiceUtil;

    @Test
    public void testGetWordEvaluationRecord() {
        goodVoiceUtil.getWordEvaluationRecord("hello", "http://192.168.0.2/file/audio/word/897f910a356841788334880a5883c0f31548987547395.mp3");
        goodVoiceUtil.getWordEvaluationRecord("hello", "http://192.168.0.2/file/audio/voice/8e416af64e6b4aa9836ec4fe6f565bf4.mp3");
    }

}

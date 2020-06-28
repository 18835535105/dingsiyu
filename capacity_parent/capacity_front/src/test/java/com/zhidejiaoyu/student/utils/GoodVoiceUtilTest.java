package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.utils.learn.GoodVoiceUtil;

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
        goodVoiceUtil.getWordEvaluationRecord("hello", null);
        goodVoiceUtil.getWordEvaluationRecord("hello", null);
    }

    @Test
    public void getTime(){


    }
}

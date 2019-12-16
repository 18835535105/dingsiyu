package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.utils.PunctuationMatchUtil;
import org.junit.Test;

/**
 * @author wuchenxi
 * @date 2018/7/30
 */
public class PunctuationMatchUtilTest {

    @Test
    public void matchPunctuation() {
        String s = PunctuationMatchUtil.matchPunctuation("T, aThough, the:\"      ...government\".has               " +
                "made some action to punish the businessmen for releasing the polluted sources, they still dare to take " +
                "the risk of earning the biggest profit. If we hurt the environment, it will hurt ourselves, because we " +
                "live together. The polluted environment threatens our health. If we want to live the comfortable life, " +
                "then the only way is to be nice to the environment. ");
        System.out.println(s);
    }
}

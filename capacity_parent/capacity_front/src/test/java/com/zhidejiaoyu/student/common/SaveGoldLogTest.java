package com.zhidejiaoyu.student.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/3/31 10:22:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SaveGoldLogTest {

    @Test
    public void saveRunLog() {

        SaveGoldLog.saveStudyGoldLog(7846L, "金币增加", 23);
        SaveGoldLog.saveStudyGoldLog(7846L, "金币增加", 0);
        SaveGoldLog.saveStudyGoldLog(7846L, "金币减少", -12);
    }
}

package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/9/14 10:44:44
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class FinishGroupOrUnitTest {

    @Resource
    private FinishGroupOrUnit finishGroupOrUnit;

    @Test
    void getMaxFinalLeve() {
        StudentStudyPlanNew maxFinalLeve = finishGroupOrUnit.getMaxFinalLeve(15499L);
        log.info("maxFinalLeve={}", maxFinalLeve.toString());
    }
}

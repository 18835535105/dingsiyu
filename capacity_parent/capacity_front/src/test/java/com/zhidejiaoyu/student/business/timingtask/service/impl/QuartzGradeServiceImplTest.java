package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzGradeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/9/2 18:09:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class QuartzGradeServiceImplTest {

    @Resource
    private QuartzGradeService quartzGradeService;

    @Test
    void updateGrade() {
        quartzGradeService.updateGrade();
    }
}

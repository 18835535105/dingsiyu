package com.dfdz.teacher.business.prizeExchangeList.service;

import com.dfdz.teacher.TeacherApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: wuchenxi
 * @date: 2020/9/9 16:51:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TeacherApplication.class)
public class PrizeExchangeListServiceTest {

    @Resource
    private PrizeExchangeListService prizeExchangeListService;

    @Test
    void deletePrizes() {
        prizeExchangeListService.deletePrizes("o7Luv0ZfivvUJRbuH2jyVZIhL-6I", Collections.singletonList(544));
    }
}

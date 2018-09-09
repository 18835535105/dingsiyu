package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.student.service.QuartzService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author wuchenxi
 * @date 2018/8/24
 */
public class QuartzServiceImplTest extends BaseTest {

    @Autowired
    private QuartzService quartzService;

    @Test
    public void studentUpgrade() {
    }

    @Test
    public void updateNews() {
    }

    @Test
    public void main() {
    }

    @Test
    public void updateRank() {
        quartzService.updateRank();
    }

    @Test
    public void updateWordDay() {
        quartzService.updateWordDay();
    }
}
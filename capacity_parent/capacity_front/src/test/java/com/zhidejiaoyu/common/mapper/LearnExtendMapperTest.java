package com.zhidejiaoyu.common.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2019/12/30 10:07:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LearnExtendMapperTest {

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Test
    public void deleteByUnitIdAndStudyModel() {
        learnExtendMapper.deleteByUnitIdAndStudyModel(14L, "慧记忆");
    }
}

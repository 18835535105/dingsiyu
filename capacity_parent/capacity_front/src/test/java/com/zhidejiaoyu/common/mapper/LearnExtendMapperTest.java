package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudyCapacity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
@Slf4j
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

    @Test
    public void selectStudyCount() {
        Integer count = learnExtendMapper.selectStudyCount(StudyCapacity.builder()
                .studentId(9604L)
                .unitId(111L)
                .wordId(10L)
                .group(10)
                .build(), "慧记忆");
        Assert.assertEquals(null, count);
    }
}

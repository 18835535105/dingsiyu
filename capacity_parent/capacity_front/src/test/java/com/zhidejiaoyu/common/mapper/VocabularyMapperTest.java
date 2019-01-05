package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wuchenxi
 * @date 2019-01-05
 */
@Slf4j
public class VocabularyMapperTest extends BaseTest {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Test
    public void testSelectWordByCourseId() {
        List<String> strings = vocabularyMapper.selectWordByCourseId(2863L, 0, 100);
        log.info("words=[{}]", strings);
    }
}
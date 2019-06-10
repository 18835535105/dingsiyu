package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2019-01-05
 */
@Slf4j
public class SimpleVocabularyMapperTest extends BaseTest {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Test
    public void testSelectWordByCourseId() {
        List<Map<String, String>> strings = vocabularyMapper.selectWordByCourseId(2863L, 0, 100, new ArrayList<>());
        log.info("words=[{}]", strings);
    }

    @Test
    public void testSelectPictureWordFromLearned() {
        vocabularyMapper.selectPictureWordFromLearned(8956L, 3);
    }
}
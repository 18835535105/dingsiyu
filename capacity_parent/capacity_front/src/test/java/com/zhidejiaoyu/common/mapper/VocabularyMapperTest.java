package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.mapper.simple.SimpleVocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.vo.simple.SimpleCapacityVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2019-01-05
 */
@Slf4j
public class VocabularyMapperTest extends BaseTest {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Resource
    private SimpleVocabularyMapper simpleVocabularyMapper;

    @Test
    public void testSelectWordByCourseId() {
        List<Map<String, String>> strings = vocabularyMapper.selectWordByCourseId(2863L, 0, 100, new ArrayList<>());
        log.info("words=[{}]", strings);
    }

    @Test
    public void testSelectPictureWordFromLearned() {
        vocabularyMapper.selectPictureWordFromLearned(8956L, 3);
    }

    @Test
    public void testSelectByPhaseNotInWord() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("word", "hello");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        PageHelper.startPage(1, 10);
        List<Vocabulary> vocabularies = vocabularyMapper.selectByPhaseNotInWord("小学", list);
        log.info("words=[{}]", vocabularies);
    }

    @Test
    public void testSelCountByStudentIdLimitTen() {
        System.out.println(vocabularyMapper.selCountByStudentIdLimitTen(7846L, 1));
    }

    @Test
    public void showWordSimple() {
        SimpleCapacityVo simpleCapacityVo = simpleVocabularyMapper.showWordSimple(15651L, 7846L, 3);
        log.info(simpleCapacityVo.toString());
    }

    @Test
    public void updateReadUrl() {
        List<Vocabulary> vocabularies = simpleVocabularyMapper.selectList(new QueryWrapper<Vocabulary>()
                .notLike("word", "【")
                .notLike("word_chinese", "【")
                .notLike("word", "?")
                .notLike("word", ",")
                .isNotNull("read_url"));
        String word;
        int num = 1;
        for (Vocabulary vocabulary : vocabularies) {
            String word1 = vocabulary.getWord();
            if (word1.contains("...")) {
                word = word1.replace("...", " ").trim();
            } else {
                word = word1.trim();
            }

            File file = new File("/var/tmp/audio/" + word + ".wav");
            if (file.exists()) {
                log.info("更新第{}个单词{}", num, word);
                num++;
                vocabulary.setReadUrl("audio/word_audio/" + word + ".wav");
                simpleVocabularyMapper.updateById(vocabulary);
            }
        }


    }
}

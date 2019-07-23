package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.read.WordInfoVo;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.ReadWordMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.ReadWord;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.language.SimpleYouDaoTranslate;
import com.zhidejiaoyu.student.service.ReadWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuchenxi
 * @date 2019-07-23
 */
@Service
public class ReadWordServiceImpl extends BaseServiceImpl<ReadWordMapper, ReadWord> implements ReadWordService {

    @Autowired
    private ReadWordMapper readWordMapper;

    @Autowired
    private SimpleYouDaoTranslate simpleYouDaoTranslate;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Override
    public ServerResponse<Object> getWordInfo(String word) {
        Vocabulary vocabulary = vocabularyMapper.selectByWord(word);
        WordInfoVo wordInfoVo = simpleYouDaoTranslate.getWordInfoVo(word);
        if (vocabulary == null) {
            wordInfoVo.setCanAddNewWordsBook(false);
        } else {
            wordInfoVo.setWordId(vocabulary.getId());
            wordInfoVo.setCanAddNewWordsBook(true);
        }
        return ServerResponse.createBySuccess(wordInfoVo);
    }

    @Override
    public ServerResponse<Object> addNewWordsBook(Long wordId) {
        Vocabulary vocabulary = vocabularyMapper.selectById(wordId);
        if (vocabulary == null) {
            throw new ServiceException(500, "未查询到 id=[" + wordId+"] 的单词信息！");
        }

        return null;
    }
}

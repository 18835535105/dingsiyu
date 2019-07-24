package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.read.WordInfoVo;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.ReadWordMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.ReadWord;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.language.SimpleYouDaoTranslate;
import com.zhidejiaoyu.student.service.ReadWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;

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
            wordInfoVo.setWordId(null);
            wordInfoVo.setCanAddNewWordsBook(false);
        } else {
            wordInfoVo.setWordId(vocabulary.getId());
            wordInfoVo.setCanAddNewWordsBook(true);
        }
        return ServerResponse.createBySuccess(wordInfoVo);
    }

    @Override
    public ServerResponse<Object> addNewWordsBook(HttpSession session, Long courseId, Long wordId) {
        Vocabulary vocabulary = vocabularyMapper.selectById(wordId);
        if (vocabulary == null) {
            throw new ServiceException(500, "未查询到 id=[" + wordId + "] 的单词信息！");
        }
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        int count = readWordMapper.countByCourseIdAndWordIdAndNotKnow(studentId, courseId, wordId);
        // 如果当前单词在当前课程的生词手册中已经存在，不再进行保存，否则正常保存
        if (count > 0) {
            return ServerResponse.createBySuccess();
        }

        this.saveReadWord(studentId, courseId, wordId);

        return ServerResponse.createBySuccess();
    }

    private void saveReadWord(Long studentId, Long courseId, Long wordId) {
        Date now = new Date();
        Date pushTime = GoldMemoryTime.getGoldMemoryTime(0.12, now);

        ReadWord readWord = new ReadWord();
        readWord.setCourseId(courseId);
        readWord.setWordId(wordId);
        readWord.setStudentId(studentId);
        readWord.setType(1);
        readWord.setMemoryStrength(0.12);
        readWord.setPush(pushTime);
        readWord.setCreateTime(now);
        readWord.setUpdateTime(now);
        this.insert(readWord);

        readWord.setType(2);
        this.insert(readWord);

        readWord.setType(3);
        this.insert(readWord);

        readWord.setType(4);
        this.insert(readWord);
    }
}

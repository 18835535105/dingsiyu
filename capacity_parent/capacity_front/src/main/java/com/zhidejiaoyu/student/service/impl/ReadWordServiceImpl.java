package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.read.NewWordsBookVo;
import com.zhidejiaoyu.common.Vo.read.WordInfoVo;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.ReadWordMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.ReadWord;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.language.SimpleYouDaoTranslate;
import com.zhidejiaoyu.student.service.ReadWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<Object> getWordInfo(HttpSession session, Long courseId, String word) {
        Vocabulary vocabulary = vocabularyMapper.selectByWord(word);
        WordInfoVo wordInfoVo = simpleYouDaoTranslate.getWordInfoVo(word);
        if (vocabulary == null) {
            wordInfoVo.setWordId(null);
            wordInfoVo.setCanAddNewWordsBook(false);
            return ServerResponse.createBySuccess(wordInfoVo);
        }

        wordInfoVo.setWordId(vocabulary.getId());
        Student student = super.getStudent(session);
        int count = readWordMapper.countByCourseIdAndWordIdAndNotKnow(student.getId(), courseId, vocabulary.getId());
        // 如果当前选中的单词存在于当前课程的生词手册中，隐藏“添加到生词手册”按钮
        if (count > 0) {
            wordInfoVo.setCanAddNewWordsBook(false);
        } else {
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

    @Override
    public ServerResponse getNewWordsBook(HttpSession session, Long courseId) {
        Student student = super.getStudent(session);
        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<ReadWord> readWords = readWordMapper.selectByStudentIdCourseId(student.getId(), courseId);
        if (readWords.size() == 0) {
            return ServerResponse.createBySuccess(new ArrayList<>());
        }
        PageInfo<ReadWord> readWordsPageInfo = new PageInfo<>(readWords);

        List<Long> wordIds = readWords.stream().map(ReadWord::getWordId).collect(Collectors.toList());
        List<Vocabulary> vocabularies = vocabularyMapper.selectByWordIds(wordIds);
        List<NewWordsBookVo> vos = vocabularies.stream().map(vocabulary -> {
            NewWordsBookVo newWordsBookVo = new NewWordsBookVo();
            newWordsBookVo.setChinese(vocabulary.getWordChinese());
            newWordsBookVo.setPhonetic(vocabulary.getSoundMark());
            newWordsBookVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));
            newWordsBookVo.setWord(vocabulary.getWord());
            return newWordsBookVo;
        }).collect(Collectors.toList());

        PageInfo<NewWordsBookVo> newWordsBookVoPageInfo = new PageInfo<>();
        newWordsBookVoPageInfo.setList(vos);
        newWordsBookVoPageInfo.setPages(readWordsPageInfo.getPages());
        newWordsBookVoPageInfo.setTotal(readWordsPageInfo.getTotal());

        return ServerResponse.createBySuccess(PageUtil.packagePage(newWordsBookVoPageInfo));
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

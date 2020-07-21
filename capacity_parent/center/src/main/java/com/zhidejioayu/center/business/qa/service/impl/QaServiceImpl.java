package com.zhidejioayu.center.business.qa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.QaKeyWordsMapper;
import com.zhidejiaoyu.common.mapper.center.QaKeyWordsQuestionMapper;
import com.zhidejiaoyu.common.mapper.center.QaQuestionMapper;
import com.zhidejiaoyu.common.pojo.center.QaKeyWords;
import com.zhidejiaoyu.common.pojo.center.QaKeyWordsQuestion;
import com.zhidejiaoyu.common.pojo.center.QaQuestion;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelUtil;
import com.zhidejiaoyu.common.vo.qa.QaKeyInputModel;
import com.zhidejiaoyu.common.vo.qa.QaQuestionInputModel;
import com.zhidejiaoyu.common.vo.qa.QaQuestionKeyInputModel;
import com.zhidejioayu.center.business.qa.service.QaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class QaServiceImpl extends ServiceImpl<QaQuestionMapper, QaQuestion> implements QaService {

    @Resource
    private QaQuestionMapper qaQuestionMapper;
    @Resource
    private QaKeyWordsMapper qaKeyWordsMapper;
    @Resource
    private QaKeyWordsQuestionMapper qaKeyWordsQuestionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importQa(MultipartFile file) {
        List<QaQuestionInputModel> inputQuestion =
                ExcelUtil.readExcel(file, QaQuestionInputModel.class, 1);
        List<QaQuestionKeyInputModel> inputQuestionKey =
                ExcelUtil.readExcel(file, QaQuestionKeyInputModel.class, 2);
        List<QaKeyInputModel> inputKey =
                ExcelUtil.readExcel(file, QaKeyInputModel.class, 3);
        inputKeyModel(inputKey);
        inputQuestionModel(inputQuestion);
        inputQuestionKeyModel(inputQuestionKey);
    }

    private void inputQuestionKeyModel(List<QaQuestionKeyInputModel> inputQuestionKey) {
        inputQuestionKey.forEach(keyQuestion -> {
            QaKeyWordsQuestion qa =
                    qaKeyWordsQuestionMapper.selectByKeyIdAndQuestionId(keyQuestion.getKeyId(), keyQuestion.getQuestionId());
            if (qa == null) {
                qa = new QaKeyWordsQuestion();
                qa.setKeyWordsId(Long.parseLong(keyQuestion.getKeyId()));
                qa.setQuestionId(Long.parseLong(keyQuestion.getQuestionId()));
                qa.setCreateTime(LocalDateTime.now());
                qa.setUpdateTime(LocalDateTime.now());
                qaKeyWordsQuestionMapper.insert(qa);
            }
        });
    }

    private void inputQuestionModel(List<QaQuestionInputModel> inputQuestion) {
        inputQuestion.forEach(question -> {
            QaQuestion qaQuestion = qaQuestionMapper.selectById(question.getNumber());
            if (qaQuestion == null) {
                qaQuestion = new QaQuestion();
                qaQuestion.setAnswer(question.getAnswer());
                qaQuestion.setQuestion(question.getProblem());
                qaQuestion.setAudioUrl(question.getSound());
                qaQuestion.setId(Long.parseLong(question.getNumber()));
                qaQuestion.setCreateTime(LocalDateTime.now());
                qaQuestion.setUpdateTime(LocalDateTime.now());
                qaQuestionMapper.insert(qaQuestion);
            } else {
                qaQuestion.setAnswer(question.getAnswer());
                qaQuestion.setQuestion(question.getProblem());
                qaQuestion.setAudioUrl(question.getSound());
                qaQuestion.setUpdateTime(LocalDateTime.now());
                qaQuestionMapper.updateById(qaQuestion);
            }
        });
    }

    private void inputKeyModel(List<QaKeyInputModel> inputKey) {
        inputKey.forEach(key -> {
            QaKeyWords qaKeyWords = qaKeyWordsMapper.selectById(key.getId());
            if (qaKeyWords == null) {
                qaKeyWords = new QaKeyWords();
                qaKeyWords.setId(Long.parseLong(key.getId()));
                qaKeyWords.setKeyWords(key.getKey());
                qaKeyWords.setCreateTime(LocalDateTime.now());
                qaKeyWords.setUpdateTime(LocalDateTime.now());
                qaKeyWordsMapper.insert(qaKeyWords);
            } else {
                qaKeyWords.setKeyWords(key.getKey());
                qaKeyWords.setUpdateTime(LocalDateTime.now());
                qaKeyWordsMapper.updateById(qaKeyWords);
            }
        });
    }
}

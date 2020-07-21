package com.zhidejioayu.center.business.wechat.qy.qa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.*;
import com.zhidejiaoyu.common.pojo.center.*;
import com.zhidejiaoyu.common.utils.excelUtil.easyexcel.ExcelUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.qa.QaKeyInputModel;
import com.zhidejiaoyu.common.vo.qa.QaQuestionInputModel;
import com.zhidejiaoyu.common.vo.qa.QaQuestionKeyInputModel;
import com.zhidejiaoyu.common.vo.wechat.qy.QaVO;
import com.zhidejioayu.center.business.wechat.qy.qa.service.QaService;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QaServiceImpl extends ServiceImpl<QaQuestionMapper, QaQuestion> implements QaService {

    @Resource
    private QaQuestionMapper qaQuestionMapper;

    @Resource
    private QaKeyWordsMapper qaKeyWordsMapper;

    @Resource
    private QaKeyWordsQuestionMapper qaKeyWordsQuestionMapper;

    @Resource
    private QaAutoLearnMapper qaAutoLearnMapper;

    @Resource
    private QaUnknownMapper qaUnknownMapper;

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

    @Override
    public ServerResponse<Object> getQa(String question) {
        QaVO qaVO = new QaVO();
        List<QaVO.OtherAnswers> answers = this.getAutoLearnAnswers(question, qaVO);
        boolean notEmpty = CollectionUtils.isNotEmpty(answers);

        List<Map<String, Object>> qaQuestions = qaQuestionMapper.selectKeyWordsAndQuestion();
        List<QaKeywordsInfo> qaKeywordsInfos = new ArrayList<>();
        qaQuestions.forEach(qaQuestion -> {
            String keyWords = String.valueOf(qaQuestion.get("keyWords"));
            if (!question.contains(keyWords)) {
                return;
            }
            int id = Integer.parseInt(String.valueOf(qaQuestion.get("id")));
            String answer = String.valueOf(qaQuestion.get("answer"));

            // 如果从自学习表查询出了答案，把根据关键词查询出的答案放到其他可能答案里
            if (notEmpty) {
                QaVO.OtherAnswers otherAnswers = new QaVO.OtherAnswers();
                otherAnswers.setId(id);
                otherAnswers.setAnswer(answer);
                answers.add(otherAnswers);
                return;
            }

            // 从自学习表未查询出答案
            QaKeywordsInfo qaKeywordsInfo = new QaKeywordsInfo();
            qaKeywordsInfo.setAnswer(answer);
            qaKeywordsInfo.setId(id);
            qaKeywordsInfo.setKeywords(keyWords);
            qaKeywordsInfo.setCount(StringUtils.countMatches(question, keyWords));
            qaKeywordsInfos.add(qaKeywordsInfo);
        });

        if (CollectionUtils.isEmpty(answers)) {
            if (CollectionUtils.isEmpty(qaKeywordsInfos)) {
                // 没有关键词能够匹配到用户的问题，记录到未知问题
                saveUnknown(question);
                return ServerResponse.createByError(401, "未能查询到用户的问题答案！");
            }
            qaKeywordsInfos.sort(Comparator.comparingInt(QaKeywordsInfo::getCount));
            int size = qaKeywordsInfos.size();
            for (int i = 0; i < size; i++) {
                QaKeywordsInfo qaKeywordsInfo = qaKeywordsInfos.get(i);
                if (i == 0) {
                    qaVO.setAnswer(qaKeywordsInfo.getAnswer());
                    qaVO.setId(qaKeywordsInfo.getId());
                } else {
                    QaVO.OtherAnswers otherAnswers1 = new QaVO.OtherAnswers();
                    otherAnswers1.setAnswer(qaKeywordsInfo.getAnswer());
                    otherAnswers1.setId(qaKeywordsInfo.getId());
                    answers.add(otherAnswers1);
                }
            }
        }

        qaVO.setOtherAnswers(answers);

        return ServerResponse.createBySuccess(qaVO);
    }

    private void saveUnknown(String question) {
        qaUnknownMapper.insert(QaUnknown.builder()
                .createTime(new Date())
                .question(question)
                .updateTime(new Date())
                .build());
    }

    @Override
    public void saveQaAutoStudy(String question, Long questionId) {
        qaAutoLearnMapper.insert(QaAutoLearn.builder()
                .createTime(new Date())
                .question(question)
                .questionId(questionId)
                .updateTime(new Date())
                .build());
    }

    @Override
    public void saveUnknownQuestion(String question) {
        this.saveUnknown(question);
    }

    /**
     * 关键词出现次数信息
     */
    @Data
    static
    class QaKeywordsInfo {
        private String keywords;
        /**
         * 问题id
         */
        private Integer id;
        private String answer;
        /**
         * 关键词出现次数
         */
        private Integer count;
    }

    public static void main(String[] args) {
        String question = "大明白李糖心大明白";
        System.out.println(StringUtils.countMatches(question, "大明白"));
        System.out.println(StringUtils.countMatches(question, "李糖心"));
    }

    /**
     * 获取自学习表需要封装的数据
     *
     * @param question
     * @param qaVO
     * @return
     */
    private List<QaVO.OtherAnswers> getAutoLearnAnswers(String question, QaVO qaVO) {
        List<QaVO.OtherAnswers> otherAnswers = new ArrayList<>(16);
        List<Map<String, Object>> qaAutoLearns = qaAutoLearnMapper.selectByQuestion(question);
        int size = qaAutoLearns.size();
        if (size > 0) {
            for (int i = 0; i < qaAutoLearns.size(); i++) {
                String answer = String.valueOf(qaAutoLearns.get(i).get("answer"));
                int id = Integer.parseInt(String.valueOf(qaAutoLearns.get(i).get("id")));
                if (i == 0) {
                    qaVO.setAnswer(answer);
                    qaVO.setId(id);
                } else {
                    QaVO.OtherAnswers otherAnswers1 = new QaVO.OtherAnswers();
                    otherAnswers1.setAnswer(answer);
                    otherAnswers1.setId(id);
                    otherAnswers.add(otherAnswers1);
                }
            }
        }
        return otherAnswers;
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

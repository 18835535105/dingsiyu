package com.zhidejiaoyu.student.BaseUtil.SaveModel;

import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SaveSentenceData {

    private final String STUDYMODEL1 ="例句翻译";
    private final String STUDYMODEL2 ="例句听力";
    private final String STUDYMODEL3 ="例句默写";

    @Resource
    private SentenceMapper sentenceMapper;
    /**
     * 返回记忆达到黄金记忆点的例句信息
     *
     * @param studyCapacity 记忆追踪
     * @param plan          进度
     * @param firstStudy    是否是第一次学习
     * @param sentenceCount 当前单元例句总数
     * @param type
     * @return 例句翻译学习页面展示信息
     */

    public ServerResponse<SentenceTranslateVo> returnGoldWord(StudyCapacity studyCapacity, Long plan, boolean firstStudy,
                                                              Long sentenceCount, Integer type,String studyModel) {
        SentenceTranslateVo sentenceTranslateVo;
        // 例句翻译
        if (STUDYMODEL1.equals(studyModel)) {
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            // 计算当前例句的记忆强度
            double memoryStrength = studyCapacity.getMemoryStrength();
           // sentenceTranslateVo = getSentenceTranslateVo(plan, firstStudy, sentenceCount, type, sentence);
            sentenceTranslateVo.setStudyNew(false);
            sentenceTranslateVo.setMemoryStrength(memoryStrength);
            sentenceTranslateVo.setCourseId(studyCapacity.getCourseId().intValue());
            sentenceTranslateVo.setUnitId(studyCapacity.getUnitId().intValue());
        } else if (STUDYMODEL2.equals(studyModel)) {
            // 例句听力
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            double memoryStrength = studyCapacity.getMemoryStrength();
            //sentenceTranslateVo = this.getListenSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
            sentenceTranslateVo.setCourseId(studyCapacity.getCourseId().intValue());
            sentenceTranslateVo.setUnitId(studyCapacity.getUnitId().intValue());
        } else if(STUDYMODEL3.equals(studyModel)){
            // 例句默写
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            double memoryStrength = studyCapacity.getMemoryStrength();
            //sentenceTranslateVo = this.getSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
        }
        return ServerResponse.createBySuccess(sentenceTranslateVo);
    }
}

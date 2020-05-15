package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.UnitVocabularyNewMapper;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 验证是否可学习单词图鉴模块
 *
 * @author: wuchenxi
 * @date: 2020/1/3 10:39:39
 */
@Component
public class JudgeWordPicture {

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    /**
     * 验证是否可以进行单词图鉴流程
     *
     * @param dto
     * @param studyFlowNew
     * @return true：可以正常执行流程；false：不执行单词图鉴流程
     */
    public boolean judgeWordPicture(NodeDto dto, StudyFlowNew studyFlowNew) {
        // 如果下个节点不是单词图鉴模块，执行正常流程
        String studyModel = "单词图鉴";
        if (!studyFlowNew.getModelName().contains(studyModel)) {
            return true;
        }

        Long unitId = dto.getUnitId();
        if (unitId == null) {
            return true;
        }

        // 当前单元含有图片的单词个数，如果大于零，执行正常流程，否则跳过单词图鉴模块
        int pictureCount = unitVocabularyNewMapper.countPicture(unitId, dto.getGroup());
        return pictureCount > 0;
    }
}

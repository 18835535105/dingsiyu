package com.zhidejiaoyu.student.syntax.savelearn;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.pojo.KnowledgePoint;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.HttpUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 保存学习记录
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 09:37
 */
@Component
public class SaveLearnInfo {

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxMemoryStrength syntaxMemoryStrength;

    public void updateNotFirstLearn(Boolean known, Learn learned, int type) {
        // 非首次学习
        StudyCapacity studyCapacity = studyCapacityMapper.selectByLearn(learned, type);
        if (Objects.isNull(studyCapacity)) {
            // 保存记忆追踪
            this.initStudyCapacity(learned, type, knowledgePointMapper, studyCapacityMapper);
        } else {
            studyCapacity.setMemoryStrength(syntaxMemoryStrength.getMemoryStrength(studyCapacity.getMemoryStrength(), known));
            studyCapacity.setPush(GoldMemoryTime.getGoldMemoryTime(studyCapacity.getMemoryStrength(), new Date()));
            studyCapacity.setUpdateTime(new Date());
            if (!known) {
                studyCapacity.setFaultTime(studyCapacity.getFaultTime() + 1);
            }
            studyCapacityMapper.updateById(studyCapacity);
        }

        learned.setStudyCount(learned.getStudyCount() + 1);
        learned.setUpdateTime(new Date());
        learnMapper.updateById(learned);
    }

    /**
     * 第一次学习，新增学习记录、记忆追踪信息
     *
     * @param learn
     * @param known
     */
    public void saveFirstLearn(Learn learn, Boolean known) {
        // 首次学习
        learn.setLearnTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(1);
        learn.setUpdateTime(new Date());
        if (known) {
            learn.setStatus(1);
            learn.setFirstIsKnown(1);
        } else {
            // 保存记忆追踪
            this.initStudyCapacity(learn, StudyCapacityTypeConstant.LEARN_SYNTAX, knowledgePointMapper, studyCapacityMapper);

            learn.setStatus(0);
            learn.setFirstIsKnown(0);
        }
        learnMapper.insert(learn);
    }

    private void initStudyCapacity(Learn learn, int type, KnowledgePointMapper knowledgePointMapper, StudyCapacityMapper studyCapacityMapper) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectById(learn.getVocabularyId());
        studyCapacityMapper.insert(StudyCapacity.builder()
                .courseId(learn.getCourseId())
                .unitId(learn.getUnitId())
                .studentId(learn.getStudentId())
                .faultTime(1)
                .memoryStrength(0.38)
                .push(GoldMemoryTime.getGoldMemoryTime(0.38, new Date()))
                .updateTime(new Date())
                .type(type)
                .word(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getName())
                .wordChinese(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getContent())
                .wordId(learn.getVocabularyId())
                .build());
    }
}

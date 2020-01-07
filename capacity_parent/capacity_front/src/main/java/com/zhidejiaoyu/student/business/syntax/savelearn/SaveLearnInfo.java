package com.zhidejiaoyu.student.business.syntax.savelearn;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
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
    private TeacherMapper teacherMapper;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxMemoryStrength syntaxMemoryStrength;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private SaveData saveData;

    /**
     * 保存语法学习记录
     *
     * @param learn
     * @param known
     * @param type
     * @return
     */
    public ServerResponse<Object> saveSyntax(Student student, Learn learn, Boolean known, int type, int easyOrHard, Long flowId, String studyModel) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(learn.getStudentId(), learn.getUnitId(), easyOrHard,4);
        Integer integer = learnExtendMapper.selectCountByLearnIdAndWordIdAndType(learnNew.getId(), learn.getVocabularyId(), type);
        LearnExtend extend = learnExtendMapper.selectByLearnIdAndWordIdAndType(learnNew.getId(), learn.getVocabularyId(), type);
        if (integer == 0) {
            LearnExtend learnExtend = new LearnExtend();
            learnExtend.setLearnId(learnNew.getId());
            learnExtend.setSchoolAdminId(Long.parseLong(teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId()).toString()));
            StudyFlowNew flow = saveData.getCurrentStudyFlowById(flowId);
            learnExtend.setFlowName(flow.getFlowName());
            learnExtend.setFirstStudyTime(new Date());
            learnExtend.setStudyModel(studyModel);
            this.saveFirstLearn(learnExtend, learnNew, known, type);
        } else {
            this.updateNotFirstLearn(known, extend, learnNew, type);
        }
        if (!known) {
            saveData.saveErrorLearnLog(learnNew.getUnitId(), type, easyOrHard, studyModel, learnNew, extend.getWordId());
        }

        return ServerResponse.createBySuccess();
    }



    private void updateNotFirstLearn(Boolean known, LearnExtend learned, LearnNew learnNew, int type) {
        // 非首次学习
        StudyCapacity studyCapacity = studyCapacityMapper.selectByLearn(learnNew, learned, type);
        if (Objects.isNull(studyCapacity)) {
            // 保存记忆追踪
            this.initStudyCapacity(learned, learnNew, type);
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
        learnExtendMapper.updateById(learned);
    }

    /**
     * 第一次学习，新增学习记录、记忆追踪信息
     *
     * @param learn
     * @param known
     * @param type
     */
    private void saveFirstLearn(LearnExtend learn, LearnNew learnNew, Boolean known, int type) {
        // 首次学习
        learn.setLearnTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(1);
        learn.setUpdateTime(new Date());
        if (known) {
            learn.setStatus(1);
            learn.setFirstIsKnow(1);
        } else {
            // 保存记忆追踪
            this.initStudyCapacity(learn, learnNew, type);
            learn.setStatus(0);
            learn.setFirstIsKnow(0);
        }
        learnExtendMapper.insert(learn);
    }

    private void initStudyCapacity(LearnExtend learnExtend, LearnNew learn, int type) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectById(learnExtend.getWordId());
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
                .wordId(learnExtend.getWordId())
                .build());

    }
}

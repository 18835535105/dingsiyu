package com.zhidejiaoyu.student.business.syntax.savelearn;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.CurrentDayOfStudyUtil;
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

    /**
     * 保存语法学习记录
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> saveSyntax(SaveSyntaxDTO dto) {
        Student student = dto.getStudent();
        Integer type = dto.getType();
        Boolean know = dto.getKnown();
        Long vocabularyId = dto.getVocabularyId();

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), dto.getUnitId(), dto.getEasyOrHard(), 4);
        LearnExtend extend = learnExtendMapper.selectByLearnIdAndWordIdAndType(learnNew.getId(), vocabularyId);

        if (extend == null) {
            LearnExtend learnExtend = new LearnExtend();
            learnExtend.setWordId(vocabularyId);
            learnExtend.setLearnId(learnNew.getId());
            learnExtend.setSchoolAdminId(Long.parseLong(teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId()).toString()));
            learnExtend.setFirstStudyTime(new Date());
            learnExtend.setStudyModel(dto.getStudyModel());
            this.saveFirstLearn(learnExtend, learnNew, know, type);
        } else {
            this.updateNotFirstLearn(know, extend, learnNew, type);
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
     * @param learnExtend
     * @param known
     * @param type
     */
    private void saveFirstLearn(LearnExtend learnExtend, LearnNew learnNew, Boolean known, int type) {
        // 首次学习
        learnExtend.setLearnTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        learnExtend.setStudyCount(1);
        learnExtend.setUpdateTime(new Date());
        if (known) {
            learnExtend.setStatus(1);
            learnExtend.setFirstIsKnow(1);
        } else {
            // 保存记忆追踪
            this.initStudyCapacity(learnExtend, learnNew, type);
            learnExtend.setStatus(0);
            learnExtend.setFirstIsKnow(0);
            CurrentDayOfStudyUtil.saveSessionCurrent(RedisKeysConst.ERROR_SYNTAX, learnExtend.getWordId());
        }
        learnExtendMapper.insert(learnExtend);
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
                .group(1)
                .build());

    }
}

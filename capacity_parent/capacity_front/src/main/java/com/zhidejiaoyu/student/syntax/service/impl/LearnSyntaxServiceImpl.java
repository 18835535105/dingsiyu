package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.service.LearnSyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @Date: 2019/10/31 17:43
 */
@Service
public class LearnSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements LearnSyntaxService {

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private SyntaxMemoryDifficulty syntaxMemoryDifficulty;

    @Resource
    private SyntaxMemoryStrength syntaxMemoryStrength;

    @Override
    public ServerResponse getLearnSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.LEARN_SYNTAX);
        int total = syntaxRedisOpt.getTotalKnowledgePointWithUnitId(unitId);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.LEARN_SYNTAX)
                .build();

        ServerResponse studyCapacity = this.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse knowledgePoint = this.getNewKnowledgePoint(dto);
        if (!Objects.isNull(knowledgePoint)) {
            return knowledgePoint;
        }

        // 获取没有达到黄金记忆点的生知识点
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        ServerResponse serverResponse = this.packageNeedViewLearnSyntax(nextStudyCapacity, dto);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入选语法模块
        this.packageStudentStudySyntax(unitId, student);

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    @Override
    public ServerResponse saveLearnSyntax(Learn learn, Boolean known) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        learn.setStudentId(student.getId());
        learn.setStudyModel(SyntaxModelNameConstant.LEARN_SYNTAX);
        Learn learned = learnMapper.selectLearnedSyntaxByUnitIdAndStudyModelAndWordId(learn);
        if (Objects.isNull(learned)) {
            saveFirstLearn(learn, known, knowledgePointMapper, studyCapacityMapper, learnMapper);
        } else {
            updateNotFirstLearn(known, learned, StudyCapacityTypeConstant.LEARN_SYNTAX, studyCapacityMapper, syntaxMemoryStrength, learnMapper, knowledgePointMapper);
        }

        return ServerResponse.createBySuccess();
    }


    static void updateNotFirstLearn(Boolean known, Learn learned, int type, StudyCapacityMapper studyCapacityMapper,
                                    SyntaxMemoryStrength syntaxMemoryStrength, LearnMapper learnMapper,
                                    KnowledgePointMapper knowledgePointMapper) {
        // 非首次学习
        StudyCapacity studyCapacity = studyCapacityMapper.selectByLearn(learned, type);
        if (Objects.isNull(studyCapacity)) {
            // 保存记忆追踪
            initStudyCapacity(learned, type, knowledgePointMapper, studyCapacityMapper);
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
     * @param knowledgePointMapper
     * @param studyCapacityMapper
     * @param learnMapper
     */
    static void saveFirstLearn(Learn learn, Boolean known, KnowledgePointMapper knowledgePointMapper,
                               StudyCapacityMapper studyCapacityMapper, LearnMapper learnMapper) {
        // 首次学习
        learn.setLearnTime((Date) HttpUtil.getHttpSession().getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(1);
        learn.setUpdateTime(new Date());
        if (known) {
            learn.setStatus(1);
            learn.setFirstIsKnown(1);
        } else {
            // 保存记忆追踪
            initStudyCapacity(learn, StudyCapacityTypeConstant.LEARN_SYNTAX, knowledgePointMapper, studyCapacityMapper);

            learn.setStatus(0);
            learn.setFirstIsKnown(0);
        }
        learnMapper.insert(learn);
    }

    private static void initStudyCapacity(Learn learn, int type, KnowledgePointMapper knowledgePointMapper, StudyCapacityMapper studyCapacityMapper) {
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

    /**
     * 初始化下个模块的数据
     *
     * @param unitId
     * @param student
     */
    private void packageStudentStudySyntax(Long unitId, Student student) {
        packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.SELECT_SYNTAX, studentStudySyntaxMapper, syntaxUnitMapper);
    }

    /**
     * @param unitId
     * @param student
     * @param nextModelName            下一个模块名称
     * @param studentStudySyntaxMapper
     * @param syntaxUnitMapper
     */
    static void packageStudentStudySyntax(Long unitId, Student student, String nextModelName,
                                          StudentStudySyntaxMapper studentStudySyntaxMapper, SyntaxUnitMapper syntaxUnitMapper) {
        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (!Objects.isNull(studentStudySyntax)) {
            studentStudySyntax.setModel(nextModelName);
            studentStudySyntax.setUpdateTime(new Date());
            studentStudySyntaxMapper.updateById(studentStudySyntax);
        } else {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            studentStudySyntaxMapper.insert(StudentStudySyntax.builder()
                    .updateTime(new Date())
                    .model(nextModelName)
                    .unitId(unitId)
                    .studentId(student.getId())
                    .courseId(!Objects.isNull(syntaxUnit) ? syntaxUnit.getCourseId() : null)
                    .build());
        }
    }

    /**
     * 获取下一个知识点内容
     *
     * @param dto
     * @return
     */
    private ServerResponse getNewKnowledgePoint(NeedViewDTO dto) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectNextByUnitId(dto.getStudentId(), dto.getUnitId());
        if (!Objects.isNull(knowledgePoint)) {
            return ServerResponse.createBySuccess(this.packageNewKnowledgePoint(dto, knowledgePoint));
        }
        return null;
    }

    /**
     * 封装新学习的知识点内容
     *
     * @param dto
     * @param knowledgePoint
     * @return
     */
    private LearnSyntaxVO packageNewKnowledgePoint(NeedViewDTO dto, KnowledgePoint knowledgePoint) {
        return LearnSyntaxVO.builder()
                .id(knowledgePoint.getId())
                .content(knowledgePoint.getContent())
                .syntaxName(knowledgePoint.getName())
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(true)
                .memoryDifficult(0)
                .memoryStrength(0)
                .build();
    }

    /**
     * 获取需要复习的知识点内容
     *
     * @param dto
     * @return
     */
    private ServerResponse getNeedView(NeedViewDTO dto) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectLargerThanGoldTimeWithStudentIdAndUnitId(dto);
        return this.packageNeedViewLearnSyntax(studyCapacity, dto);
    }

    private ServerResponse packageNeedViewLearnSyntax(StudyCapacity studyCapacity, NeedViewDTO dto) {
        return packageNeedViewLearnSyntax(studyCapacity, dto, syntaxMemoryDifficulty);
    }

    static ServerResponse packageNeedViewLearnSyntax(StudyCapacity studyCapacity, NeedViewDTO dto, SyntaxMemoryDifficulty syntaxMemoryDifficulty) {
        if (!Objects.isNull(studyCapacity)) {
            return ServerResponse.createBySuccess(LearnSyntaxVO.builder()
                    .id(studyCapacity.getWordId())
                    .content(studyCapacity.getWordChinese())
                    .syntaxName(studyCapacity.getWord())
                    .total(dto.getTotal())
                    .plan(Math.min(dto.getPlan(), dto.getTotal()))
                    .studyNew(false)
                    .memoryStrength((int) Math.round(studyCapacity.getMemoryStrength()))
                    .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                    .build());
        }
        return null;
    }
}

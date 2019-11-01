package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.SelectSyntaxVO;
import com.zhidejiaoyu.common.Vo.syntax.game.GameSelect;
import com.zhidejiaoyu.common.Vo.syntax.game.GameVO;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.dto.syntax.NeedViewDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.memorydifficulty.SyntaxMemoryDifficulty;
import com.zhidejiaoyu.common.study.memorystrength.SyntaxMemoryStrength;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.service.SelectSyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @Date: 2019/10/31 17:52
 */
@Service
public class SelectSyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SelectSyntaxService {

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
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private SyntaxMemoryStrength syntaxMemoryStrength;

    @Override
    public ServerResponse getSelectSyntax(Long unitId) {
        HttpUtil.getHttpSession().setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.SELECT_SYNTAX);
        int total = syntaxRedisOpt.getTotalSyntaxContentWithUnitId(unitId, SyntaxModelNameConstant.SELECT_SYNTAX);

        // 如果有需要复习的，返回需要复习的数据
        NeedViewDTO dto = NeedViewDTO.builder()
                .unitId(unitId)
                .studentId(student.getId())
                .plan(plan)
                .total(total)
                .type(StudyCapacityTypeConstant.SELECT_SYNTAX)
                .build();

        ServerResponse studyCapacity = this.getNeedView(dto);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse newSyntaxTopic = this.getNewSyntaxTopic(dto);
        if (!Objects.isNull(newSyntaxTopic)) {
            return newSyntaxTopic;
        }

        // 获取没有达到黄金记忆点的语法内容
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(dto);
        ServerResponse serverResponse = this.packageSelectSyntaxNeedView(dto, nextStudyCapacity);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入语法游戏模块
        this.packageStudentStudySyntax(unitId, student);

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);

    }

    @Override
    public ServerResponse saveSelectSyntax(Learn learn, Boolean known) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        learn.setStudentId(student.getId());
        learn.setStudyModel(SyntaxModelNameConstant.SELECT_SYNTAX);
        Learn learned = learnMapper.selectLearnedSyntaxByUnitIdAndStudyModelAndWordId(learn);
        if (Objects.isNull(learned)) {
            LearnSyntaxServiceImpl.saveFirstLearn(learn, known, knowledgePointMapper, studyCapacityMapper, learnMapper);
        } else {
            LearnSyntaxServiceImpl.updateNotFirstLearn(known, learned, StudyCapacityTypeConstant.SELECT_SYNTAX, studyCapacityMapper, syntaxMemoryStrength, learnMapper, knowledgePointMapper);
        }

        return ServerResponse.createBySuccess();
    }

    private ServerResponse getNewSyntaxTopic(NeedViewDTO dto) {
        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectNextByUnitIdAndType(dto.getStudentId(), dto.getUnitId(), SyntaxModelNameConstant.SELECT_SYNTAX);
        if (!Objects.isNull(syntaxTopic)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(syntaxTopic.getId());
            LearnSyntaxVO knowledgePoint1 = this.packageNewKnowledgePoint(dto, knowledgePoint);
            GameVO selects = this.getGameVO(dto.getUnitId(), syntaxTopic);
            return ServerResponse.createBySuccess(this.packageSelectSyntaxVO(knowledgePoint1, selects));
        }
        return null;
    }

    /**
     * 初始化下个模块的数据
     *
     * @param unitId
     * @param student
     */
    private void packageStudentStudySyntax(Long unitId, Student student) {
        LearnSyntaxServiceImpl.packageStudentStudySyntax(unitId, student, SyntaxModelNameConstant.GAME, studentStudySyntaxMapper, syntaxUnitMapper);
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
        if (Objects.equals(dto.getType(), StudyCapacityTypeConstant.LEARN_SYNTAX)) {
            return LearnSyntaxServiceImpl.packageNeedViewLearnSyntax(studyCapacity, dto, syntaxMemoryDifficulty);
        }
        if (Objects.equals(dto.getType(), StudyCapacityTypeConstant.SELECT_SYNTAX)) {
            return this.packageSelectSyntaxNeedView(dto, studyCapacity);
        }

        return null;
    }

    /**
     * 获取选语法需要复习的数据
     *
     * @param dto
     * @param studyCapacity
     * @return
     */
    private ServerResponse packageSelectSyntaxNeedView(NeedViewDTO dto, StudyCapacity studyCapacity) {
        if (!Objects.isNull(studyCapacity)) {
            KnowledgePoint knowledgePoint = knowledgePointMapper.selectByTopicId(studyCapacity.getWordId());

            SyntaxTopic syntaxTopic = syntaxTopicMapper.selectById(studyCapacity.getWordId());

            LearnSyntaxVO knowledgePoint1 = this.getSelectSyntaxKnowledgePoint(dto, studyCapacity, knowledgePoint);
            GameVO selects = getGameVO(dto.getUnitId(), syntaxTopic);
            return ServerResponse.createBySuccess(this.packageSelectSyntaxVO(knowledgePoint1, selects));
        }
        return null;
    }

    /**
     * 封装选语法需要返回的数据
     *
     * @param knowledgePoint
     * @param selects
     * @return
     */
    private Object packageSelectSyntaxVO(LearnSyntaxVO knowledgePoint, GameVO selects) {
        SelectSyntaxVO.SelectSyntaxVOBuilder builder = SelectSyntaxVO.builder()
                .id(knowledgePoint.getId())
                .total(knowledgePoint.getTotal())
                .plan(knowledgePoint.getPlan())
                .studyNew(knowledgePoint.getStudyNew())
                .memoryDifficult(knowledgePoint.getMemoryDifficult())
                .memoryStrength(knowledgePoint.getMemoryStrength())
                .selects(selects);

        knowledgePoint.setId(null);
        knowledgePoint.setTotal(null);
        knowledgePoint.setPlan(null);
        knowledgePoint.setStudyNew(null);
        knowledgePoint.setMemoryDifficult(null);
        knowledgePoint.setMemoryStrength(null);

        return builder.knowledgePoint(knowledgePoint).build();
    }

    /**
     * 封装试题选项
     *
     * @param unitId
     * @param syntaxTopic 当前语法题
     * @return
     */
    private GameVO getGameVO(Long unitId, SyntaxTopic syntaxTopic) {
        return new GameVO(syntaxTopic.getTopic().replace("$&$", "___"), this.packageSelectAnswer(syntaxTopic, unitId));
    }

    /**
     * 封装选语法中知识点内容
     *
     * @param dto
     * @param studyCapacity
     * @param knowledgePoint
     * @return
     */
    private LearnSyntaxVO getSelectSyntaxKnowledgePoint(NeedViewDTO dto, StudyCapacity studyCapacity, KnowledgePoint knowledgePoint) {
        return LearnSyntaxVO.builder()
                .id(studyCapacity.getWordId())
                .content(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getContent())
                .syntaxName(Objects.isNull(knowledgePoint) ? null : knowledgePoint.getName())
                .total(dto.getTotal())
                .plan(Math.min(dto.getPlan(), dto.getTotal()))
                .studyNew(false)
                .memoryStrength((int) Math.round(studyCapacity.getMemoryStrength()))
                .memoryDifficult(syntaxMemoryDifficulty.getMemoryDifficulty(studyCapacity))
                .build();
    }

    /**
     * 封装选语法选项
     *
     * @param syntaxTopic
     * @param unitId
     * @return
     */
    private List<GameSelect> packageSelectAnswer(SyntaxTopic syntaxTopic, Long unitId) {
        List<SyntaxTopic> syntaxTopics = syntaxTopicMapper.selectByUnitId(unitId);
        Collections.shuffle(syntaxTopics);
        // 三个错误选项
        List<GameSelect> select = syntaxTopics.stream()
                .filter(syntaxTopic1 -> !Objects.equals(syntaxTopic1.getId(), syntaxTopic.getId()))
                .limit(3)
                .map(syntaxTopic1 -> new GameSelect(syntaxTopic1.getAnswer(), false))
                .collect(Collectors.toList());
        // 一个正确选项
        select.add(new GameSelect(syntaxTopic.getAnswer(), true));
        Collections.shuffle(select);
        return select;
    }
}
